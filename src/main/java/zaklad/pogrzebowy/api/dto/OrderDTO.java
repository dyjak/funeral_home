package zaklad.pogrzebowy.api.dto;

import zaklad.pogrzebowy.api.models.Order;
import java.time.LocalDateTime;

/**
 * Obiekt transferu danych (DTO) dla encji {@link Order}.
 * Służy do przekazywania danych zamówienia pomiędzy warstwami aplikacji.
 */
public class OrderDTO {
    private Long id;
    private String cadaverFirstName;
    private String cadaverLastName;
    private String deathCertificateNumber;
    private LocalDateTime orderDate;
    private String status;
    private UserDTO user;
    private ClientDTO client;

    /**
     * Domyślny konstruktor.
     */
    public OrderDTO() {}

    /**
     * Tworzy obiekt OrderDTO na podstawie encji Order.
     *
     * @param order encja zamówienia do konwersji na DTO
     */
    public OrderDTO(Order order) {
        this.id = order.getId();
        this.cadaverFirstName = order.getCadaverFirstName();
        this.cadaverLastName = order.getCadaverLastName();
        this.deathCertificateNumber = order.getDeathCertificateNumber();
        this.orderDate = order.getOrderDate();
        this.status = order.getStatus().toString();
        this.user = order.getUser() != null ? new UserDTO(order.getUser()) : null;
        this.client = order.getClient() != null ? new ClientDTO(order.getClient()) : null;
    }

    /**
     * Zwraca identyfikator zamówienia.
     *
     * @return identyfikator zamówienia
     */
    public Long getId() {
        return id;
    }

    /**
     * Zwraca imię zmarłego.
     *
     * @return imię zmarłego
     */
    public String getCadaverFirstName() {
        return cadaverFirstName;
    }

    /**
     * Zwraca nazwisko zmarłego.
     *
     * @return nazwisko zmarłego
     */
    public String getCadaverLastName() {
        return cadaverLastName;
    }

    /**
     * Zwraca numer aktu zgonu.
     *
     * @return numer aktu zgonu
     */
    public String getDeathCertificateNumber() {
        return deathCertificateNumber;
    }

    /**
     * Zwraca datę i godzinę zamówienia.
     *
     * @return data i godzina zamówienia
     */
    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    /**
     * Zwraca status zamówienia.
     *
     * @return status zamówienia
     */
    public String getStatus() {
        return status;
    }

    /**
     * Zwraca użytkownika powiązanego z zamówieniem.
     *
     * @return DTO użytkownika
     */
    public UserDTO getUser() {
        return user;
    }

    /**
     * Zwraca klienta powiązanego z zamówieniem.
     *
     * @return DTO klienta
     */
    public ClientDTO getClient() {
        return client;
    }

    /**
     * Ustawia identyfikator zamówienia.
     *
     * @param id identyfikator zamówienia do ustawienia
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Ustawia imię zmarłego.
     *
     * @param cadaverFirstName imię zmarłego do ustawienia
     */
    public void setCadaverFirstName(String cadaverFirstName) {
        this.cadaverFirstName = cadaverFirstName;
    }

    /**
     * Ustawia nazwisko zmarłego.
     *
     * @param cadaverLastName nazwisko zmarłego do ustawienia
     */
    public void setCadaverLastName(String cadaverLastName) {
        this.cadaverLastName = cadaverLastName;
    }

    /**
     * Ustawia numer aktu zgonu.
     *
     * @param deathCertificateNumber numer aktu zgonu do ustawienia
     */
    public void setDeathCertificateNumber(String deathCertificateNumber) {
        this.deathCertificateNumber = deathCertificateNumber;
    }

    /**
     * Ustawia datę i godzinę zamówienia.
     *
     * @param orderDate data i godzina zamówienia do ustawienia
     */
    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    /**
     * Ustawia status zamówienia.
     *
     * @param status status zamówienia do ustawienia
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Ustawia użytkownika powiązanego z zamówieniem.
     *
     * @param user DTO użytkownika do ustawienia
     */
    public void setUser(UserDTO user) {
        this.user = user;
    }

    /**
     * Ustawia klienta powiązanego z zamówieniem.
     *
     * @param client DTO klienta do ustawienia
     */
    public void setClient(ClientDTO client) {
        this.client = client;
    }

    /**
     * Konwertuje ten DTO na encję Order.
     *
     * @return skonwertowana encja Order
     */
    public Order toEntity() {
        Order order = new Order();
        order.setId(this.id);
        order.setCadaverFirstName(this.cadaverFirstName);
        order.setCadaverLastName(this.cadaverLastName);
        order.setDeathCertificateNumber(this.deathCertificateNumber);
        order.setOrderDate(this.orderDate);
        if (this.status != null) {
            order.setStatus(Order.Status.valueOf(this.status.toUpperCase()));
        }
        return order;
    }
}
