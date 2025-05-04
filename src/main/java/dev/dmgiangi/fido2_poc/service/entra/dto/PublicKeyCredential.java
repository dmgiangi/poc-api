package dev.dmgiangi.fido2_poc.service.entra.dto;

import dev.dmgiangi.fido2_poc.service.entra.dto.webauthn.AuthenticatorAttestationResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PublicKeyCredential {
    private String id;
    private AuthenticatorAttestationResponse response;
    private Map<String, Object> clientExtensionResults;
}
