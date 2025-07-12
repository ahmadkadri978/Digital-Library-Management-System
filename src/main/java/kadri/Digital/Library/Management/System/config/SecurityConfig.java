package kadri.Digital.Library.Management.System.config;

import kadri.Digital.Library.Management.System.service.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final CustomOAuth2UserService customOAuth2UserService;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService) {
        this.customOAuth2UserService = customOAuth2UserService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers
                        .contentSecurityPolicy(csp -> csp
                                .policyDirectives("img-src 'self' https://avatars.githubusercontent.com;")
                        )
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/","/index","/Digital Library/error", "/css/**", "/js/**").permitAll()
                        .requestMatchers("/DigitalLibrary/books/**").authenticated()
                        .requestMatchers("/DigitalLibrary/admin/**").authenticated()

                        .requestMatchers("/DigitalLibrary/admin/**").hasRole("ADMIN")
                        .requestMatchers("/DigitalLibrary/books/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/DigitalLibrary/reservations/**").authenticated()
                        .requestMatchers("/DigitalLibrary/profile").authenticated()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("/Digital Library/redirect", true)
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .failureUrl("/login?error")
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/index")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .addLogoutHandler((request, response, authentication) -> {
                            SecurityContextHolder.clearContext();
                            request.getSession().invalidate();
                        })
                );

        return http.build();
    }
}


