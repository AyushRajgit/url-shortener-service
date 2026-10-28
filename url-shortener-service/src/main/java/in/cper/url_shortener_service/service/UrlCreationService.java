package in.cper.url_shortener_service.service;

import in.cper.url_shortener_service.dto.UrlRequestDTO;
import in.cper.url_shortener_service.dto.UrlResponseDTO;
import in.cper.url_shortener_service.entity.UrlEntity;
import in.cper.url_shortener_service.mapper.Mapper;
import in.cper.url_shortener_service.repository.UrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class UrlCreationService {

    @Value("${platformUrl}")
    private String platformUrl;


    private UrlRepository urlRepository;
    private Mapper mapper = new Mapper();

    @Autowired
    public UrlCreationService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    public UrlResponseDTO shortenUrl(UrlRequestDTO request) {
        String originalUrl = request.getOriginalUrl();
        String shortCode = generateShortCode(originalUrl);

        UrlEntity urlEntity = mapper.mapIntoEntity(originalUrl, shortCode);
        urlRepository.save(urlEntity);

        String shortUrl = generateShortURL(shortCode);
        UrlResponseDTO urlResponseDTO = mapper.mapIntoUrlResponseDTO(originalUrl, shortCode, shortUrl);

        return urlResponseDTO;
    }

    public String generateShortCode(String originalUrl) {
        String hash = Base64.getUrlEncoder().encodeToString(originalUrl.getBytes(StandardCharsets.UTF_8));
        return hash.substring(0, 11);
    }

    public String generateShortURL(String shortCode) {
        StringBuilder shortUrl = new StringBuilder(platformUrl);
        shortUrl.append(shortCode);
        return shortUrl.toString();
    }

}
