package co.crediyacorp.seguridad.token_generation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

    @Value("${security.jwt.secret}")
    private String secret;

    @Value("${security.jwt.expiration}")
    private long expiration;

    @Bean
    public JwtProvider jwtProvider() {
        return new JwtProvider(secret, expiration);
    }
}
