package zaklad.pogrzebowy.api.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Klasa encji reprezentująca zadanie (Task) w systemie.
 * Mapowana na tabelę "tasks" w bazie danych.
 * 
 * Reprezentuje pojedyncze zadanie z przypisanymi właściwościami
 * takimi jak nazwa, opis, termin realizacji, priorytet, status,
 * a także powiązania z użytkownikiem, zleceniem oraz przypisaniami.
 */
@Entity
@Table(name = "tasks")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Task {

    /**
     * Unikalny identyfikator zadania, generowany automatycznie.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nazwa zadania.
     */
    private String taskName;

    /**
     * Opis zadania.
     */
    private String description;

    /**
     * Termin wykonania zadania.
     */
    private LocalDateTime dueDate;

    /**
     * Priorytet zadania.
     */
    @Enumerated(EnumType.STRING)
    private Priority priority;

    /**
     * Status realizacji zadania.
     */
    @Enumerated(EnumType.STRING)
    private Status status;

    /**
     * Użytkownik, któremu zadanie jest przypisane.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = true)
    @JsonIgnoreProperties({"assignedTasks", "passwordHash", "plainPassword", "email"})
    private User assignedUser;

    /**
     * Zlecenie powiązane z tym zadaniem.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id", nullable = true)
    @JsonIgnoreProperties({"tasks"})
    private Order order;

    /**
     * Zbiór przypisań (TaskAssignment) powiązanych z zadaniem.
     */
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("task")
    private Set<TaskAssignment> assignments = new HashSet<>();

    /**
     * Możliwe poziomy priorytetu zadania.
     */
    public enum Priority {
        low,       // niski
        medium,    // średni
        high       // wysoki
    }

    /**
     * Możliwe statusy zadania.
     */
    public enum Status {
        pending,     // oczekujące
        in_progress, // w trakcie realizacji
        completed,   // zakończone
        canceled     // anulowane
    }

    /**
     * Pobiera unikalny identyfikator zadania.
     * 
     * @return id zadania
     */
    public Long getId() {
        return id;
    }

    /**
     * Ustawia unikalny identyfikator zadania.
     * 
     * @param id id zadania
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Pobiera nazwę zadania.
     * 
     * @return nazwa zadania
     */
    public String getTaskName() {
        return taskName;
    }

    /**
     * Ustawia nazwę zadania.
     * 
     * @param taskName nazwa zadania
     */
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    /**
     * Pobiera opis zadania.
     * 
     * @return opis zadania
     */
    public String getDescription() {
        return description;
    }

    /**
     * Ustawia opis zadania.
     * 
     * @param description opis zadania
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Pobiera termin wykonania zadania.
     * 
     * @return termin wykonania
     */
    public LocalDateTime getDueDate() {
        return dueDate;
    }

    /**
     * Ustawia termin wykonania zadania.
     * 
     * @param dueDate termin wykonania
     */
    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    /**
     * Pobiera priorytet zadania.
     * 
     * @return priorytet
     */
    public Priority getPriority() {
        return priority;
    }

    /**
     * Ustawia priorytet zadania.
     * 
     * @param priority priorytet
     */
    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    /**
     * Pobiera status realizacji zadania.
     * 
     * @return status zadania
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Ustawia status realizacji zadania.
     * 
     * @param status status zadania
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Pobiera użytkownika, któremu zadanie jest przypisane.
     * 
     * @return użytkownik przypisany do zadania
     */
    public User getAssignedUser() {
        return assignedUser;
    }

    /**
     * Ustawia użytkownika przypisanego do zadania.
     * 
     * @param assignedUser użytkownik
     */
    public void setAssignedUser(User assignedUser) {
        this.assignedUser = assignedUser;
    }

    /**
     * Pobiera zlecenie powiązane z tym zadaniem.
     * 
     * @return zlecenie
     */
    public Order getOrder() {
        return order;
    }

    /**
     * Ustawia zlecenie powiązane z tym zadaniem.
     * 
     * @param order zlecenie
     */
    public void setOrder(Order order) {
        this.order = order;
    }

    /**
     * Pobiera zbiór przypisań powiązanych z zadaniem.
     * 
     * @return zbiór przypisań
     */
    public Set<TaskAssignment> getAssignments() {
        return assignments;
    }

    /**
     * Ustawia zbiór przypisań powiązanych z zadaniem.
     * 
     * @param assignments zbiór przypisań
     */
    public void setAssignments(Set<TaskAssignment> assignments) {
        this.assignments = assignments;
    }
}
