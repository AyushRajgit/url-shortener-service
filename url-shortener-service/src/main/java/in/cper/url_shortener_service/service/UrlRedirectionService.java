package in.cper.url_shortener_service.service;

import in.cper.url_shortener_service.entity.UrlEntity;
import in.cper.url_shortener_service.exception.ShortCodeNotFoundException;
import in.cper.url_shortener_service.exception.UrlHasExpiredException;
import in.cper.url_shortener_service.repository.UrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UrlRedirectionService {

    private UrlRepository urlRepository;

    @Autowired
    public UrlRedirectionService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    public String fetchURL(String shortCode) {
        UrlEntity urlEntity = urlRepository.findByShortCode(shortCode).orElseThrow(
                () -> new ShortCodeNotFoundException("Short code not found : " + shortCode)
        );

        validateIsUrlExpired(urlEntity);

        String orginalUrl = urlEntity.getOriginalUrl();

        return orginalUrl;
    }

    private boolean validateIsUrlExpired(UrlEntity urlEntity) {
        Instant expiry = urlEntity.getExpireAt();

        if (Instant.now().isAfter(expiry)) {
            throw new UrlHasExpiredException("This shortened URL has expired and is no longer available :(");
        }

        return true;
    }

}
