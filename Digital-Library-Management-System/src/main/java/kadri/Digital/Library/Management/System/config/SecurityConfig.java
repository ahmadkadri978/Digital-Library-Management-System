package kadri.Digital.Library.Management.System.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                .authorizeHttpRequests(authorize->authorize
                        .requestMatchers("/", "/books", "/search", "/css/**", "/js/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2->oauth2.defaultSuccessUrl("/books")
                        .failureUrl("/login?error=true")
                )
                .logout(logout->logout
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                );
        return http.build();

    }
}
