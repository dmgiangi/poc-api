package dev.dmgiangi.fido2_poc.service.entra.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Fido2AuthenticationMethod {
    private String id;
    private String displayName;
    private String createdDateTime;
    private String aaGuid;
    private String model;
    private List<String> attestationCertificates;
    private String attestationLevel;
    private PublicKeyCredential publicKeyCredential;
}
