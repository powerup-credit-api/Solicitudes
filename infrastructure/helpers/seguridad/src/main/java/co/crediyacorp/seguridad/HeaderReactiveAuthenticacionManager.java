package co.crediyacorp.seguridad;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;

public class HeaderReactiveAuthenticacionManager implements ReactiveAuthenticationManager {
    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.justOrEmpty(authentication)
                .map(auth -> !auth.isAuthenticated() ? setAuthenticated(auth) : auth);
    }

    private Authentication setAuthenticated(Authentication auth) {
        auth.setAuthenticated(true);
        return auth;
    }

}
