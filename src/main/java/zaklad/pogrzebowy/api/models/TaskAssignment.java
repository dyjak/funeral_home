package zaklad.pogrzebowy.api.models;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;

/**
 * Klasa encji reprezentująca przypisanie zadania do użytkownika.
 * Mapowana na tabelę "task_assignments" w bazie danych.
 * 
 * Reprezentuje powiązanie między zadaniem (Task) a użytkownikiem (User),
 * zawiera informację o dacie przypisania zadania.
 */
@Entity
@Table(name = "task_assignments")
public class TaskAssignment {

    /**
     * Unikalny identyfikator przypisania, generowany automatycznie.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Zadanie, które zostało przypisane.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "task_id", nullable = false)
    @JsonIgnoreProperties({"assignments", "assignedUser", "order"})
    private Task task;

    /**
     * Użytkownik, do którego przypisano zadanie.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"assignedTasks", "passwordHash", "plainPassword", "email"})
    private User user;

    /**
     * Data i czas przypisania zadania do użytkownika.
     */
    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt;

    /**
     * Konstruktor bezparametrowy.
     * Ustawia datę przypisania na aktualny czas.
     */
    public TaskAssignment() {
        this.assignedAt = LocalDateTime.now();
    }

    /**
     * Konstruktor z parametrami.
     * 
     * @param task Zadanie, które jest przypisane
     * @param user Użytkownik, do którego przypisano zadanie
     * @param assignedAt Data przypisania
     */
    public TaskAssignment(Task task, User user, LocalDateTime assignedAt) {
        this.task = task;
        this.user = user;
        this.assignedAt = assignedAt;
    }

    /**
     * Pobiera unikalny identyfikator przypisania.
     * 
     * @return id przypisania
     */
    public Long getId() {
        return id;
    }

    /**
     * Ustawia unikalny identyfikator przypisania.
     * 
     * @param id id przypisania
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Pobiera zadanie przypisane w tym obiekcie.
     * 
     * @return zadanie
     */
    public Task getTask() {
        return task;
    }

    /**
     * Ustawia zadanie przypisane w tym obiekcie.
     * 
     * @param task zadanie
     */
    public void setTask(Task task) {
        this.task = task;
    }

    /**
     * Pobiera użytkownika, do którego przypisano zadanie.
     * 
     * @return użytkownik
     */
    public User getUser() {
        return user;
    }

    /**
     * Ustawia użytkownika, do którego przypisano zadanie.
     * 
     * @param user użytkownik
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Pobiera datę i czas przypisania zadania.
     * 
     * @return data i czas przypisania
     */
    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    /**
     * Ustawia datę i czas przypisania zadania.
     * 
     * @param assignedAt data i czas przypisania
     */
    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }
}
