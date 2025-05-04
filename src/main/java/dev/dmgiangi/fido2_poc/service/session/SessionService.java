package dev.dmgiangi.fido2_poc.service.session;

import dev.dmgiangi.fido2_poc.service.entra.dto.EntraUser;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionService {
    private static final Map<UUID, Session> AUTHENTICATION = new ConcurrentHashMap<>();

    public static UUID createSession(EntraUser user) {
        final var sessionId = UUID.randomUUID();
        final var session = new Session(user, Instant.now().plusSeconds(24 * 60 * 60));

        AUTHENTICATION.put(sessionId, session);

        return sessionId;
    }

    public static Optional<EntraUser> getUserFromSession(UUID sessionId) {
        if (sessionId == null)
            return Optional.empty();

        final var session = AUTHENTICATION.get(sessionId);
        if (session == null)
            return Optional.empty();

        final var user = session.user();
        final var expiration = session.expiration();
        if (expiration.isAfter(Instant.now()) && session.user() != null)
            return Optional.of(user);

        AUTHENTICATION.remove(sessionId);
        return Optional.empty();
    }
}
