package zaklad.pogrzebowy.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zaklad.pogrzebowy.api.services.TaskReportService;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Kontroler REST odpowiedzialny za generowanie raportów PDF dotyczących zadań.
 *
 * Udostępnia następujące operacje:
 * <ul>
 *   <li>Generowanie raportu PDF dla zadań na podstawie filtrów (POST /reports/tasks)</li>
 *   <li>Obsługa zapytań OPTIONS dla endpointu raportów zadań (OPTIONS /reports/tasks)</li>
 * </ul>
 *
 * Wstrzykiwane zależności:
 * <ul>
 *   <li>TaskReportService – logika generowania raportów PDF dla zadań</li>
 * </ul>
 *
 * Raporty są zwracane jako pliki PDF z odpowiednimi nagłówkami umożliwiającymi pobranie lub podgląd w przeglądarce.
 * Kontroler umożliwia dostęp z dowolnego pochodzenia (CORS).
 */
@RestController
@RequestMapping("/reports")
@CrossOrigin(origins = "*")
public class TaskReportController {

    @Autowired
    private TaskReportService reportService;

    /**
     * Generuje raport PDF dla zadań na podstawie przekazanych filtrów.
     * @param request mapa zawierająca filtry do raportu
     * @param authHeader nagłówek autoryzacyjny JWT
     * @return plik PDF z raportem zadań
     */
    @PostMapping(value = "/tasks", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> generateTaskReport(
            @RequestBody Map<String, Object> request,
            @RequestHeader("Authorization") String authHeader) {

        Map<String, Object> filters = (Map<String, Object>) request.get("filters");
        ByteArrayInputStream reportStream = reportService.generateReport(filters);

        // Generowanie nazwy pliku z datą i godziną
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = "raport_zadan_" + timestamp + ".pdf";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=" + filename);
        headers.add("Access-Control-Expose-Headers", "Content-Disposition");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(reportStream));
    }

    /**
     * Obsługuje zapytania OPTIONS dla endpointu raportów zadań.
     * Umożliwia prawidłową obsługę CORS w przypadku zapytań preflight.
     * @return odpowiedź z dozwolonymi nagłówkami i metodami
     */
    @RequestMapping(value = "/tasks", method = RequestMethod.OPTIONS)
    public ResponseEntity<?> handleOptions() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Access-Control-Allow-Origin", "*");
        headers.add("Access-Control-Allow-Methods", "POST, OPTIONS");
        headers.add("Access-Control-Allow-Headers", "Content-Type, Authorization");
        headers.add("Access-Control-Expose-Headers", "Content-Disposition");

        return ResponseEntity.ok().headers(headers).build();
    }
}