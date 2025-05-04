package dev.dmgiangi.fido2_poc.service.entra.dto.webauthn;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticatorSelection {
    private String authenticatorAttachment;
    private Boolean requireResidentKey;
    private String userVerification;
}
