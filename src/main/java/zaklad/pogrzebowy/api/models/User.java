package zaklad.pogrzebowy.api.models;

import jakarta.persistence.*;
import org.springframework.security.crypto.bcrypt.BCrypt;
import java.util.HashSet;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Klasa reprezentująca użytkownika w systemie zakładu pogrzebowego.
 * Przechowuje informacje o pracownikach oraz ich uprawnieniach w systemie.
 * 
 * @author INF_CZARNI
 * @version 1.0
 */
@Entity
@Table(name = "users")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Transient
    private String plainPassword;

    @OneToMany(mappedBy = "assignedUser")
    @JsonIgnoreProperties("assignedUser")
    private Set<Task> assignedTasks = new HashSet<>();

    /**
     * Enumeracja określająca role użytkownika w systemie.
     */
    public enum Role {
        /** Administrator systemu z pełnymi uprawnieniami */
        ADMIN, 
        /** Standardowy użytkownik z ograniczonymi uprawnieniami */
        USER
    }

    /**
     * Konstruktor domyślny.
     */
    public User() {
    }

    /**
     * Konstruktor tworzący nowego użytkownika z podstawowymi danymi.
     *
     * @param firstName    Imię użytkownika
     * @param lastName     Nazwisko użytkownika
     * @param email       Adres email użytkownika (unikalny)
     * @param passwordHash Zahaszowane hasło użytkownika
     * @param role        Rola użytkownika w systemie
     */
    public User(String firstName, String lastName, String email, String passwordHash, Role role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    /**
     * Pobiera identyfikator użytkownika.
     *
     * @return Identyfikator użytkownika
     */
    public Long getId() {
        return id;
    }

    /**
     * Ustawia identyfikator użytkownika.
     *
     * @param id Nowy identyfikator użytkownika
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Pobiera imię użytkownika.
     *
     * @return Imię użytkownika
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Ustawia imię użytkownika.
     *
     * @param firstName Nowe imię użytkownika
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Pobiera nazwisko użytkownika.
     *
     * @return Nazwisko użytkownika
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Ustawia nazwisko użytkownika.
     *
     * @param lastName Nowe nazwisko użytkownika
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Pobiera adres email użytkownika.
     *
     * @return Adres email użytkownika
     */
    public String getEmail() {
        return email;
    }

    /**
     * Ustawia adres email użytkownika.
     *
     * @param email Nowy adres email użytkownika
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Pobiera zahaszowane hasło użytkownika.
     *
     * @return Zahaszowane hasło użytkownika
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * Ustawia zahaszowane hasło użytkownika.
     *
     * @param passwordHash Nowe zahaszowane hasło użytkownika
     */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    /**
     * Pobiera rolę użytkownika w systemie.
     *
     * @return Rola użytkownika
     */
    public Role getRole() {
        return role;
    }

    /**
     * Ustawia rolę użytkownika w systemie.
     *
     * @param role Nowa rola użytkownika
     */
    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * Pobiera niezahaszowane hasło użytkownika (używane tymczasowo podczas tworzenia konta).
     *
     * @return Niezahaszowane hasło użytkownika
     */
    public String getPlainPassword() {
        return plainPassword;
    }

    /**
     * Ustawia niezahaszowane hasło użytkownika.
     *
     * @param plainPassword Nowe niezahaszowane hasło użytkownika
     */
    public void setPlainPassword(String plainPassword) {
        this.plainPassword = plainPassword;
    }

    /**
     * Pobiera listę zadań przypisanych do użytkownika.
     *
     * @return Zbiór zadań przypisanych do użytkownika
     */
    public Set<Task> getAssignedTasks() {
        return assignedTasks;
    }

    /**
     * Ustawia listę zadań przypisanych do użytkownika.
     *
     * @param assignedTasks Nowy zbiór zadań przypisanych do użytkownika
     */
    public void setAssignedTasks(Set<Task> assignedTasks) {
        this.assignedTasks = assignedTasks;
    }

    /**
     * Weryfikuje, czy podane hasło jest zgodne z hasłem użytkownika.
     *
     * @param inputPassword Hasło do weryfikacji
     * @return true jeśli hasło jest poprawne, false w przeciwnym razie
     */
    public boolean verifyPassword(String inputPassword) {
        if (this.passwordHash == null || inputPassword == null) {
            return false;
        }
        return BCrypt.checkpw(inputPassword, this.passwordHash);
    }
}