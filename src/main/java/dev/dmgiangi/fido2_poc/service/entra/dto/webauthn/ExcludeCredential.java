package dev.dmgiangi.fido2_poc.service.entra.dto.webauthn;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExcludeCredential {
    private String id;
    private String type;
    private List<String> transports;
}
