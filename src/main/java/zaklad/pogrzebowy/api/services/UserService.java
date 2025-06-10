package zaklad.pogrzebowy.api.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zaklad.pogrzebowy.api.models.User;
import zaklad.pogrzebowy.api.repositories.OrderRepository;
import zaklad.pogrzebowy.api.repositories.TaskAssignmentRepository;
import zaklad.pogrzebowy.api.repositories.UserRepository;

import java.util.List;
import java.util.Optional;
import org.springframework.security.crypto.bcrypt.BCrypt;


/**
 * Serwis odpowiedzialny za zarządzanie użytkownikami w systemie zakładu pogrzebowego.
 * Implementuje interfejs IUserService i obsługuje operacje CRUD na użytkownikach,
 * wraz z bezpiecznym przechowywaniem haseł i walidacją danych.
 *
 * @author INF_CZARNI
 * @version 1.0
 */
@Service
public class UserService implements IUserService {

    /**
     * Repozytorium użytkowników.
     */
    private final UserRepository repository;

    /**
     * Enkoder do bezpiecznego hashowania haseł.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Repozytorium przypisań zadań.
     */
    @Autowired
    private TaskAssignmentRepository assignmentRepository;

    /**
     * Repozytorium zamówień.
     */
    @Autowired
    private OrderRepository orderRepository;

    /**
     * Konstruktor serwisu użytkowników.
     *
     * @param repository Repozytorium użytkowników
     * @param passwordEncoder Enkoder do hashowania haseł
     */
    @Autowired
    public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Pobiera wszystkich użytkowników z systemu.
     *
     * @return Lista wszystkich użytkowników
     */
    @Override
    public List<User> findAll() {
        return repository.findAll();
    }

    /**
     * Wyszukuje użytkownika po identyfikatorze.
     *
     * @param id Identyfikator użytkownika
     * @return Optional zawierający znalezionego użytkownika lub pusty Optional
     */
    @Override
    public Optional<User> findById(Long id) {
        return repository.findById(id);
    }

    /**
     * Wyszukuje użytkownika po adresie email.
     *
     * @param email Adres email użytkownika
     * @return Optional zawierający znalezionego użytkownika lub pusty Optional
     */
    @Override
    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    /**
     * Tworzy nowego użytkownika w systemie.
     * Waliduje unikalność emaila i hasło, następnie hashuje hasło przed zapisem.
     *
     * @param user Nowy użytkownik do utworzenia
     * @return Utworzony użytkownik z przypisanym identyfikatorem
     * @throws IllegalArgumentException Gdy email już istnieje lub hasło jest puste
     */
    @Override
    @Transactional
    public User create(User user) {
        // Validate email uniqueness
        if (repository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Get and validate password
        String rawPassword = user.getPasswordHash();
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        // Hash the password
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        return repository.save(user);
    }

    /**
     * Aktualizuje istniejącego użytkownika.
     * Sprawdza unikalność nowego emaila i aktualizuje hasło tylko jeśli zostało zmienione.
     *
     * @param id Identyfikator użytkownika do aktualizacji
     * @param updatedUser Zaktualizowane dane użytkownika
     * @return Zaktualizowany użytkownik
     * @throws RuntimeException Gdy użytkownik nie istnieje
     * @throws IllegalArgumentException Gdy nowy email już istnieje
     */
    @Override
    @Transactional
    public User update(Long id, User updatedUser) {
        return repository.findById(id)
                .map(existingUser -> {
                    // Update basic info
                    existingUser.setFirstName(updatedUser.getFirstName());
                    existingUser.setLastName(updatedUser.getLastName());
                    existingUser.setRole(updatedUser.getRole());

                    // Handle email change with validation
                    if (!existingUser.getEmail().equals(updatedUser.getEmail())) {
                        if (repository.findByEmail(updatedUser.getEmail()).isPresent()) {
                            throw new IllegalArgumentException("New email already exists");
                        }
                        existingUser.setEmail(updatedUser.getEmail());
                    }

                    // Handle password change if provided
                    if (updatedUser.getPasswordHash() != null &&
                            !updatedUser.getPasswordHash().isBlank() &&
                            !updatedUser.getPasswordHash().startsWith("$2a$")) {
                        existingUser.setPasswordHash(passwordEncoder.encode(updatedUser.getPasswordHash()));
                    }

                    return repository.save(existingUser);
                })
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Usuwa użytkownika z systemu.
     * Usuwa również wszystkie powiązane przypisania zadań i zamówienia.
     *
     * @param id Identyfikator użytkownika do usunięcia
     * @throws RuntimeException Gdy użytkownik nie istnieje
     */
    @Override
    @Transactional
    public void delete(Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

        // Usuń wszystkie przypisania i zamówienia tego użytkownika (jeśli takie istnieją)
        assignmentRepository.deleteAllByUserId(id);
        orderRepository.deleteAllByUserId(id);

        repository.deleteById(id);
    }
}