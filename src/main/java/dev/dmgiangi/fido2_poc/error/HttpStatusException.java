package dev.dmgiangi.fido2_poc.error;

import lombok.Getter;

public class HttpStatusException extends RuntimeException {
    @Getter
    private final int status;

    public HttpStatusException(String message, int status) {
        super(message);
        this.status = status;
    }
}
