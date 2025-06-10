package zaklad.pogrzebowy.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zaklad.pogrzebowy.api.services.OrderReportService;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Kontroler REST odpowiedzialny za generowanie raportów PDF dotyczących zamówień pogrzebowych.
 *
 * Udostępnia następujące operacje:
 * <ul>
 *   <li>Generowanie raportu PDF dla pojedynczego zamówienia (POST /reports/orders/{orderId})</li>
 *   <li>Generowanie zbiorczego raportu PDF dla wielu zamówień (POST /reports/orders/bulk)</li>
 * </ul>
 *
 * Wstrzykiwane zależności:
 * <ul>
 *   <li>OrderReportService – logika generowania raportów PDF</li>
 * </ul>
 *
 * Raporty są zwracane jako pliki PDF z odpowiednimi nagłówkami umożliwiającymi pobranie lub podgląd w przeglądarce.
 * Kontroler umożliwia dostęp z dowolnego pochodzenia (CORS).
 */
@RestController
@RequestMapping("/reports")
@CrossOrigin(origins = "*")
public class OrderReportController {

    @Autowired
    private OrderReportService reportService;

    /**
     * Generuje raport PDF dla pojedynczego zamówienia.
     * @param orderId identyfikator zamówienia
     * @param authHeader nagłówek autoryzacyjny JWT
     * @return plik PDF z raportem zamówienia
     */
    @PostMapping(value = "/orders/{orderId}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> generateOrderReport(
            @PathVariable Long orderId,
            @RequestHeader("Authorization") String authHeader) {

        ByteArrayInputStream reportStream = reportService.generateReport(orderId);

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = "raport_zamowienia_" + orderId + "_" + timestamp + ".pdf";

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
     * Generuje zbiorczy raport PDF dla wielu zamówień.
     * @param orderIds lista identyfikatorów zamówień
     * @param authHeader nagłówek autoryzacyjny JWT
     * @return plik PDF z raportem zbiorczym
     */
    @PostMapping(value = "/orders/bulk", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> generateBulkOrderReport(
            @RequestBody List<Long> orderIds,
            @RequestHeader("Authorization") String authHeader) {

        ByteArrayInputStream reportStream = reportService.generateBulkReport(orderIds);

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = "raport_zbiorczy_zamowien_" + timestamp + ".pdf";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=" + filename);
        headers.add("Access-Control-Expose-Headers", "Content-Disposition");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(reportStream));
    }
}