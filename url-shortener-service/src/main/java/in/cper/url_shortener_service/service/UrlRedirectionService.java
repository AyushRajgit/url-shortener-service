package in.cper.url_shortener_service.service;

import in.cper.url_shortener_service.entity.UrlEntity;
import in.cper.url_shortener_service.exception.ShortCodeNotFound;
import in.cper.url_shortener_service.repository.UrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UrlRedirectionService {

    private UrlRepository urlRepository;

    @Autowired
    public UrlRedirectionService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    public String fetchURL(String shortCode) {
        UrlEntity urlEntity = urlRepository.findByShortCode(shortCode).orElseThrow(
                () -> new ShortCodeNotFound("Short code not found : " + shortCode)
        );

        String orginalUrl = urlEntity.getOriginalUrl();

        return orginalUrl;
    }

}
