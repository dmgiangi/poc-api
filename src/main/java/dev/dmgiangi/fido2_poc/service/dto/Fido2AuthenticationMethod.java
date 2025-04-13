package dev.dmgiangi.fido2_poc.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class Fido2AuthenticationMethod {
    private String id;
    private String displayName;
    private String createdDateTime;
    private String aaGuid;
    private String model;
    private List<String> attestationCertificates;
    private String attestationLevel;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(String createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public String getAaGuid() {
        return aaGuid;
    }

    public void setAaGuid(String aaGuid) {
        this.aaGuid = aaGuid;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<String> getAttestationCertificates() {
        return attestationCertificates;
    }

    public void setAttestationCertificates(List<String> attestationCertificates) {
        this.attestationCertificates = attestationCertificates;
    }

    public String getAttestationLevel() {
        return attestationLevel;
    }

    public void setAttestationLevel(String attestationLevel) {
        this.attestationLevel = attestationLevel;
    }
}
