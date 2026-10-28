package in.cper.url_shortener_service.mapper;

import in.cper.url_shortener_service.dto.UrlResponseDTO;
import in.cper.url_shortener_service.entity.UrlEntity;

public class Mapper {

    public UrlEntity mapIntoEntity(String originalUrl, String shortCode) {
        UrlEntity urlEntity = new UrlEntity(
                originalUrl,
                shortCode
        );

        return urlEntity;
    }

    public UrlResponseDTO mapIntoUrlResponseDTO(String originalUrl, String shortCode, String shortUrl) {
        UrlResponseDTO urlResponseDTO = new UrlResponseDTO(
                shortUrl,
                shortCode,
                originalUrl
        );

        return urlResponseDTO;
    }


}
