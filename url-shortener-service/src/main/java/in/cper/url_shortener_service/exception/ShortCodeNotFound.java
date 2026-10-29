package in.cper.url_shortener_service.exception;

public class ShortCodeNotFound extends RuntimeException {
    public ShortCodeNotFound(String message) {
        super(message);
    }
}
