package dev.dmgiangi.fido2_poc.service.entra.dto.webauthn;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Rp {
    private String id;
    private String name;
}
