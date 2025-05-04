package dev.dmgiangi.fido2_poc.service.entra.dto.webauthn;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PublicKeyCredentialCreationOptions {
    private String odataType;
    private Rp rp;
    private PublicKeyCredentialUserEntity user;
    private String challenge;
    private List<PubKeyCredParam> pubKeyCredParams;
    private Integer timeout;
    private List<ExcludeCredential> excludeCredentials;
    private AuthenticatorSelection authenticatorSelection;
    private String attestation;
    private Map<String, Object> extensions;
}
