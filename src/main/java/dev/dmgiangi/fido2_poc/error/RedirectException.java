package dev.dmgiangi.fido2_poc.error;

import lombok.Getter;

public class RedirectException extends RuntimeException {
    @Getter
    private final String redirectPath;

    public RedirectException(String message, String redirectPath) {
        super(message);
        this.redirectPath = redirectPath;
    }
}
