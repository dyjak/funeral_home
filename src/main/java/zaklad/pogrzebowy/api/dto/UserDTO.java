package zaklad.pogrzebowy.api.dto;

import zaklad.pogrzebowy.api.models.User;

/**
 * Klasa Data Transfer Object (DTO) reprezentująca użytkownika (User).
 * Służy do przenoszenia danych użytkownika między warstwami aplikacji,
 * chroniąc bezpośredni dostęp do encji.
 * 
 * Zawiera podstawowe informacje o użytkowniku, takie jak imię, nazwisko,
 * adres e-mail, rola oraz zaszyfrowane hasło.
 */
public class UserDTO {
    /**
     * Unikalny identyfikator użytkownika.
     */
    private Long id;

    /**
     * Imię użytkownika.
     */
    private String firstName;

    /**
     * Nazwisko użytkownika.
     */
    private String lastName;

    /**
     * Adres e-mail użytkownika.
     */
    private String email;

    /**
     * Rola użytkownika (np. ADMIN, USER) w formie tekstowej.
     */
    private String role;

    /**
     * Hash hasła użytkownika.
     */
    private String passwordHash;

    /**
     * Konstruktor domyślny.
     */
    public UserDTO() {}

    /**
     * Konstruktor tworzący obiekt UserDTO na podstawie encji User.
     * Kopiuje podstawowe pola z encji.
     * 
     * @param user encja użytkownika, z której kopiowane są dane
     */
    public UserDTO(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.role = user.getRole().toString();
    }

    // Gettery i settery z opisami

    /**
     * Pobiera identyfikator użytkownika.
     * 
     * @return id użytkownika
     */
    public Long getId() {
        return id;
    }

    /**
     * Ustawia identyfikator użytkownika.
     * 
     * @param id id użytkownika
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Pobiera imię użytkownika.
     * 
     * @return imię użytkownika
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Ustawia imię użytkownika.
     * 
     * @param firstName imię użytkownika
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Pobiera nazwisko użytkownika.
     * 
     * @return nazwisko użytkownika
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Ustawia nazwisko użytkownika.
     * 
     * @param lastName nazwisko użytkownika
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Pobiera adres e-mail użytkownika.
     * 
     * @return e-mail użytkownika
     */
    public String getEmail() {
        return email;
    }

    /**
     * Ustawia adres e-mail użytkownika.
     * 
     * @param email e-mail użytkownika
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Pobiera rolę użytkownika.
     * 
     * @return rola użytkownika w formie tekstowej
     */
    public String getRole() {
        return role;
    }

    /**
     * Ustawia rolę użytkownika.
     * 
     * @param role rola użytkownika w formie tekstowej
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Pobiera hash hasła użytkownika.
     * 
     * @return hash hasła
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * Ustawia hash hasła użytkownika.
     * 
     * @param passwordHash hash hasła
     */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    /**
     * Konwertuje obiekt UserDTO na encję User.
     * Mapuje pola DTO na odpowiednie pola encji,
     * uwzględnia rolę oraz hash hasła, jeśli są ustawione.
     * 
     * @return obiekt encji User odpowiadający temu DTO
     */
    public User toEntity() {
        User user = new User();
        user.setId(this.id);
        user.setFirstName(this.firstName);
        user.setLastName(this.lastName);
        user.setEmail(this.email);
        if (this.passwordHash != null) {
            user.setPasswordHash(this.passwordHash);
        }
        if (this.role != null) {
            user.setRole(User.Role.valueOf(this.role.toUpperCase()));
        }
        return user;
    }
}
