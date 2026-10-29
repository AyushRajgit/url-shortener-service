package in.cper.url_shortener_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ValidationExceptionDTO {
    private int status;
    private String error;
    private Map<String,String> message;
    private String path;
    private LocalDateTime timestamp;
}
