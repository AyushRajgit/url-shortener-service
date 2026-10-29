package in.cper.url_shortener_service.exception;

import in.cper.url_shortener_service.dto.ExceptionDTO;
import in.cper.url_shortener_service.dto.ValidationExceptionDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UrlHasExpiredException.class)
    public ResponseEntity<ExceptionDTO> handleUrlHasExpired(UrlHasExpiredException urlHasExpiredException, HttpServletRequest request) {
        ExceptionDTO exceptionDTO = new ExceptionDTO(
                HttpStatus.GONE.value(),
                HttpStatus.GONE.getReasonPhrase(),
                urlHasExpiredException.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.GONE).body(exceptionDTO);
    }

    @ExceptionHandler(ShortCodeNotFoundException.class)
    public ResponseEntity<ExceptionDTO> handleOriginalUrlNotFound(ShortCodeNotFoundException shortCodeNotFoundException, HttpServletRequest request) {
        ExceptionDTO exceptionDTO = new ExceptionDTO(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                shortCodeNotFoundException.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionDTO);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationExceptionDTO> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String,String> listErrorMessage = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach((error)->listErrorMessage.put(error.getField(), error.getDefaultMessage()));

        ValidationExceptionDTO validationExceptionDTO = new ValidationExceptionDTO(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                listErrorMessage,
                request.getRequestURI(),
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(validationExceptionDTO);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ExceptionDTO> handleRuntimeException(RuntimeException runtimeException, HttpServletRequest request) {
        ExceptionDTO exceptionDTO = new ExceptionDTO(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                runtimeException.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionDTO);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionDTO> handleException(Exception exception, HttpServletRequest request) {
        ExceptionDTO exceptionDTO = new ExceptionDTO(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                exception.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionDTO);
    }
}
