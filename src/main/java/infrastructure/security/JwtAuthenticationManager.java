package infrastructure.security;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import reactor.core.publisher.Mono;

public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtService jwtService;

    public JwtAuthenticationManager(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getCredentials().toString();

        return Mono.fromCallable(() -> jwtService.validateAndGetPayload(token))
                .map(payload -> {
                    List<SimpleGrantedAuthority> authorities = payload.roles().stream()
                            .map(role -> new SimpleGrantedAuthority(role))
                            .collect(Collectors.toList());

                    Authentication authResult = new UsernamePasswordAuthenticationToken(
                            payload.subject(),
                            null,
                            authorities
                    );

                    return authResult;
                })
                .onErrorMap(ex -> new BadCredentialsException("Token inválido", ex));
    }
}