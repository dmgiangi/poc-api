package dev.dmgiangi.fido2_poc.service.entra.dto;

import dev.dmgiangi.fido2_poc.service.entra.dto.webauthn.PublicKeyCredentialCreationOptions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CredentialCreationOptions {
    private Instant challengeTimeoutDateTime;
    private PublicKeyCredentialCreationOptions publicKey;
}
