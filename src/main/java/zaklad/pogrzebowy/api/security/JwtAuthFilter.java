package zaklad.pogrzebowy.api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import zaklad.pogrzebowy.api.models.User;
import zaklad.pogrzebowy.api.repositories.UserRepository;

import java.io.IOException;
import java.util.List;

/**
 * Filtr uwierzytelniania JWT (JSON Web Token) dla aplikacji zakładu pogrzebowego.
 * Odpowiada za przechwytywanie żądań HTTP i weryfikację tokenów JWT w nagłówkach autoryzacji.
 * 
 * <p>Filtr wykonuje następujące zadania:</p>
 * <ul>
 *   <li>Wyodrębnia token JWT z nagłówka Authorization</li>
 *   <li>Weryfikuje token i wyodrębnia email użytkownika</li>
 *   <li>Ładuje dane użytkownika z bazy danych</li>
 *   <li>Ustawia kontekst bezpieczeństwa Spring Security</li>
 * </ul>
 *
 * @author INF_CZARNI
 * @version 1.0
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    /**
     * Narzędzie do obsługi tokenów JWT.
     */
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Repozytorium do operacji na danych użytkowników.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Główna metoda filtrująca żądania HTTP.
     * Przetwarza nagłówek Authorization, weryfikuje token JWT i ustawia kontekst bezpieczeństwa.
     *
     * @param request     Żądanie HTTP
     * @param response    Odpowiedź HTTP
     * @param filterChain Łańcuch filtrów do dalszego przetwarzania
     * @throws ServletException W przypadku błędu przetwarzania żądania
     * @throws IOException      W przypadku błędu wejścia/wyjścia
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        String email = null;
        String jwt = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            email = jwtUtil.extractUsername(jwt);
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            User user = userRepository.findByEmail(email).orElse(null);

            if (user != null) {
                // Convert user role to Spring Security authority
                List<SimpleGrantedAuthority> authorities = List.of(
                    new SimpleGrantedAuthority("ROLE_" + user.getRole())
                );

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    user,
                    null,
                    authorities  // Add the authorities here
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
