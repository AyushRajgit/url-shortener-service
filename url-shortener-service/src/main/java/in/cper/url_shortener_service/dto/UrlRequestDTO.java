package in.cper.url_shortener_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UrlRequestDTO {
    @NotBlank
    private String originalUrl;
}
