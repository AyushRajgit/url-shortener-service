package in.cper.url_shortener_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UrlRequestDTO {
    @NotBlank(message = "Url cannot be null/empty/white-space")
    @URL(message = "Please enter a valid url")
    private String originalUrl;
}
