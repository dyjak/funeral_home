package zaklad.pogrzebowy.api.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import zaklad.pogrzebowy.api.models.Order;
import zaklad.pogrzebowy.api.services.OrderService;

import java.util.List;
import java.util.Optional;

/**
 * Kontroler REST do zarządzania zamówieniami pogrzebowymi.
 *
 * Udostępnia następujące operacje na zasobie zamówienia:
 * <ul>
 *   <li>Pobieranie wszystkich zamówień (GET /orders)</li>
 *   <li>Pobieranie zamówienia po ID (GET /orders/{id})</li>
 *   <li>Pobieranie zamówień po statusie (GET /orders/status/{status})</li>
 *   <li>Tworzenie nowego zamówienia (POST /orders)</li>
 *   <li>Aktualizacja zamówienia (PUT /orders/{id})</li>
 *   <li>Usuwanie zamówienia (DELETE /orders/{id})</li>
 * </ul>
 *
 * Wstrzykiwane zależności:
 * <ul>
 *   <li>OrderService – logika biznesowa związana z zamówieniami</li>
 * </ul>
 *
 * Kontroler umożliwia dostęp z dowolnego pochodzenia (CORS).
 */
@RestController
@RequestMapping("/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * Pobiera listę wszystkich zamówień.
     * @return lista zamówień
     */
    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.findAll();
    }

    /**
     * Pobiera zamówienie o podanym identyfikatorze.
     * @param id identyfikator zamówienia
     * @return zamówienie (jeśli istnieje)
     */
    @GetMapping("/{id}")
    public Optional<Order> getOrderById(@PathVariable Long id) {
        return orderService.findById(id);
    }

    /**
     * Pobiera zamówienia o określonym statusie.
     * @param status status zamówienia
     * @return lista zamówień o danym statusie
     */
    @GetMapping("/status/{status}")
    public List<Order> getOrdersByStatus(@PathVariable Order.Status status) {
        return orderService.findByStatus(status);
    }

    /**
     * Tworzy nowe zamówienie.
     * @param order dane nowego zamówienia
     * @return utworzone zamówienie
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Order createOrder(@RequestBody Order order) {
        return orderService.create(order);
    }

    /**
     * Aktualizuje istniejące zamówienie.
     * @param id identyfikator zamówienia
     * @param order nowe dane zamówienia
     * @return zaktualizowane zamówienie
     */
    @PutMapping("/{id}")
    public Order updateOrder(@PathVariable Long id, @RequestBody Order order) {
        return orderService.update(id, order);
    }

    /**
     * Usuwa zamówienie o podanym identyfikatorze.
     * @param id identyfikator zamówienia do usunięcia
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable Long id) {
        orderService.delete(id);
    }
}

