package dev.dmgiangi.fido2_poc.service.entra.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO class representing a FIDO2 authentication request.
 * This class replaces the Map<String, Object> that was previously used.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Fido2AuthenticationRequest {
    /**
     * The ID of the credential.
     */
    @NotNull
    private String id;

    /**
     * The raw ID of the credential (base64 encoded).
     */
    private String rawId;

    /**
     * The type of the credential (usually "public-key").
     */
    private String type;

    /**
     * The authenticator response containing clientDataJSON and authenticatorData.
     */
    private AuthenticatorResponse response;

    /**
     * Inner class representing the authenticator response.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthenticatorResponse {
        /**
         * The client data JSON (base64 encoded).
         */
        private String clientDataJSON;

        /**
         * The authenticator data (base64 encoded).
         */
        private String authenticatorData;

        /**
         * The signature (base64 encoded).
         */
        private String signature;

        /**
         * The user handle (base64 encoded).
         */
        private String userHandle;
    }
}