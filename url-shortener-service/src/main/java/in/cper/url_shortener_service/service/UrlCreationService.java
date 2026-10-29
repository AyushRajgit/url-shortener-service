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
import java.util.Base64;

@Service
public class UrlCreationService {

    @Value("${platformUrl}")
    private String platformUrl;

    private UrlRepository urlRepository;
    private Mapper mapper = new Mapper();
    private UrlEncoder urlEncoder = new UrlEncoder();

    @Autowired
    public UrlCreationService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    public UrlResponseDTO shortenUrl(UrlRequestDTO request) {
        String originalUrl = request.getOriginalUrl();

        // generating random short code
        String shortCode = urlEncoder.generateShortCode();

        while (urlRepository.existsByShortCode(shortCode) || originalUrl == null || originalUrl.isEmpty()) {
            shortCode = urlEncoder.generateShortCode();
        }

        UrlEntity urlEntity = mapper.mapIntoEntity(originalUrl, shortCode);
        urlRepository.save(urlEntity);

        String shortUrl = urlEncoder.generateShortURL(shortCode, platformUrl);
        UrlResponseDTO urlResponseDTO = mapper.mapIntoUrlResponseDTO(originalUrl, shortCode, shortUrl);

        return urlResponseDTO;
    }

}
