package co.crediyacorp.seguridad.token_generation;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import reactor.core.publisher.Mono;

import java.util.Date;

public class JwtProvider {

    private final String secret;
    private final long expiration;

    public JwtProvider(String secret, long expiration) {
        this.secret = secret;
        this.expiration = expiration;
    }

    public Mono<String> generateServiceToken(String serviceName) {
        return Mono.fromSupplier(() -> {
            Date now = new Date();
            Date expiry = new Date(now.getTime() + expiration);

            return JWT.create()
                    .withSubject(serviceName)
                    .withClaim("role", "ADMINISTRADOR")
                    .withClaim("type", "service")
                    .withIssuedAt(now)
                    .withExpiresAt(expiry)
                    .sign(Algorithm.HMAC256(secret));
        });
    }

}