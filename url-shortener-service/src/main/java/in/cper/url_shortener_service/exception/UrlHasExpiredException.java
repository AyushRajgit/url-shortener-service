package in.cper.url_shortener_service.exception;

public class UrlHasExpiredException extends RuntimeException {
    public UrlHasExpiredException(String message) {
        super(message);
    }
}
