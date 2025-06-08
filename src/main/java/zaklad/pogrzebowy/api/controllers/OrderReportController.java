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

@RestController
@RequestMapping("/reports")
@CrossOrigin(origins = "*")
public class OrderReportController {

    @Autowired
    private OrderReportService reportService;

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