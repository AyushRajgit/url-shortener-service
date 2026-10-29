package in.cper.url_shortener_service.utils;

import org.springframework.beans.factory.annotation.Value;

import java.security.SecureRandom;

public class UrlEncoder {

    @Value("${Encoding.Secret.Key}")
    private String CHARACTERS;

    @Value("${Encoding.Short.Code.Length}")
    private long SHORT_CODE_LENGTH;

    private static final SecureRandom RANDOM = new SecureRandom();

    public String generateShortCode() {
        StringBuilder shortCode = new StringBuilder();

        for (int i = 0; i < SHORT_CODE_LENGTH; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            shortCode.append(CHARACTERS.charAt(index));
        }

        return shortCode.toString();
    }

    public String generateShortURL(String shortCode, String platformUrl) {
        StringBuilder shortUrl = new StringBuilder(platformUrl);
        shortUrl.append(shortCode);
        return shortUrl.toString();
    }
}
