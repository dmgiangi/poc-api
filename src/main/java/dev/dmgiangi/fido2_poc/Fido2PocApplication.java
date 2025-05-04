package dev.dmgiangi.fido2_poc;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.security.web.webauthn.jackson.WebauthnJackson2Module;

@SpringBootApplication
public class Fido2PocApplication {

    public static void main(String[] args) {
        SpringApplication.run(Fido2PocApplication.class, args);
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customJackson() {
        return builder -> builder
                .modules(
                        new WebauthnJackson2Module(),
                        new JavaTimeModule());
    }
}
