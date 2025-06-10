package zaklad.pogrzebowy.api.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zaklad.pogrzebowy.api.models.Report;
import zaklad.pogrzebowy.api.repositories.ReportRepository;

import java.util.List;
import java.util.Optional;

/**
 * Serwis odpowiedzialny za zarządzanie raportami w systemie zakładu pogrzebowego.
 * Implementuje interfejs IReportService i dostarcza podstawowe operacje CRUD na raportach.
 *
 * @author INF_CZARNI
 * @version 1.0
 */
@Service
public class ReportService implements IReportService {

    /**
     * Repozytorium do operacji na raportach.
     */
    @Autowired
    private ReportRepository repository;

    /**
     * Pobiera wszystkie raporty z bazy danych.
     *
     * @return Lista wszystkich raportów
     */
    @Override
    public List<Report> findAll() {
        return repository.findAll();
    }

    /**
     * Wyszukuje raport po identyfikatorze.
     *
     * @param id Identyfikator raportu
     * @return Optional zawierający znaleziony raport lub pusty Optional
     */
    @Override
    public Optional<Report> findById(Long id) {
        return repository.findById(id);
    }

    /**
     * Tworzy nowy raport w systemie.
     *
     * @param report Nowy raport do utworzenia
     * @return Utworzony raport z przypisanym identyfikatorem
     */
    @Override
    public Report create(Report report) {
        return repository.save(report);
    }

    /**
     * Aktualizuje istniejący raport.
     *
     * @param id Identyfikator raportu do aktualizacji
     * @param updatedReport Zaktualizowane dane raportu
     * @return Zaktualizowany raport
     * @throws RuntimeException Gdy raport o podanym ID nie istnieje
     */
    @Override
    public Report update(Long id, Report updatedReport) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setReportType(updatedReport.getReportType());
                    existing.setGeneratedAt(updatedReport.getGeneratedAt());
                    existing.setFileUrl(updatedReport.getFileUrl());
                    existing.setGeneratedBy(updatedReport.getGeneratedBy());
                    return repository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Raport nie znaleziony"));
    }

    /**
     * Usuwa raport z systemu.
     *
     * @param id Identyfikator raportu do usunięcia
     */
    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}