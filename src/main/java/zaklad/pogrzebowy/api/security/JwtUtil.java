package zaklad.pogrzebowy.api.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * Klasa narzędziowa do obsługi tokenów JWT (JSON Web Token).
 * Zapewnia funkcjonalność generowania, walidacji i ekstrakcji danych z tokenów JWT.
 * 
 * @author INF_CZARNI
 * @version 1.0
 */
@Component
public class JwtUtil {

    /**
     * Klucz tajny używany do podpisywania tokenów JWT.
     * Musi mieć minimum 32 znaki dla algorytmu HS256.
     */
    private static final String SECRET_KEY = "TwójSuperTajnyKluczJWT123456789012345";

    /**
     * Czas ważności tokenu w milisekundach (1 dzień).
     */
    private static final long EXPIRATION_TIME = 86400000;

    /**
     * Klucz kryptograficzny wygenerowany na podstawie SECRET_KEY.
     */
    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    /**
     * Generuje nowy token JWT dla podanego adresu email.
     *
     * @param email Adres email użytkownika
     * @return Wygenerowany token JWT
     */
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Wyodrębnia nazwę użytkownika (email) z tokenu JWT.
     *
     * @param token Token JWT do przetworzenia
     * @return Email użytkownika zapisany w tokenie
     * @throws JwtException W przypadku nieprawidłowego tokenu
     */
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    /**
     * Wyodrębnia claims (dane) z tokenu JWT.
     *
     * @param token Token JWT do przetworzenia
     * @return Obiekt Claims zawierający dane z tokenu
     * @throws JwtException W przypadku nieprawidłowego tokenu
     */
    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Sprawdza poprawność tokenu JWT.
     *
     * @param token Token JWT do walidacji
     * @return true jeśli token jest poprawny, false w przeciwnym razie
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    /**
     * Pobiera adres email użytkownika z tokenu JWT.
     * Metoda pomocnicza wykorzystująca extractUsername.
     *
     * @param token Token JWT do przetworzenia
     * @return Email użytkownika zapisany w tokenie
     */
    public String getEmailFromToken(String token) {
        return extractUsername(token);
    }
}