package zaklad.pogrzebowy.api.controllers;




import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import zaklad.pogrzebowy.api.services.ClientService;
import zaklad.pogrzebowy.api.models.Client;

import java.util.List;

/**
 * Kontroler REST do zarządzania klientami.
 *
 * Udostępnia następujące operacje na zasobie klienta:
 * <ul>
 *   <li>Pobieranie wszystkich klientów (GET /clients)</li>
 *   <li>Tworzenie nowego klienta (POST /clients)</li>
 *   <li>Aktualizacja danych klienta (PUT /clients/{id})</li>
 *   <li>Usuwanie klienta (DELETE /clients/{id})</li>
 * </ul>
 *
 * Wstrzykiwane zależności:
 * <ul>
 *   <li>ClientService – logika biznesowa związana z klientami</li>
 * </ul>
 *
 * Kontroler umożliwia dostęp z dowolnego pochodzenia (CORS).
 */
@RestController
@RequestMapping("/clients") // Ustalona ścieżka bazowa dla API klientów
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ClientController {

    @Autowired
    private ClientService service;

    /**
     * Pobiera listę wszystkich klientów.
     * @return lista klientów
     */
    @GetMapping
    public List<Client> findAll() {
        return service.findAll();
    }

    /**
     * Tworzy nowego klienta.
     * @param client dane nowego klienta
     * @return utworzony klient
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Client create(@RequestBody Client client) {
        return service.create(client);
    }

    /**
     * Aktualizuje dane istniejącego klienta.
     * @param id identyfikator klienta
     * @param client nowe dane klienta
     * @return zaktualizowany klient
     */
    @PutMapping("/{id}")
    public Client update(@PathVariable Long id, @RequestBody Client client) {
        return service.update(id, client);
    }

    /**
     * Usuwa klienta o podanym identyfikatorze.
     * @param id identyfikator klienta do usunięcia
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}