package co.crediyacorp.seguridad;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

public class HeaderAutenticacionConverter implements ServerAuthenticationConverter {
    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {

        return Mono.justOrEmpty(exchange.getRequest())
                .flatMap(request ->{
                    String email = request.getHeaders().getFirst("X-USER-SUBJECT");
                    String role = request.getHeaders().getFirst("X-USER-ROLE");

                    return email == null || role == null ?
                            Mono.empty() :
                            Mono.just(new UsernamePasswordAuthenticationToken(
                                    email,
                                    role,
                                    List.of(new SimpleGrantedAuthority("ROLE_" + role))));
                });

    }
}
