package kadri.Digital.Library.Management.System.config;

import kadri.Digital.Library.Management.System.service.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
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
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize->authorize
                        .requestMatchers( "/","/error","/css/**", "/js/**").permitAll()
                        .requestMatchers("/Digital Library/admin/**").hasRole("ADMIN")
                        .requestMatchers("/Digital Library/reservations/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/Digital Library/books/**").hasAnyRole("USER")
                        .requestMatchers("/Digital Library/profile").authenticated()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exceptions->exceptions
                        .accessDeniedPage("/access-denied"))
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("/Digital Library/profile")
                                .userInfoEndpoint(userInfo -> userInfo
                                        .userService(customOAuth2UserService)
                                )
                        .failureUrl("/login?error")
                )

                .logout(logout->logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("https://github.com/logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .addLogoutHandler(((request, response, authentication) -> {
                            SecurityContextHolder.clearContext();
                        }))
                );
        return http.build();

    }
}
