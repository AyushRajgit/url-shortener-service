package in.cper.url_shortener_service.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UrlResponseDTO {
    private String shortUrl;
    private String shortCode;
    private String originalUrl;
}
