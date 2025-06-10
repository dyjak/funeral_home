package zaklad.pogrzebowy.api.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zaklad.pogrzebowy.api.models.Order;
import zaklad.pogrzebowy.api.models.Client;
import zaklad.pogrzebowy.api.repositories.ClientRepository;
import zaklad.pogrzebowy.api.repositories.OrderRepository;

import java.util.List;
import java.util.Optional;

/**
 * Serwis odpowiedzialny za zarządzanie zamówieniami w zakładzie pogrzebowym.
 * Implementuje interfejs IOrderService i dostarcza podstawowe operacje CRUD na zamówieniach.
 *
 * @author INF_CZARNI
 * @version 1.0
 */
@Service
public class OrderService implements IOrderService {

    /**
     * Repozytorium do operacji na zamówieniach.
     */
    @Autowired
    private OrderRepository repository;

    /**
     * Repozytorium do operacji na klientach.
     */
    @Autowired
    private ClientRepository clientRepository;

    /**
     * Pobiera wszystkie zamówienia z bazy danych.
     *
     * @return Lista wszystkich zamówień
     */
    @Override
    public List<Order> findAll() {
        return repository.findAll();
    }

    /**
     * Wyszukuje zamówienie po identyfikatorze.
     *
     * @param id Identyfikator zamówienia
     * @return Optional zawierający znalezione zamówienie lub pusty Optional
     */
    @Override
    public Optional<Order> findById(Long id) {
        return repository.findById(id);
    }

    /**
     * Wyszukuje zamówienia o określonym statusie.
     *
     * @param status Status zamówienia do wyszukania
     * @return Lista zamówień o podanym statusie
     */
    @Override
    public List<Order> findByStatus(Order.Status status) {
        return repository.findByStatus(status);
    }

    /**
     * Tworzy nowe zamówienie w systemie.
     *
     * @param order Nowe zamówienie do utworzenia
     * @return Utworzone zamówienie z przypisanym identyfikatorem
     */
    @Override
    public Order create(Order order) {
        return repository.save(order);
    }

    /**
     * Aktualizuje istniejące zamówienie.
     *
     * @param id Identyfikator zamówienia do aktualizacji
     * @param updatedOrder Zaktualizowane dane zamówienia
     * @return Zaktualizowane zamówienie
     * @throws RuntimeException Gdy zamówienie o podanym ID nie istnieje
     */
    @Override
    public Order update(Long id, Order updatedOrder) {
        return repository.findById(id)
                .map(existingOrder -> {
                    existingOrder.setOrderDate(updatedOrder.getOrderDate());
                    existingOrder.setStatus(updatedOrder.getStatus());
                    existingOrder.setCadaverFirstName(updatedOrder.getCadaverFirstName());
                    existingOrder.setCadaverLastName(updatedOrder.getCadaverLastName());
                    existingOrder.setDeathCertificateNumber(updatedOrder.getDeathCertificateNumber());
                    existingOrder.setBirthDate(updatedOrder.getBirthDate());
                    existingOrder.setDeathDate(updatedOrder.getDeathDate());
                    existingOrder.setClient(updatedOrder.getClient());
                    existingOrder.setUser(updatedOrder.getUser());
                    return repository.save(existingOrder);
                })
                .orElseThrow(() -> new RuntimeException("Zamówienie nie znalezione"));
    }

    /**
     * Usuwa zamówienie oraz powiązanego z nim klienta.
     * 
     * @param id Identyfikator zamówienia do usunięcia
     * @throws RuntimeException Gdy zamówienie o podanym ID nie istnieje
     */
    @Override
    public void delete(Long id) {
        Order order = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Get the client before deleting the order
        Client client = order.getClient();

        // Delete the order first
        repository.deleteById(id);

        // Then delete the associated client if exists
        if (client != null) {
            clientRepository.deleteById(client.getId());
        }
    }
}
