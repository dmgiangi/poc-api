package dev.dmgiangi.fido2_poc.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
public class RestExceptionAdvice extends ResponseEntityExceptionHandler {
    @Value("${app.backend-url}")
    private String backendUrl;

    @ExceptionHandler(RedirectException.class)
    public ResponseEntity<ErrorResponse> handleRedirectException(RedirectException ex) {
        final var redirect = ex.getRedirectPath();

        log.info("Redirecting to: {}", backendUrl + redirect);

        final var body = new ErrorResponse(ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.SEE_OTHER)
                .header("Location", backendUrl + redirect)
                .body(body);
    }

    @ExceptionHandler(HttpStatusException.class)
    public ResponseEntity<ErrorResponse> handleHttpStatusException(HttpStatusException ex) {

        log.error("Si è verificata un'eccezione con HTTP status {}: {}", ex.getStatus(), ex.getMessage(), ex);

        final var body = new ErrorResponse(ex.getMessage());
        final var status = HttpStatus.resolve(ex.getStatus());

        return new ResponseEntity<>(body, status == null ? HttpStatus.INTERNAL_SERVER_ERROR : status);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleHttpStatusException(RuntimeException ex) {

        log.error("Si è verificata un'eccezione senza HTTP status: {}", ex.getMessage(), ex);

        final var body = new ErrorResponse(ex.getMessage());
        final var status = HttpStatus.INTERNAL_SERVER_ERROR;

        return new ResponseEntity<>(body, status);
    }
}
