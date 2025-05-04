package dev.dmgiangi.fido2_poc.service.entra;

import dev.dmgiangi.fido2_poc.service.entra.dto.CredentialCreationOptions;
import dev.dmgiangi.fido2_poc.service.entra.dto.EntraUser;
import dev.dmgiangi.fido2_poc.service.entra.dto.Fido2AuthenticationMethod;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@AllArgsConstructor
public class GraphService {
    private final RestTemplate restTemplate;

    public boolean checkConnection() {
        final var users = getUsers();
        return !users.isEmpty();
    }

    public List<EntraUser> getUsers() {
        final var response = restTemplate
                .getForObject("/v1.0/users?$select=givenName,surname,userPrincipalName,id,mail,employeeId,employeeHireDate", MultipleUserResponse.class);

        if (response == null)
            return List.of();

        return response
                .getValue()
                .stream()
                .filter(u -> u.getUserPrincipalName() != null)
                .filter(u -> u.getUserPrincipalName().endsWith("@dmgiangi.dev"))
                .toList();
    }

    public Fido2AuthenticationMethod createFido2Methods(String userId, Fido2AuthenticationMethod fido2AuthenticationMethod) {
        return restTemplate
                .postForObject(
                        "/beta/users/" + userId + "/authentication/fido2Methods",
                        fido2AuthenticationMethod,
                        Fido2AuthenticationMethod.class);
    }

    public CredentialCreationOptions getFido2CreationOption(String userId) {
        return restTemplate
                .getForObject(
                        "/beta/users/" + userId + "/authentication/fido2Methods/creationOptions(challengeTimeoutInMinutes=10)",
                        CredentialCreationOptions.class);
    }

    public List<Fido2AuthenticationMethod> getFido2Methods(String userId) {
        return restTemplate
                .getForObject(
                        "/beta/users/" + userId + "/authentication/fido2Methods",
                        MultipleFidoResponse.class)
                .getValue();
    }

    public Fido2AuthenticationMethod getFido2Method(String userId, String fidoId) {
        return restTemplate
                .getForObject(
                        "/beta/users/" + userId + "/authentication/fido2Methods/" + fidoId,
                        SingleFidoResponse.class)
                .getValue();
    }

    public void deleteFido2Method(String userId, String fidoId) {
        restTemplate
                .delete("/beta/users/" + userId + "/authentication/fido2Methods/" + fidoId);
    }

    @Setter
    @Getter
    @NoArgsConstructor
    private static class MultipleFidoResponse {
        private List<Fido2AuthenticationMethod> value;
    }

    @Setter
    @Getter
    @NoArgsConstructor
    private static class SingleFidoResponse {
        private Fido2AuthenticationMethod value;
    }

    @Setter
    @Getter
    @NoArgsConstructor
    public static class MultipleUserResponse {
        private List<EntraUser> value;
    }
}
