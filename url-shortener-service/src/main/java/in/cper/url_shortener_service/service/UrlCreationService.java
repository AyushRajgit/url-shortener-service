package in.cper.url_shortener_service.service;

import in.cper.url_shortener_service.dto.UrlRequestDTO;
import in.cper.url_shortener_service.dto.UrlResponseDTO;
import in.cper.url_shortener_service.entity.UrlEntity;
import in.cper.url_shortener_service.mapper.Mapper;
import in.cper.url_shortener_service.repository.UrlRepository;
import in.cper.url_shortener_service.utils.UrlEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

@Service
public class UrlCreationService {

    @Value("${platformUrl}")
    private String platformUrl;

    @Value("${Short.Code.Expiry.Days}")
    private int EXPIRY_DAYS;

    private UrlRepository urlRepository;
    private Mapper mapper = new Mapper();
    private UrlEncoder urlEncoder;

    @Autowired
    public UrlCreationService(UrlRepository urlRepository, UrlEncoder urlEncoder) {
        this.urlRepository = urlRepository;
        this.urlEncoder = urlEncoder;
    }

    public UrlResponseDTO shortenUrl(UrlRequestDTO request) {
        String originalUrl = request.getOriginalUrl();

        // generating random short code
        String shortCode = null;

        while (true) {
            shortCode = urlEncoder.generateShortCode();
            System.out.println(shortCode);
            if (!urlRepository.existsByShortCode(shortCode)) {
                break;
            }
        }

        UrlEntity urlEntity = mapper.mapIntoEntity(originalUrl, shortCode);
        setUpCreationAndExpiry(urlEntity);
        urlRepository.save(urlEntity);

        String shortUrl = urlEncoder.generateShortURL(shortCode, platformUrl);
        UrlResponseDTO urlResponseDTO = mapper.mapIntoUrlResponseDTO(originalUrl, shortCode, shortUrl);

        return urlResponseDTO;
    }

    private void setUpCreationAndExpiry(UrlEntity urlEntity) {
        Instant createdAt = Instant.now();
        Instant expiryAt = createdAt.plus(EXPIRY_DAYS, ChronoUnit.DAYS);

        urlEntity.setCreatedAt(createdAt);
        urlEntity.setExpireAt(expiryAt);
    }

}
