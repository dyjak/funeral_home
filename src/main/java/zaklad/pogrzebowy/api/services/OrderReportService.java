package zaklad.pogrzebowy.api.services;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zaklad.pogrzebowy.api.models.Order;
import zaklad.pogrzebowy.api.repositories.OrderRepository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Serwis odpowiedzialny za generowanie raportów PDF dla zamówień w zakładzie pogrzebowym.
 * Umożliwia generowanie zarówno pojedynczych raportów dla zamówienia, jak i raportów zbiorczych.
 *
 * @author INF_CZARNI
 * @version 1.0
 */
@Service
public class OrderReportService {

    @Autowired
    private OrderRepository orderRepository;

    /**
     * Generuje raport PDF dla pojedynczego zamówienia.
     *
     * @param orderId Identyfikator zamówienia
     * @return Strumień bajtów zawierający wygenerowany dokument PDF
     * @throws RuntimeException Gdy nie znaleziono zamówienia lub wystąpił błąd podczas generowania PDF
     */
    public ByteArrayInputStream generateReport(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);
            document.open();

            // Create fonts with encoding for Polish characters
            BaseFont baseFont = BaseFont.createFont(
                BaseFont.HELVETICA, 
                BaseFont.CP1250, 
                BaseFont.EMBEDDED
            );
            Font titleFont = new Font(baseFont, 18, Font.BOLD);
            Font headerFont = new Font(baseFont, 12, Font.BOLD);
            Font contentFont = new Font(baseFont, 11, Font.NORMAL);

            // Add title
            Paragraph title = new Paragraph("Raport zamówienia #" + order.getId(), titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Add generation date
            Paragraph dateInfo = new Paragraph(
                "Wygenerowano: " + java.time.LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                contentFont
            );
            dateInfo.setAlignment(Element.ALIGN_RIGHT);
            dateInfo.setSpacingAfter(20);
            document.add(dateInfo);

            // Order details
            addSection(document, "Dane zamówienia", headerFont);
            addField(document, "Status:", formatStatus(order.getStatus().toString()), contentFont);
            addField(document, "Data zamówienia:", 
                    order.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), contentFont);

            // Client details
            addSection(document, "Dane klienta", headerFont);
            addField(document, "Imię i nazwisko:", 
                    order.getClient().getFirstName() + " " + order.getClient().getLastName(), contentFont);
            addField(document, "Telefon:", order.getClient().getPhone(), contentFont);

            // Deceased details
            addOrderDetails(document, order, headerFont, contentFont);

            // Tasks
            if (order.getTasks() != null && !order.getTasks().isEmpty()) {
                addSection(document, "Przypisane zadania", headerFont);
                PdfPTable table = new PdfPTable(4);
                table.setWidthPercentage(100);
                table.setSpacingBefore(10f);
                table.setSpacingAfter(10f);

                // Headers
                addTableHeader(table, "Nazwa", headerFont);
                addTableHeader(table, "Status", headerFont);
                addTableHeader(table, "Priorytet", headerFont);
                addTableHeader(table, "Termin", headerFont);

                // Data
                order.getTasks().forEach(task -> {
                    addTableCell(table, task.getTaskName(), contentFont);
                    addTableCell(table, formatStatus(task.getStatus().toString()), contentFont);
                    addTableCell(table, formatPriority(task.getPriority().toString()), contentFont);
                    addTableCell(table, task.getDueDate() != null ? 
                        task.getDueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : 
                        "Nie określono", contentFont);
                });

                document.add(table);
            }

            document.close();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Błąd podczas generowania PDF", e);
        }
    }

    /**
     * Generuje zbiorczy raport PDF dla wielu zamówień.
     *
     * @param orderIds Lista identyfikatorów zamówień
     * @return Strumień bajtów zawierający wygenerowany dokument PDF
     * @throws RuntimeException Gdy nie znaleziono zamówienia lub wystąpił błąd podczas generowania PDF
     */
    public ByteArrayInputStream generateBulkReport(List<Long> orderIds) {
        List<Order> orders = orderIds.stream()
            .map(id -> orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id)))
            .collect(Collectors.toList());

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);
            document.open();

            BaseFont baseFont = BaseFont.createFont(
                BaseFont.HELVETICA, 
                BaseFont.CP1250, 
                BaseFont.EMBEDDED
            );
            Font titleFont = new Font(baseFont, 18, Font.BOLD);
            Font headerFont = new Font(baseFont, 12, Font.BOLD);
            Font contentFont = new Font(baseFont, 11, Font.NORMAL);

            // Add title
            Paragraph title = new Paragraph("Raport zbiórczy zamówień", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Add generation date
            Paragraph dateInfo = new Paragraph(
                "Wygenerowano: " + java.time.LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                contentFont
            );
            dateInfo.setAlignment(Element.ALIGN_RIGHT);
            dateInfo.setSpacingAfter(20);
            document.add(dateInfo);

            for (Order order : orders) {
                addSection(document, "Zamówienie #" + order.getId(), headerFont);
                
                // Order details
                addField(document, "Status:", formatStatus(order.getStatus().toString()), contentFont);
                addField(document, "Data zamówienia:", 
                        order.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), contentFont);

                // Client details
                addSection(document, "Dane klienta", headerFont);
                addField(document, "Imię i nazwisko:", 
                        order.getClient().getFirstName() + " " + order.getClient().getLastName(), contentFont);
                addField(document, "Telefon:", order.getClient().getPhone(), contentFont);

                // Deceased details
                addOrderDetails(document, order, headerFont, contentFont);

                // Tasks
                if (order.getTasks() != null && !order.getTasks().isEmpty()) {
                    addSection(document, "Przypisane zadania", headerFont);
                    PdfPTable table = new PdfPTable(4);
                    table.setWidthPercentage(100);
                    table.setSpacingBefore(10f);
                    table.setSpacingAfter(10f);

                    addTableHeader(table, "Nazwa", headerFont);
                    addTableHeader(table, "Status", headerFont);
                    addTableHeader(table, "Priorytet", headerFont);
                    addTableHeader(table, "Termin", headerFont);

                    order.getTasks().forEach(task -> {
                        addTableCell(table, task.getTaskName(), contentFont);
                        addTableCell(table, formatStatus(task.getStatus().toString()), contentFont);
                        addTableCell(table, formatPriority(task.getPriority().toString()), contentFont);
                        addTableCell(table, task.getDueDate() != null ? 
                            task.getDueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : 
                            "Nie określono", contentFont);
                    });

                    document.add(table);
                }

                // Add page break between orders
                if (orders.indexOf(order) < orders.size() - 1) {
                    document.newPage();
                }
            }

            document.close();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Błąd podczas generowania PDF", e);
        }
    }

    /**
     * Dodaje sekcję do dokumentu PDF.
     *
     * @param document Dokument PDF
     * @param title Tytuł sekcji
     * @param font Czcionka do użycia
     * @throws DocumentException W przypadku błędu podczas dodawania sekcji
     */
    private void addSection(Document document, String title, Font font) throws DocumentException {
        Paragraph section = new Paragraph(title, font);
        section.setSpacingBefore(15);
        section.setSpacingAfter(10);
        document.add(section);
    }

    /**
     * Dodaje pole z etykietą i wartością do dokumentu PDF.
     *
     * @param document Dokument PDF
     * @param label Etykieta pola
     * @param value Wartość pola
     * @param font Czcionka do użycia
     * @throws DocumentException W przypadku błędu podczas dodawania pola
     */
    private void addField(Document document, String label, String value, Font font) throws DocumentException {
        Paragraph field = new Paragraph(label + " " + value, font);
        field.setIndentationLeft(20);
        field.setSpacingAfter(5);
        document.add(field);
    }

    /**
     * Dodaje nagłówek kolumny do tabeli PDF.
     *
     * @param table Tabela PDF
     * @param text Tekst nagłówka
     * @param font Czcionka do użycia
     */
    private void addTableHeader(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(new BaseColor(211, 211, 211));
        cell.setPadding(5);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    /**
     * Dodaje komórkę do tabeli PDF.
     *
     * @param table Tabela PDF
     * @param text Tekst komórki
     * @param font Czcionka do użycia
     */
    private void addTableCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5);
        table.addCell(cell);
    }

    /**
     * Formatuje status zamówienia na język polski.
     *
     * @param status Status w formie tekstowej
     * @return Przetłumaczony status
     */
    private String formatStatus(String status) {
        switch (status.toLowerCase()) {
            case "pending": return "Oczekujące";
            case "in_progress": return "W trakcie";
            case "completed": return "Zakończone";
            case "canceled": return "Anulowane";
            default: return status;
        }
    }

    /**
     * Formatuje priorytet na język polski.
     *
     * @param priority Priorytet w formie tekstowej
     * @return Przetłumaczony priorytet
     */
    private String formatPriority(String priority) {
        switch (priority.toLowerCase()) {
            case "low": return "Niski";
            case "medium": return "Średni";
            case "high": return "Wysoki";
            default: return priority;
        }
    }

    /**
     * Dodaje szczegóły zamówienia do dokumentu PDF.
     *
     * @param document Dokument PDF
     * @param order Zamówienie
     * @param headerFont Czcionka nagłówków
     * @param contentFont Czcionka treści
     * @throws DocumentException W przypadku błędu podczas dodawania szczegółów
     */
    private void addOrderDetails(Document document, Order order, Font headerFont, Font contentFont) throws DocumentException {
        addSection(document, "Dane osoby zmarłej", headerFont);
        addField(document, "Imię i nazwisko:", 
                order.getCadaverFirstName() + " " + order.getCadaverLastName(), contentFont);
        addField(document, "Data urodzenia:", 
                order.getBirthDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), contentFont);
        addField(document, "Data zgonu:", 
                order.getDeathDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), contentFont);
        addField(document, "Numer aktu zgonu:", order.getDeathCertificateNumber(), contentFont);
    }
}