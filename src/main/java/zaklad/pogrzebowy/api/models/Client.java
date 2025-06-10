package zaklad.pogrzebowy.api.models;

import jakarta.persistence.*;

/**
 * Klasa encji reprezentująca klienta w systemie.
 * Mapowana na tabelę "clients" w bazie danych.
 * 
 * Zawiera podstawowe informacje o kliencie, takie jak imię,
 * nazwisko oraz numer telefonu.
 */
@Entity
@Table(name = "clients")
public class Client {

    /**
     * Unikalny identyfikator klienta, generowany automatycznie.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Imię klienta.
     * Pole nie może być puste, maksymalna długość to 50 znaków.
     */
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    /**
     * Nazwisko klienta.
     * Pole nie może być puste, maksymalna długość to 50 znaków.
     */
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    /**
     * Numer telefonu klienta.
     * Pole nie może być puste, maksymalna długość to 15 znaków.
     */
    @Column(name = "phone", nullable = false, length = 15)
    private String phone;

    /**
     * Konstruktor domyślny wymagany przez JPA.
     */
    public Client() {}

    /**
     * Konstruktor tworzący nowego klienta z podanymi danymi.
     * 
     * @param firstName imię klienta
     * @param lastName nazwisko klienta
     * @param phone numer telefonu klienta
     */
    public Client(String firstName, String lastName, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
    }

    /**
     * Pobiera unikalny identyfikator klienta.
     * 
     * @return id klienta
     */
    public Long getId() {
        return id;
    }

    /**
     * Ustawia unikalny identyfikator klienta.
     * 
     * @param id id klienta
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Pobiera imię klienta.
     * 
     * @return imię klienta
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Ustawia imię klienta.
     * 
     * @param firstName imię klienta
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Pobiera nazwisko klienta.
     * 
     * @return nazwisko klienta
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Ustawia nazwisko klienta.
     * 
     * @param lastName nazwisko klienta
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Pobiera numer telefonu klienta.
     * 
     * @return numer telefonu
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Ustawia numer telefonu klienta.
     * 
     * @param phone numer telefonu
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }
}
