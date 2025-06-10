package zaklad.pogrzebowy.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zaklad.pogrzebowy.api.services.UserReportService;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Kontroler REST odpowiedzialny za generowanie raportów PDF dotyczących użytkowników.
 *
 * Udostępnia następujące operacje:
 * <ul>
 *   <li>Generowanie raportu PDF dla użytkowników na podstawie filtrów (POST /reports/users)</li>
 *   <li>Obsługa zapytań OPTIONS dla endpointu raportów użytkowników (OPTIONS /reports/users)</li>
 * </ul>
 *
 * Wstrzykiwane zależności:
 * <ul>
 *   <li>UserReportService – logika generowania raportów PDF dla użytkowników</li>
 * </ul>
 *
 * Raporty są zwracane jako pliki PDF z odpowiednimi nagłówkami umożliwiającymi pobranie lub podgląd w przeglądarce.
 * Kontroler umożliwia dostęp z dowolnego pochodzenia (CORS).
 */
@RestController
@RequestMapping("/reports")
@CrossOrigin(origins = "*")
public class UserReportController {

    @Autowired
    private UserReportService userReportService;

    /**
     * Generuje raport PDF dla użytkowników na podstawie przekazanych filtrów.
     * @param request mapa zawierająca filtry do raportu
     * @param authHeader nagłówek autoryzacyjny JWT
     * @return plik PDF z raportem użytkowników
     */
    @PostMapping(value = "/users", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> generateUserReport(@RequestBody Map<String, Object> request,
                                                                  @RequestHeader("Authorization") String authHeader) {
        ByteArrayInputStream bis = userReportService.generateReport(request.get("filters"));

        String filename = "raport_uzytkownicy_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=" + filename);
        headers.add("Access-Control-Expose-Headers", "Content-Disposition");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

    /**
     * Obsługuje zapytania OPTIONS dla endpointu raportów użytkowników.
     * Umożliwia prawidłową obsługę CORS w przypadku zapytań preflight.
     * @return odpowiedź z dozwolonymi nagłówkami i metodami
     */
    @RequestMapping(value = "/users", method = RequestMethod.OPTIONS)
    public ResponseEntity<?> handleOptions() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Access-Control-Allow-Origin", "*");
        headers.add("Access-Control-Allow-Methods", "POST, OPTIONS");
        headers.add("Access-Control-Allow-Headers", "Content-Type, Authorization");
        headers.add("Access-Control-Expose-Headers", "Content-Disposition");
        return ResponseEntity.ok().headers(headers).build();
    }
}