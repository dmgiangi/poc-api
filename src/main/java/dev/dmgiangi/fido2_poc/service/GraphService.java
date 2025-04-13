package dev.dmgiangi.fido2_poc.service;

import dev.dmgiangi.fido2_poc.service.dto.Fido2AuthenticationMethod;
import dev.dmgiangi.fido2_poc.service.dto.User;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class GraphService {
    private final RestTemplate restTemplate;

    public GraphService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean checkConnection() {
        final var users = getUsers();
        return !users.isEmpty();
    }

    public List<User> getUsers() {
        final var users = restTemplate
                .getForObject("/v1.0/users?$select=givenName,surname,userPrincipalName,id,mail,employeeId,employeeHireDate", UserResponse.class)
                .getValue();

        return users
                .stream()
                .filter(u -> u.getUserPrincipalName() != null)
                .filter(u -> u.getUserPrincipalName().endsWith("@dmgiangi.dev"))
                .toList();
    }

    public List<Fido2AuthenticationMethod> getFido2Methods(String userId) {
        return restTemplate
                .getForObject(
                        "/beta/users/" + userId + "/authentication/fido2Methods",
                        MultipleFidoResponse.class)
                .getValue();
    }

    public Fido2AuthenticationMethod getFido2Methods(String userId, String fidoId) {
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

    private static class UserResponse {
        private List<User> value;

        public List<User> getValue() {
            return value;
        }

        public void setValue(List<User> value) {
            this.value = value;
        }
    }

    private static class MultipleFidoResponse {
        private List<Fido2AuthenticationMethod> value;

        public List<Fido2AuthenticationMethod> getValue() {
            return value;
        }

        public void setValue(List<Fido2AuthenticationMethod> value) {
            this.value = value;
        }
    }

    private static class SingleFidoResponse {
        private Fido2AuthenticationMethod value;

        public Fido2AuthenticationMethod getValue() {
            return value;
        }

        public void setValue(Fido2AuthenticationMethod value) {
            this.value = value;
        }
    }
}
