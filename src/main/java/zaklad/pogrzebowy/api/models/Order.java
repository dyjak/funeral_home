package zaklad.pogrzebowy.api.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Klasa encji reprezentująca zlecenie (Order) w systemie.
 * Mapowana na tabelę "orders" w bazie danych.
 * 
 * Zawiera dane dotyczące zlecenia pogrzebowego, takie jak daty
 * (zlecenia, urodzenia, śmierci), dane denata, status zlecenia,
 * a także powiązania z użytkownikiem, klientem i listą zadań.
 */
@Entity
@Table(name = "orders")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Order {

    /**
     * Unikalny identyfikator zlecenia, generowany automatycznie.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Data utworzenia zlecenia.
     */
    private LocalDateTime orderDate;

    /**
     * Data urodzenia denata.
     */
    private LocalDateTime birthDate;

    /**
     * Data śmierci denata.
     */
    private LocalDateTime deathDate;

    /**
     * Imię denata.
     */
    private String cadaverFirstName;

    /**
     * Nazwisko denata.
     */
    private String cadaverLastName;

    /**
     * Numer aktu zgonu.
     */
    @Column(name = "death_certificate_number", length = 50)
    private String deathCertificateNumber;

    /**
     * Status zlecenia.
     */
    @Enumerated(EnumType.STRING)
    private Status status;

    /**
     * Użytkownik przypisany do zlecenia.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"assignedTasks", "passwordHash", "plainPassword", "email"})
    private User user;

    /**
     * Klient powiązany ze zleceniem.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id")
    @JsonIgnoreProperties({"orders"})
    private Client client;

    /**
     * Lista zadań powiązanych ze zleceniem.
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"order", "assignments", "assignedUser"})
    private List<Task> tasks = new ArrayList<>();

    /**
     * Możliwe statusy zlecenia.
     */
    public enum Status {
        pending,    // oczekujące
        completed,  // zakończone
        canceled    // anulowane
    }

    /**
     * Pobiera unikalny identyfikator zlecenia.
     * 
     * @return id zlecenia
     */
    public Long getId() {
        return id;
    }

    /**
     * Ustawia unikalny identyfikator zlecenia.
     * 
     * @param id id zlecenia
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Pobiera datę utworzenia zlecenia.
     * 
     * @return data zlecenia
     */
    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    /**
     * Ustawia datę utworzenia zlecenia.
     * 
     * @param orderDate data zlecenia
     */
    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    /**
     * Pobiera datę urodzenia denata.
     * 
     * @return data urodzenia
     */
    public LocalDateTime getBirthDate() {
        return birthDate;
    }

    /**
     * Ustawia datę urodzenia denata.
     * 
     * @param birthDate data urodzenia
     */
    public void setBirthDate(LocalDateTime birthDate) {
        this.birthDate = birthDate;
    }

    /**
     * Pobiera datę śmierci denata.
     * 
     * @return data śmierci
     */
    public LocalDateTime getDeathDate() {
        return deathDate;
    }

    /**
     * Ustawia datę śmierci denata.
     * 
     * @param deathDate data śmierci
     */
    public void setDeathDate(LocalDateTime deathDate) {
        this.deathDate = deathDate;
    }

    /**
     * Pobiera imię denata.
     * 
     * @return imię denata
     */
    public String getCadaverFirstName() {
        return cadaverFirstName;
    }

    /**
     * Ustawia imię denata.
     * 
     * @param cadaverFirstName imię denata
     */
    public void setCadaverFirstName(String cadaverFirstName) {
        this.cadaverFirstName = cadaverFirstName;
    }

    /**
     * Pobiera nazwisko denata.
     * 
     * @return nazwisko denata
     */
    public String getCadaverLastName() {
        return cadaverLastName;
    }

    /**
     * Ustawia nazwisko denata.
     * 
     * @param cadaverLastName nazwisko denata
     */
    public void setCadaverLastName(String cadaverLastName) {
        this.cadaverLastName = cadaverLastName;
    }

    /**
     * Pobiera numer aktu zgonu.
     * 
     * @return numer aktu zgonu
     */
    public String getDeathCertificateNumber() {
        return deathCertificateNumber;
    }

    /**
     * Ustawia numer aktu zgonu.
     * 
     * @param deathCertificateNumber numer aktu zgonu
     */
    public void setDeathCertificateNumber(String deathCertificateNumber) {
        this.deathCertificateNumber = deathCertificateNumber;
    }

    /**
     * Pobiera status zlecenia.
     * 
     * @return status zlecenia
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Ustawia status zlecenia.
     * 
     * @param status status zlecenia
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Pobiera użytkownika przypisanego do zlecenia.
     * 
     * @return użytkownik
     */
    public User getUser() {
        return user;
    }

    /**
     * Ustawia użytkownika przypisanego do zlecenia.
     * 
     * @param user użytkownik
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Pobiera klienta powiązanego ze zleceniem.
     * 
     * @return klient
     */
    public Client getClient() {
        return client;
    }

    /**
     * Ustawia klienta powiązanego ze zleceniem.
     * 
     * @param client klient
     */
    public void setClient(Client client) {
        this.client = client;
    }

    /**
     * Pobiera listę zadań powiązanych ze zleceniem.
     * 
     * @return lista zadań
     */
    public List<Task> getTasks() {
        return tasks;
    }

    /**
     * Ustawia listę zadań powiązanych ze zleceniem.
     * 
     * @param tasks lista zadań
     */
    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
}
