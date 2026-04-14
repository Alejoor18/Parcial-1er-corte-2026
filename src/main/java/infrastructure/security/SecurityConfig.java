package infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

@Configuration
@EnableReactiveMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public MapReactiveUserDetailsService reactiveUserDetailsService(PasswordEncoder encoder) {
        UserDetails admin = User.withUsername("admin")
                .password(encoder.encode("admin123"))
                .roles("ADMIN")
                .build();

        UserDetails user = User.withUsername("user")
                .password(encoder.encode("user123"))
                .roles("USER")
                .build();

        return new MapReactiveUserDetailsService(admin, user);
    }

    @Bean
    public ReactiveAuthenticationManager jwtAuthenticationManager(JwtService jwtService) {
        return new JwtAuthenticationManager(jwtService);
    }

    @Bean
    public AuthenticationWebFilter jwtAuthWebFilter(ReactiveAuthenticationManager jwtAuthenticationManager) {
        AuthenticationWebFilter filter = new AuthenticationWebFilter(jwtAuthenticationManager);
        filter.setServerAuthenticationConverter(new JwtServerAuthenticationConverter());
        filter.setSecurityContextRepository(NoOpServerSecurityContextRepository.getInstance());
        return filter;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http,
            AuthenticationWebFilter jwtAuthWebFilter
    ) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/health").permitAll()
                        .pathMatchers("/", "/index.html", "/styles.css", "/app.js").permitAll()
                        .pathMatchers("/static/**").permitAll()
                        .pathMatchers(HttpMethod.POST, "/api/v1/auth/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/v1/marcas/**").hasAnyRole("ADMIN", "USER")
                        .pathMatchers(HttpMethod.GET, "/api/v1/gafas/**").hasAnyRole("ADMIN", "USER")
                        .pathMatchers(HttpMethod.POST, "/api/v1/marcas/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/api/v1/marcas/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/api/v1/marcas/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.POST, "/api/v1/gafas/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/api/v1/gafas/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/api/v1/gafas/**").hasRole("ADMIN")
                        .anyExchange().authenticated()
                )
                .addFilterAt(jwtAuthWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }
}