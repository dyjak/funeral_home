package zaklad.pogrzebowy.api.controllers;



import zaklad.pogrzebowy.api.models.User;
import zaklad.pogrzebowy.api.repositories.UserRepository;
import zaklad.pogrzebowy.api.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Kontroler odpowiedzialny za obsługę uwierzytelniania użytkowników.
 *
 * Endpointy:
 * <ul>
 *   <li>POST /auth/login – uwierzytelnia użytkownika na podstawie adresu e-mail i hasła, zwraca token JWT przy poprawnych danych.</li>
 * </ul>
 *
 * Wstrzykiwane zależności:
 * <ul>
 *   <li>UserRepository – dostęp do danych użytkowników</li>
 *   <li>PasswordEncoder – weryfikacja hasła</li>
 *   <li>JwtUtil – generowanie tokenów JWT</li>
 * </ul>
 *
 * Kontroler umożliwia dostęp z dowolnego pochodzenia (CORS).
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*") // 🔹 Dla React
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Uwierzytelnia użytkownika na podstawie e-maila i hasła.
     *
     * @param loginUser Obiekt użytkownika zawierający e-mail i hasło (w polu passwordHash).
     * @return Token JWT w przypadku poprawnej autoryzacji lub błąd 401 w przeciwnym razie.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginUser) {
        Optional<User> user = userRepository.findByEmail(loginUser.getEmail());

        if (user.isPresent() && passwordEncoder.matches(loginUser.getPasswordHash(), user.get().getPasswordHash())) {
            String token = jwtUtil.generateToken(user.get().getEmail());
            return ResponseEntity.ok().body("{\"token\": \"" + token + "\"}");
        } else {
            return ResponseEntity.status(401).body("Nieprawidłowy email lub hasło");
        }
    }
}