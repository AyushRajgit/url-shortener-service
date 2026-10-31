package in.cper.url_shortener_service.service;

import in.cper.url_shortener_service.entity.UrlEntity;
import in.cper.url_shortener_service.exception.ShortCodeNotFoundException;
import in.cper.url_shortener_service.exception.UrlHasExpiredException;
import in.cper.url_shortener_service.repository.UrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class UrlRedirectionService {

    private StringRedisTemplate stringRedisTemplate;
    private UrlRepository urlRepository;

    @Autowired
    public UrlRedirectionService(UrlRepository urlRepository, StringRedisTemplate stringRedisTemplate) {
        this.urlRepository = urlRepository;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public String fetchURL(String shortCode) {
        String redisKey = "url:" + shortCode;
        String redisCachedUrl = stringRedisTemplate.opsForValue().get(redisKey);

        if (redisCachedUrl != null) {
            System.out.println("Redis Cached URL used");
            return redisCachedUrl;
        }

        UrlEntity urlEntity = urlRepository.findByShortCode(shortCode).orElseThrow(
                () -> new ShortCodeNotFoundException("Short code not found : " + shortCode)
        );

        validateIsUrlExpired(urlEntity);
        String orginalUrl = urlEntity.getOriginalUrl();

        Instant expiry = urlEntity.getExpireAt();
        Duration ttl = Duration.between(Instant.now(), expiry);

        if (ttl.isPositive()) {
            stringRedisTemplate.opsForValue().set(redisKey, orginalUrl, ttl);
        }

        return orginalUrl;
    }

    private void validateIsUrlExpired(UrlEntity urlEntity) {
        Instant expiry = urlEntity.getExpireAt();

        if (!Instant.now().isBefore(expiry)) {
            throw new UrlHasExpiredException("This shortened URL has expired and is no longer available :(");
        }
    }

}
