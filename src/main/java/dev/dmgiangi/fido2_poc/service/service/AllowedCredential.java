package dev.dmgiangi.fido2_poc.service.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AllowedCredential {
    private String id;
    private String type;
}
