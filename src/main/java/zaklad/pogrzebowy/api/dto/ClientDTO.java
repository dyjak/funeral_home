package zaklad.pogrzebowy.api.dto;

import zaklad.pogrzebowy.api.models.Client;


/**
 * Data Transfer Object (DTO) dla encji Client.
 *
 * Służy do przesyłania podstawowych danych klienta pomiędzy warstwami aplikacji oraz do komunikacji z API.
 *
 * Pola:
 * <ul>
 *   <li>id – identyfikator klienta</li>
 *   <li>firstName – imię klienta</li>
 *   <li>lastName – nazwisko klienta</li>
 *   <li>phone – numer telefonu klienta</li>
 * </ul>
 *
 * Udostępnia konstruktory, gettery, settery oraz metodę konwertującą DTO na encję Client.
 */
public class ClientDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String phone;

    // Constructors
    public ClientDTO() {}

    public ClientDTO(Client client) {
        this.id = client.getId();
        this.firstName = client.getFirstName();
        this.lastName = client.getLastName();
        this.phone = client.getPhone();
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhone() {
        return phone;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    // Convert DTO to Entity
    public Client toEntity() {
        Client client = new Client();
        client.setFirstName(this.firstName);
        client.setLastName(this.lastName);
        client.setPhone(this.phone);
        return client;
    }
}