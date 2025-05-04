package dev.dmgiangi.fido2_poc.controller;

import dev.dmgiangi.fido2_poc.service.entra.GraphService;
import dev.dmgiangi.fido2_poc.service.entra.dto.EntraUser;
import dev.dmgiangi.fido2_poc.service.entra.dto.Fido2AuthenticationOptions;
import dev.dmgiangi.fido2_poc.service.entra.dto.Fido2AuthenticationRequest;
import dev.dmgiangi.fido2_poc.service.entra.dto.Fido2AuthenticationResult;
import dev.dmgiangi.fido2_poc.service.service.AuthenticationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@AllArgsConstructor
@RequestMapping("/authentication")
public class AuthenticationController {
    private static final Map<UUID, WebAuthNAuthenticationResponse> AUTHENTICATION_RESPONSES = new ConcurrentHashMap<>();
    private final GraphService graphService;
    private final AuthenticationService authenticationService;

    @PostMapping("/initiate")
    public ResponseEntity<WebAuthNAuthenticationResponse> initiateAuthentication(
            @Valid @RequestBody UserAuthentication userAuthentication) {
        return graphService
                .getUsers()
                .stream()
                .filter(user -> user.getEmployeeId().equals(userAuthentication.getEmployeeId().toString()))
                .findFirst()
                .map(this::getFido2AuthenticationOption)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private WebAuthNAuthenticationResponse getFido2AuthenticationOption(EntraUser user) {
        final var userPrincipalName = user.getUserPrincipalName();
        final var fido2AuthenticationOption = authenticationService.getFido2AuthenticationOption(userPrincipalName);

        final var webAuthNAuthenticationResponse = new WebAuthNAuthenticationResponse(
                UUID.randomUUID(),
                userPrincipalName,
                Instant.now().plusSeconds(600),
                fido2AuthenticationOption);

        AUTHENTICATION_RESPONSES.put(webAuthNAuthenticationResponse.getAuthRequestId(), webAuthNAuthenticationResponse);

        return webAuthNAuthenticationResponse;
    }

    @PostMapping("/complete/{auth-request-id}")
    public ResponseEntity<Fido2AuthenticationResult> completeAuthentication(
            @PathVariable("auth-request-id") UUID authRequestId,
            @Valid @RequestBody Fido2AuthenticationRequest authenticationResponse) {
        final var webAuthNAuthenticationResponse = AUTHENTICATION_RESPONSES.get(authRequestId);
        if (webAuthNAuthenticationResponse == null)
            return ResponseEntity.notFound().build();

        final var result = authenticationService.verifyFido2Authentication(
                webAuthNAuthenticationResponse.getUserPrincipalName(),
                authenticationResponse);

        if (result.isSuccess()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class UserAuthentication {
        @NotNull
        private Integer employeeId;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WebAuthNAuthenticationResponse {
        @NotNull
        private UUID authRequestId;
        @NotNull
        private String userPrincipalName;
        @NotNull
        private Instant challengeTimeoutDateTime;
        @NotNull
        private Fido2AuthenticationOptions publicKey;
    }
}
