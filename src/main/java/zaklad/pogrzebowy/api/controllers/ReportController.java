package zaklad.pogrzebowy.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import zaklad.pogrzebowy.api.models.Report;
import zaklad.pogrzebowy.api.services.ReportService;

import java.util.List;
import java.util.Optional;

/**
 * Kontroler REST do zarządzania raportami.
 *
 * Udostępnia następujące operacje na zasobie raportu:
 * <ul>
 *   <li>Pobieranie wszystkich raportów (GET /reports)</li>
 *   <li>Pobieranie raportu po ID (GET /reports/{id})</li>
 *   <li>Tworzenie nowego raportu (POST /reports)</li>
 *   <li>Aktualizacja raportu (PUT /reports/{id})</li>
 *   <li>Usuwanie raportu (DELETE /reports/{id})</li>
 * </ul>
 *
 * Wstrzykiwane zależności:
 * <ul>
 *   <li>ReportService – logika biznesowa związana z raportami</li>
 * </ul>
 *
 * Kontroler umożliwia dostęp z dowolnego pochodzenia (CORS).
 */
@RestController
@RequestMapping("/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    @Autowired
    private ReportService reportService;

    /**
     * Pobiera listę wszystkich raportów.
     * @return lista raportów
     */
    @GetMapping
    public List<Report> getAllReports() {
        return reportService.findAll();
    }

    /**
     * Pobiera raport o podanym identyfikatorze.
     * @param id identyfikator raportu
     * @return raport (jeśli istnieje)
     */
    @GetMapping("/{id}")
    public Optional<Report> getReportById(@PathVariable Long id) {
        return reportService.findById(id);
    }

    /**
     * Tworzy nowy raport.
     * @param report dane nowego raportu
     * @return utworzony raport
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Report createReport(@RequestBody Report report) {
        return reportService.create(report);
    }

    /**
     * Aktualizuje istniejący raport.
     * @param id identyfikator raportu
     * @param report nowe dane raportu
     * @return zaktualizowany raport
     */
    @PutMapping("/{id}")
    public Report updateReport(@PathVariable Long id, @RequestBody Report report) {
        return reportService.update(id, report);
    }

    /**
     * Usuwa raport o podanym identyfikatorze.
     * @param id identyfikator raportu do usunięcia
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReport(@PathVariable Long id) {
        reportService.delete(id);
    }
}