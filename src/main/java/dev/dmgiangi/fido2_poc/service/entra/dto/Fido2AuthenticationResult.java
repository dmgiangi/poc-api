package dev.dmgiangi.fido2_poc.service.entra.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO class representing the result of a FIDO2 authentication.
 * This class replaces the Map<String, Object> that was previously used.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Fido2AuthenticationResult {
    /**
     * Whether the authentication was successful.
     */
    private boolean success;

    /**
     * A message describing the result of the authentication.
     */
    private String message;

    /**
     * The user that was authenticated, if successful.
     */
    private EntraUser user;

    /**
     * Static factory method for creating a successful result.
     *
     * @param user The authenticated user
     * @return A successful authentication result
     */
    public static Fido2AuthenticationResult success(EntraUser user) {
        return new Fido2AuthenticationResult(true, "Authentication successful", user);
    }

    /**
     * Static factory method for creating a failed result.
     *
     * @param errorMessage The error message
     * @return A failed authentication result
     */
    public static Fido2AuthenticationResult failure(String errorMessage) {
        return new Fido2AuthenticationResult(false, errorMessage, null);
    }
}