
package in.cper.url_shortener_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    private final RestClient restClient;
    private final String apiUrl;
    private final String apiKey;
    private final String senderEmail;
    private final String senderName;

    public EmailService(
            @Value("${brevo.api.url}") String apiUrl,
            @Value("${brevo.api.key}") String apiKey,
            @Value("${brevo.sender.email}") String senderEmail,
            @Value("${brevo.sender.name}") String senderName
    ) {
        this.restClient = RestClient.builder().build();
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
        this.senderEmail = senderEmail;
        this.senderName = senderName;
    }

    public void sendUrlEmail(String email, String shortUrl, String originalUrl) {

        Map<String, Object> body = Map.of(
                "sender", Map.of(
                        "name", senderName,
                        "email", senderEmail
                ),
                "to", List.of(Map.of("email", email)),
                "subject", "Your Shortened URL is Ready!",
                "textContent",
                "Hello!\n\n" +
                        "Your URL has been successfully shortened.\n\n" +
                        "Original URL: " + originalUrl + "\n" +
                        "Short URL: " + shortUrl + "\n\n" +
                        "Thank you for using our URL Shortener!"
        );

        restClient.post()
                .uri(apiUrl)
                .header("api-key", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .toBodilessEntity();
    }
}