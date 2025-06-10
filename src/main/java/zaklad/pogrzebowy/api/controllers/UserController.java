package zaklad.pogrzebowy.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zaklad.pogrzebowy.api.repositories.UserRepository;
import zaklad.pogrzebowy.api.security.JwtUtil;
import zaklad.pogrzebowy.api.services.UserService;
import zaklad.pogrzebowy.api.models.User;
import zaklad.pogrzebowy.api.dto.UserDTO;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Kontroler REST do zarządzania użytkownikami.
 *
 * Udostępnia następujące operacje na zasobie użytkownika:
 * <ul>
 *   <li>Pobieranie danych aktualnie zalogowanego użytkownika (GET /users/me)</li>
 *   <li>Pobieranie wszystkich użytkowników (GET /users)</li>
 *   <li>Pobieranie użytkownika po ID (GET /users/{id})</li>
 *   <li>Pobieranie użytkownika po adresie e-mail (GET /users/email/{email})</li>
 *   <li>Tworzenie nowego użytkownika (POST /users)</li>
 *   <li>Aktualizacja danych użytkownika (PUT /users/{id})</li>
 *   <li>Usuwanie użytkownika (DELETE /users/{id})</li>
 * </ul>
 *
 * Wstrzykiwane zależności:
 * <ul>
 *   <li>UserService – logika biznesowa związana z użytkownikami</li>
 *   <li>UserRepository – dostęp do danych użytkowników</li>
 *   <li>JwtUtil – obsługa tokenów JWT</li>
 * </ul>
 *
 * Kontroler umożliwia dostęp z określonego pochodzenia (CORS).
 */
@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Pobiera dane aktualnie zalogowanego użytkownika na podstawie tokena JWT.
     * @param authHeader nagłówek autoryzacyjny JWT
     * @return dane użytkownika lub 401 jeśli nieautoryzowany
     */
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String email = jwtUtil.extractUsername(token);
            return userRepository.findByEmail(email)
                    .map(user -> ResponseEntity.ok(new UserDTO(user)))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * Pobiera listę wszystkich użytkowników.
     * @return lista użytkowników
     */
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        try {
            List<User> users = userService.findAll();
            List<UserDTO> userDTOs = users.stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
            return ResponseEntity.ok(userDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Pobiera użytkownika po identyfikatorze.
     * @param id identyfikator użytkownika
     * @return dane użytkownika lub 404 jeśli nie znaleziono
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return userService.findById(id)
            .map(user -> ResponseEntity.ok(new UserDTO(user)))
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Pobiera użytkownika po adresie e-mail.
     * @param email adres e-mail użytkownika
     * @return dane użytkownika lub 404 jeśli nie znaleziono
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable String email) {
        return userService.findByEmail(email)
            .map(user -> ResponseEntity.ok(new UserDTO(user)))
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Tworzy nowego użytkownika.
     * @param userDTO dane nowego użytkownika
     * @return utworzony użytkownik lub błąd walidacji
     */
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        try {
            User user = userDTO.toEntity();
            User createdUser = userService.create(user);
            return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new UserDTO(createdUser));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .build();
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
        }
    }

    /**
     * Aktualizuje dane istniejącego użytkownika.
     * @param id identyfikator użytkownika
     * @param userDTO nowe dane użytkownika
     * @return zaktualizowany użytkownik lub błąd walidacji
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        try {
            User user = userDTO.toEntity();
            user.setId(id);
            User updatedUser = userService.update(id, user);
            return ResponseEntity.ok(new UserDTO(updatedUser));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .build();
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
        }
    }

    /**
     * Usuwa użytkownika o podanym identyfikatorze.
     * @param id identyfikator użytkownika do usunięcia
     * @return odpowiedź bez treści lub 404 jeśli nie znaleziono użytkownika
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            userService.delete(id);
            return ResponseEntity
                .noContent()
                .build();
        } catch (Exception e) {
            return ResponseEntity
                .notFound()
                .build();
        }
    }
}