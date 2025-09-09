package co.crediyacorp.seguridad;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {


    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http, AuthenticationWebFilter headerAuthFilter) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(auth -> auth
                        .pathMatchers("/api/v1/validar",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/swagger-ui/index.html").permitAll()
                        .pathMatchers(HttpMethod.POST,"/api/v1/solicitud").hasAnyRole("ADMINISTRADOR", "SOLICITANTE","ASESOR")
                        .pathMatchers(HttpMethod.GET,"/api/v1/solicitud").hasAnyRole("ADMINISTRADOR", "ASESOR")
                        .pathMatchers(HttpMethod.PUT,"/api/v1/solicitud").hasAnyRole( "ADMINISTRADOR", "ASESOR")
                        .anyExchange().authenticated()
                )
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .addFilterAt(headerAuthFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    @Bean("headerReactiveAuthenticationManager")
    public ReactiveAuthenticationManager headerReactiveAuthenticationManager() {
        return new HeaderReactiveAuthenticacionManager();
    }

    @Bean
    public ServerAuthenticationConverter headerAuthenticationConverter() {
        return new HeaderAutenticacionConverter();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}