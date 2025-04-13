package dev.dmgiangi.fido2_poc.configuration;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.Serial;
import java.time.Instant;
import java.util.HashMap;

@Component
public class GraphConfiguration {
    @Bean
    public RestTemplate graphServiceClient(
            @Value("${entra.credential.client-id}") String clientId,
            @Value("${entra.credential.client-secret}") String clientSecret,
            @Value("${entra.credential.tenant-id}") String tenantId,
            RestTemplateBuilder builder) {
        return builder
                .rootUri("https://graph.microsoft.com")
                .interceptors(new BearerTokenAuthInterceptor(tenantId, clientId, clientSecret))
                .build();
    }

    private static class BearerTokenAuthInterceptor implements ClientHttpRequestInterceptor {
        private static final String SCOPE = "https://graph.microsoft.com/.default";
        private final RestTemplate restTemplate = new RestTemplate();

        private final String endpoint;
        private final String clientId;
        private final String clientSecret;

        private DecodedJWT decodedJWT;

        public BearerTokenAuthInterceptor(String tenantId,
                                          String clientId,
                                          String clientSecret) {
            this.endpoint = "https://login.microsoftonline.com/" + tenantId + "/oauth2/v2.0/token";
            this.clientId = clientId;
            this.clientSecret = clientSecret;
        }

        @NonNull
        @Override
        public ClientHttpResponse intercept(@NonNull HttpRequest request,
                                            @NonNull byte[] body,
                                            @NonNull ClientHttpRequestExecution execution) throws IOException {
            if (this.decodedJWT == null || isExpired())
                this.decodedJWT = getToken();

            request.getHeaders().setBearerAuth(decodedJWT.getToken());
            return execution.execute(request, body);
        }

        private boolean isExpired() {
            return this.decodedJWT.getExpiresAtAsInstant().isBefore(Instant.now());
        }

        private DecodedJWT getToken() {
            final var formData = new LinkedMultiValueMap<String, String>();
            formData.add("client_id", clientId);
            formData.add("client_secret", clientSecret);
            formData.add("scope", SCOPE);
            formData.add("grant_type", "client_credentials");

            final var headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            final var tokenRequest = new HttpEntity<>(formData, headers);

            final var response = restTemplate.exchange(
                    endpoint,
                    HttpMethod.POST,
                    tokenRequest,
                    TokenResponse.class);

            if (response.getBody() == null)
                throw new RuntimeException("No body in response");

            final var token = response.getBody().get("access_token");
            if (token == null)
                throw new RuntimeException("No access token in response");

            return JWT.decode(token);
        }

        private static class TokenResponse extends HashMap<String, String> {
            @Serial
            private static final long serialVersionUID = 6276053145950871722L;
        }
    }
}
