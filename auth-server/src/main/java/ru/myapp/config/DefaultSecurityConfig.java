package ru.myapp.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2TokenType;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.server.authorization.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import ru.myapp.model.CustomUser;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.security.config.Customizer.withDefaults;

@Slf4j
@EnableWebSecurity
public class DefaultSecurityConfig {

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .authorizeRequests(authorizeRequests ->
                authorizeRequests
                .antMatchers("/register").not().fullyAuthenticated()
                .mvcMatchers("/users/**")
                .fullyAuthenticated()
                .anyRequest().authenticated()
        )
        .formLogin(withDefaults())
                .logout().invalidateHttpSession(true)
                .clearAuthentication(true)
                .logoutUrl("/logout")
                .deleteCookies("JSESSIONID").permitAll();
        return http.build();
    }

    @Bean
    OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer() {
        return context -> {
            if (context.getTokenType() == OAuth2TokenType.ACCESS_TOKEN
                    || Objects.equals(context.getTokenType(), new OAuth2TokenType(OidcParameterNames.ID_TOKEN))) {
                Authentication principal = context.getPrincipal();

                Set<String> authorities = principal.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet());
                context.getClaims().claim("user-authorities", authorities);
                CustomUser user = (CustomUser) principal.getPrincipal();
                context.getClaims().claim("email", user.getEmail());
            }
        };
    }
}
