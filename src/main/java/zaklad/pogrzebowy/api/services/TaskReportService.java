package zaklad.pogrzebowy.api.services;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zaklad.pogrzebowy.api.models.Task;
import zaklad.pogrzebowy.api.repositories.TaskRepository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Serwis odpowiedzialny za generowanie raportów PDF dla zadań w systemie zakładu pogrzebowego.
 * Umożliwia generowanie raportów z filtrowaniem według różnych kryteriów.
 *
 * @author INF_CZARNI
 * @version 1.0
 */
@Service
public class TaskReportService {

    /**
     * Repozytorium zadań używane do pobierania danych.
     */
    @Autowired
    private TaskRepository taskRepository;

    /**
     * Generuje raport PDF zawierający listę zadań według podanych filtrów.
     *
     * @param filters Mapa filtrów do zastosowania (status, priorytet, data, itp.)
     * @return Strumień bajtów zawierający wygenerowany dokument PDF
     * @throws RuntimeException Gdy wystąpi błąd podczas generowania PDF
     */
    public ByteArrayInputStream generateReport(Map<String, Object> filters) {
        List<Task> tasks = getFilteredTasks(filters);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, out);
            document.open();

            // Create fonts with encoding for Polish characters
            BaseFont baseFont = BaseFont.createFont(
                BaseFont.HELVETICA, 
                BaseFont.CP1250, 
                BaseFont.EMBEDDED
            );
            Font titleFont = new Font(baseFont, 18, Font.BOLD);
            Font headerFont = new Font(baseFont, 10, Font.BOLD);
            Font contentFont = new Font(baseFont, 9, Font.NORMAL);

            // Add title
            Paragraph title = new Paragraph("Raport zadań", titleFont);
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
            dateInfo.setSpacingAfter(15);
            document.add(dateInfo);

            // Create table
            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);
            
            // Set column widths
            float[] columnWidths = {0.5f, 2f, 1f, 1f, 1.5f, 2f, 1f};
            table.setWidths(columnWidths);

            // Add headers with Polish font
            addHeaderCell(table, "ID", headerFont);
            addHeaderCell(table, "Nazwa zadania", headerFont);
            addHeaderCell(table, "Status", headerFont);
            addHeaderCell(table, "Priorytet", headerFont);
            addHeaderCell(table, "Termin", headerFont);
            addHeaderCell(table, "Przypisany do", headerFont);
            addHeaderCell(table, "Zamówienie", headerFont);

            // Add data with Polish font
            for (Task task : tasks) {
                addCell(table, task.getId().toString(), contentFont);
                addCell(table, task.getTaskName(), contentFont);
                addCell(table, formatStatus(task.getStatus().toString()), contentFont);
                addCell(table, formatPriority(task.getPriority().toString()), contentFont);
                addCell(table, task.getDueDate() != null ? 
                    task.getDueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "Nie określono", contentFont);
                addCell(table, task.getAssignedUser() != null ? 
                    task.getAssignedUser().getFirstName() + " " + task.getAssignedUser().getLastName() : "Nie przypisano", contentFont);
                addCell(table, task.getOrder() != null ? "ID: " + task.getOrder().getId() : "Brak", contentFont);
            }

            document.add(table);
            document.close();

            return new ByteArrayInputStream(out.toByteArray());
        } catch (DocumentException | IOException e) {
            throw new RuntimeException("Błąd podczas generowania PDF", e);
        }
    }

    /**
     * Dodaje komórkę nagłówkową do tabeli PDF.
     *
     * @param table Tabela PDF
     * @param content Zawartość komórki
     * @param font Czcionka do użycia
     */
    private void addHeaderCell(PdfPTable table, String content, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setBackgroundColor(new BaseColor(51, 51, 51));
        cell.setPadding(5);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
    }

    /**
     * Dodaje standardową komórkę do tabeli PDF.
     *
     * @param table Tabela PDF
     * @param content Zawartość komórki
     * @param font Czcionka do użycia
     */
    private void addCell(PdfPTable table, String content, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setPadding(5);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
    }

    /**
     * Filtruje zadania według podanych kryteriów.
     *
     * @param filters Mapa filtrów do zastosowania
     * @return Lista przefiltrowanych zadań
     */
    private List<Task> getFilteredTasks(Map<String, Object> filters) {
        List<Task> allTasks = taskRepository.findAll();

        return allTasks.stream()
                .filter(task -> {
                    if (filters.get("orderId") != null &&
                            (task.getOrder() == null || !task.getOrder().getId().equals(
                                    Long.valueOf(filters.get("orderId").toString())))) {
                        return false;
                    }

                    if (filters.get("status") != null && !filters.get("status").toString().isEmpty() &&
                            !task.getStatus().toString().equals(filters.get("status").toString())) {
                        return false;
                    }

                    if (filters.get("priority") != null && !filters.get("priority").toString().isEmpty() &&
                            !task.getPriority().toString().equals(filters.get("priority").toString())) {
                        return false;
                    }

                    if (filters.get("employeeId") != null &&
                            (task.getAssignedUser() == null || !task.getAssignedUser().getId().equals(
                                    Long.valueOf(filters.get("employeeId").toString())))) {
                        return false;
                    }

                    if (filters.get("name") != null && !filters.get("name").toString().isEmpty() &&
                            !task.getTaskName().toLowerCase().contains(filters.get("name").toString().toLowerCase())) {
                        return false;
                    }

                    if (filters.get("dateFrom") != null && !filters.get("dateFrom").toString().isEmpty()
                            && task.getDueDate() != null) {
                        if (task.getDueDate().toLocalDate().isBefore(
                                java.time.LocalDate.parse(filters.get("dateFrom").toString()))) {
                            return false;
                        }
                    }

                    if (filters.get("dateTo") != null && !filters.get("dateTo").toString().isEmpty()
                            && task.getDueDate() != null) {
                        if (task.getDueDate().toLocalDate().isAfter(
                                java.time.LocalDate.parse(filters.get("dateTo").toString()))) {
                            return false;
                        }
                    }

                    return true;
                })
                .collect(Collectors.toList());
    }

    /**
     * Formatuje status zadania na język polski.
     *
     * @param status Status w formie tekstowej (angielskiej)
     * @return Przetłumaczony status w języku polskim
     */
    private String formatStatus(String status) {
        switch (status.toLowerCase()) {
            case "pending":
                return "Oczekujące";
            case "in_progress":
                return "W trakcie";
            case "completed":
                return "Zakończone";
            case "canceled":
                return "Anulowane";
            default:
                return status;
        }
    }

    /**
     * Formatuje priorytet zadania na język polski.
     *
     * @param priority Priorytet w formie tekstowej (angielskiej)
     * @return Przetłumaczony priorytet w języku polskim
     */
    private String formatPriority(String priority) {
        switch (priority.toLowerCase()) {
            case "low":
                return "Niski";
            case "medium":
                return "Średni";
            case "high":
                return "Wysoki";
            default:
                return priority;
        }
    }
}