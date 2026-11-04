package in.cper.url_shortener_service.service;

import in.cper.url_shortener_service.entity.UrlEntity;
import in.cper.url_shortener_service.exception.ShortCodeNotFoundException;
import in.cper.url_shortener_service.exception.UrlHasExpiredException;
import in.cper.url_shortener_service.repository.UrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisConnectionFailureException;
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
        String redisCachedUrl = null;

        try {
            redisCachedUrl = stringRedisTemplate.opsForValue().get(redisKey);
        } catch (RedisConnectionFailureException e) {
            System.out.println("Redis unavailable, falling back to PostgreSQL");
        }

        if (redisCachedUrl != null) {
            int separator = redisCachedUrl.indexOf("|");
            long expiryTime = 0;
            String cachedUrl = "";

            if (separator > 0) {
                try {
                    expiryTime = Long.parseLong(redisCachedUrl.substring(0, separator));
                } catch (NumberFormatException e) {
                    System.out.println("Invalid expiry timestamp in Redis.");
                }

                cachedUrl = redisCachedUrl.substring(separator + 1);

                if (Instant.now().toEpochMilli() < expiryTime) {
                    System.out.println("Redis Cached URL used");
                    return cachedUrl;
                }

                try {
                    stringRedisTemplate.delete(redisKey);
                    System.out.println("Expired Redis cache entry removed.");
                } catch (RedisConnectionFailureException e) {
                    System.out.println("Unable to remove expired Redis cache entry");
                }

                System.out.println("Redis cache expired, falling back to PostgreSQL");
            }
        }

        UrlEntity urlEntity = urlRepository.findByShortCode(shortCode).orElseThrow(
                () -> new ShortCodeNotFoundException("Short code not found : " + shortCode)
        );

        validateIsUrlExpired(urlEntity);
        String orginalUrl = urlEntity.getOriginalUrl();

        Instant expiry = urlEntity.getExpireAt();
        Duration ttl = Duration.between(Instant.now(), expiry);

        if (ttl.isPositive()) {
            try {
                String cacheValue = expiry.toEpochMilli() + "|" + orginalUrl;
                stringRedisTemplate.opsForValue().set(redisKey, cacheValue, ttl);
            } catch (RedisConnectionFailureException e) {
                System.out.println("Redis unavailable. Skipping cache population.");
            }
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
