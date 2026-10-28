package in.cper.url_shortener_service.exception;

public class OriginalUrlNotFound extends RuntimeException {
    public OriginalUrlNotFound(String message) {
        super(message);
    }
}
