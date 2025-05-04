package dev.dmgiangi.fido2_poc.service.session;

import dev.dmgiangi.fido2_poc.service.entra.dto.EntraUser;

import java.time.Instant;

public record Session(EntraUser user, Instant expiration) {
}