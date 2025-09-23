package co.crediyacorp.seguridad;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import reactor.core.publisher.Mono;

@Configuration
public class JwtAuthFilterConfig {

    @Bean
    public JwtServerAuthenticationConverter jwtServerAuthenticationConverter(@Value("${security.jwt.secret}") String secret) {
        return new JwtServerAuthenticationConverter(secret);
    }

    @Bean("jwtReactiveAuthenticationManager")
    public ReactiveAuthenticationManager jwtReactiveAuthenticationManager() {
        return Mono::just;
    }


    @Bean
    public AuthenticationWebFilter jwtAuthWebFilter(
            @Qualifier("jwtReactiveAuthenticationManager") ReactiveAuthenticationManager jwtReactiveAuthenticationManager,
            JwtServerAuthenticationConverter jwtServerAuthenticationConverter) {

        AuthenticationWebFilter jwtAuthFilter = new AuthenticationWebFilter(jwtReactiveAuthenticationManager);
        jwtAuthFilter.setServerAuthenticationConverter(jwtServerAuthenticationConverter);

        jwtAuthFilter.setAuthenticationFailureHandler((exchange, ex) -> {
            exchange.getExchange().getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getExchange().getResponse().setComplete();
        });

        return jwtAuthFilter;
    }
}
