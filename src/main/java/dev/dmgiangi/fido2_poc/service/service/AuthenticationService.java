package dev.dmgiangi.fido2_poc.service.service;

import dev.dmgiangi.fido2_poc.error.HttpStatusException;
import dev.dmgiangi.fido2_poc.error.RedirectException;
import dev.dmgiangi.fido2_poc.service.entra.GraphService;
import dev.dmgiangi.fido2_poc.service.entra.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    public static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final Map<String, ValidCredentials> LOGIN_REQUEST = new ConcurrentHashMap<>();
    private final GraphService graphService;

    public Fido2AuthenticationOptions getFido2AuthenticationOption(String userId) {
        List<Fido2AuthenticationMethod> fido2Methods = graphService.getFido2Methods(userId);

        if (fido2Methods.isEmpty())
            throw new RedirectException("No Fido2Method found, please register first", "register.html");


        byte[] challenge = new byte[32];
        SECURE_RANDOM.nextBytes(challenge);
        final var challengeBase64 = Base64.getUrlEncoder().withoutPadding().encodeToString(challenge);

        final var allowCredentials = fido2Methods
                .stream()
                .map(method -> method.getPublicKeyCredential().getId())
                .map(method -> new AllowedCredential(method, "public-key"))
                .toList();

        final var expire = Instant.now().plusSeconds(300000);
        allowCredentials
                .stream()
                .map(AllowedCredential::getId)
                .forEach(credentialId -> LOGIN_REQUEST.put(credentialId, new ValidCredentials(challengeBase64, expire)));

        return new Fido2AuthenticationOptions(
                challengeBase64,
                300000,
                "api.dmgiangi.dev",
                allowCredentials,
                "preferred");
    }

    public Fido2AuthenticationResult verifyFido2Authentication(String userId, Fido2AuthenticationRequest authenticationResponse) {
        final var credentialId = authenticationResponse.getId();
        final var validCredentials = LOGIN_REQUEST.get(credentialId);
        if (validCredentials == null)
            throw new HttpStatusException("Unauthorized", 401);

        if (validCredentials.expire().isBefore(Instant.now()))
            throw new HttpStatusException("Unauthorized", 401);

        final var challenge = validCredentials.challenge();
        final var fido2Method = graphService.getFido2Method(userId, credentialId);
        if (fido2Method == null)
            throw new HttpStatusException("Unauthorized", 401);

        final var publicKeyCredential = fido2Method.getPublicKeyCredential();

        try {
            // Decode the client data JSON from Base64
            final var clientDataJSON = new String(Base64.getUrlDecoder().decode(authenticationResponse.getResponse().getClientDataJSON()));

            // Extract the challenge from the client data JSON
            // This is a simple string search, in a production environment you would use a JSON parser
            int challengeStartIndex = clientDataJSON.indexOf("\"challenge\":\"") + 13;
            int challengeEndIndex = clientDataJSON.indexOf("\"", challengeStartIndex);
            String responseChallenge = clientDataJSON.substring(challengeStartIndex, challengeEndIndex);

            // Verify that the challenge in the response matches the stored challenge
            if (!responseChallenge.equals(challenge)) {
                return Fido2AuthenticationResult.failure("Challenge verification failed");
            }

            // Get the user information
            List<EntraUser> users = graphService.getUsers();
            EntraUser user = users.stream()
                    .filter(u -> u.getId().equals(userId))
                    .findFirst()
                    .orElseThrow(() -> new HttpStatusException("User not found", 404));

            // Return a successful result
            return Fido2AuthenticationResult.success(user);
        } catch (Exception e) {
            return Fido2AuthenticationResult.failure("Authentication failed: " + e.getMessage());
        }
    }

    private record ValidCredentials(String challenge, Instant expire) {
    }
}
