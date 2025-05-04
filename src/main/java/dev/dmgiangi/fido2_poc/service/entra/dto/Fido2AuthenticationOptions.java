package dev.dmgiangi.fido2_poc.service.entra.dto;

import dev.dmgiangi.fido2_poc.service.service.AllowedCredential;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * DTO class representing FIDO2 authentication options.
 * This class replaces the Map<String, Object> that was previously used.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Fido2AuthenticationOptions {
    /**
     * The challenge for the authentication.
     */
    private String challenge;

    /**
     * The timeout for the authentication in milliseconds.
     */
    private Integer timeout;

    /**
     * The Relying Party ID.
     */
    private String rpId;

    /**
     * The list of allowed credentials.
     */
    private List<AllowedCredential> allowCredentials;

    /**
     * The user verification preference.
     */
    private String userVerification;
}