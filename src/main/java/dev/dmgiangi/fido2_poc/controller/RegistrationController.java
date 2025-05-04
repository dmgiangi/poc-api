package dev.dmgiangi.fido2_poc.controller;

import dev.dmgiangi.fido2_poc.service.entra.GraphService;
import dev.dmgiangi.fido2_poc.service.entra.dto.EntraUser;
import dev.dmgiangi.fido2_poc.service.entra.dto.Fido2AuthenticationMethod;
import dev.dmgiangi.fido2_poc.service.entra.dto.webauthn.PublicKeyCredentialCreationOptions;
import dev.dmgiangi.fido2_poc.service.entra.dto.webauthn.Rp;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@AllArgsConstructor
@RequestMapping("/registration")
public class RegistrationController {
    private static final Map<UUID, WebAuthNInitiationResponse> REGISTRATION_REQUEST = new ConcurrentHashMap<>();
    private final GraphService graphService;

    @PostMapping("/initiate")
    public ResponseEntity<WebAuthNInitiationResponse> initiateRegistration(
            @Valid @RequestBody UserVerification userVerification) {
        return graphService
                .getUsers()
                .stream()
                .filter(user -> user.getEmployeeId().equals(userVerification.getEmployeeId().toString()))
                //.filter(user -> userVerification.getDateOfBirth().equals(user.getEmployeeHireDate()))
                .findFirst()
                .map(this::getFido2CreationOption)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private WebAuthNInitiationResponse getFido2CreationOption(EntraUser user) {
        final var userPrincipalName = user.getUserPrincipalName();
        final var fido2CreationOption = graphService.getFido2CreationOption(userPrincipalName);
        fido2CreationOption.getPublicKey().setRp(new Rp("api.dmgiangi.dev", "FIDO2 POC"));
        fido2CreationOption.getPublicKey().setTimeout(60000);
        fido2CreationOption.getPublicKey().getAuthenticatorSelection().setAuthenticatorAttachment("platform");
        fido2CreationOption
                .getPublicKey()
                .setExtensions(
                        Map.of("hmacCreateSecret", true,
                                "enforceCredentialProtectionPolicy", true,
                                "credentialProtectionPolicy", "userVerificationRequired"));


        final var webAuthNInitiationResponse = new WebAuthNInitiationResponse(
                UUID.randomUUID(),
                userPrincipalName,
                fido2CreationOption.getChallengeTimeoutDateTime(),
                fido2CreationOption.getPublicKey());

        REGISTRATION_REQUEST.put(webAuthNInitiationResponse.getCreationRequestId(), webAuthNInitiationResponse);

        return webAuthNInitiationResponse;
    }

    @PostMapping("/complete/{creation-request-id}")
    public ResponseEntity<?> completeRegistration(
            @PathVariable("creation-request-id") UUID creationRequestId,
            @Valid @RequestBody Fido2AuthenticationMethod fido2AuthenticationMethod) {
        final var webAuthNInitiationResponse = REGISTRATION_REQUEST.get(creationRequestId);
        if (webAuthNInitiationResponse == null)
            return ResponseEntity.notFound().build();

        final var response = graphService.createFido2Methods(
                webAuthNInitiationResponse.getUserPrincipalName(),
                fido2AuthenticationMethod);
        return ResponseEntity.ok(response);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class UserVerification {
        @NotNull
        private Integer employeeId;
        @NotNull
        private LocalDate dateOfBirth;
        @NotNull
        private String verificationCode;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WebAuthNInitiationResponse {
        @NotNull
        private UUID creationRequestId;
        @NotNull
        private String userPrincipalName;
        @NotNull
        private Instant challengeTimeoutDateTime;
        @NotNull
        private PublicKeyCredentialCreationOptions publicKey;
    }
}
