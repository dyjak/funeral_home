package zaklad.pogrzebowy.api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Konfiguracja zabezpieczeń aplikacji zakładu pogrzebowego.
 * Definiuje ustawienia bezpieczeństwa, w tym filtry, kodowanie haseł oraz zasady CORS.
 *
 * @author INF_CZARNI
 * @version 1.0
 */
@Configuration
public class SecurityConfig {

    /**
     * Dostarcza enkoder do bezpiecznego hashowania haseł.
     *
     * @return Instancja BCryptPasswordEncoder do kodowania haseł
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Konfiguruje i dostarcza menedżera uwierzytelniania.
     *
     * @param authenticationConfiguration Konfiguracja uwierzytelniania
     * @return Skonfigurowany AuthenticationManager
     * @throws Exception W przypadku błędu konfiguracji
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Filtr uwierzytelniania JWT wstrzykiwany do konfiguracji.
     */
    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    /**
     * Konfiguruje łańcuch filtrów bezpieczeństwa.
     * Definiuje zasady dostępu do endpointów oraz konfigurację uwierzytelniania.
     *
     * @param http Obiekt konfiguracji bezpieczeństwa HTTP
     * @return Skonfigurowany SecurityFilterChain
     * @throws Exception W przypadku błędu konfiguracji
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/users/**").permitAll()
                        .requestMatchers("/tasks/assigned").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/tasks/**").hasAnyRole("USER", "ADMIN") // Allow both roles to access tasks
                        .requestMatchers("/clients/**").authenticated()
                        .requestMatchers("/users/me").authenticated()
                        .requestMatchers("/reports/**").authenticated()
                        .requestMatchers("/api/task-report").authenticated()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * Konfiguruje zasady CORS (Cross-Origin Resource Sharing).
     * Określa dozwolone źródła, metody i nagłówki dla żądań międzydomenowych.
     *
     * @return Skonfigurowane źródło konfiguracji CORS
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173")); // Explicit origin
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(Arrays.asList("Content-Disposition")); // Dodane dla pobierania raportów

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}