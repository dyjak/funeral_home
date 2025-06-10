package zaklad.pogrzebowy.api.dto;

import zaklad.pogrzebowy.api.models.TaskAssignment;
import java.time.LocalDateTime;

/**
 * Obiekt Transferu Danych (DTO) dla encji {@link TaskAssignment}.
 * Reprezentuje przypisanie zadania do użytkownika wraz z dodatkowymi informacjami do wyświetlania.
 */
public class TaskAssignmentDTO {
    private Long id;
    private Long taskId;
    private Long userId;
    private LocalDateTime assignedAt;
    private String taskName;
    private String userName;

    /**
     * Domyślny konstruktor.
     */
    public TaskAssignmentDTO() {}

    /**
     * Konstruktor tworzący TaskAssignmentDTO na podstawie encji TaskAssignment.
     * Automatycznie pobiera i łączy informacje z powiązanych encji (nazwa zadania, pełna nazwa użytkownika).
     *
     * @param assignment encja TaskAssignment do konwersji na DTO
     */
    public TaskAssignmentDTO(TaskAssignment assignment) {
        this.id = assignment.getId();
        this.taskId = assignment.getTask().getId();
        this.userId = assignment.getUser().getId();
        this.assignedAt = assignment.getAssignedAt();
        this.taskName = assignment.getTask().getTaskName();
        this.userName = assignment.getUser().getFirstName() + " " + assignment.getUser().getLastName();
    }

    /**
     * Pobiera ID przypisania.
     *
     * @return ID przypisania
     */
    public Long getId() {
        return id;
    }

    /**
     * Ustawia ID przypisania.
     *
     * @param id ID przypisania do ustawienia
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Pobiera ID przypisanego zadania.
     *
     * @return ID zadania
     */
    public Long getTaskId() {
        return taskId;
    }

    /**
     * Ustawia ID przypisanego zadania.
     *
     * @param taskId ID zadania do ustawienia
     */
    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    /**
     * Pobiera ID przypisanego użytkownika.
     *
     * @return ID użytkownika
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * Ustawia ID przypisanego użytkownika.
     *
     * @param userId ID użytkownika do ustawienia
     */
    public void setUserId(Long userId) {
        this.userId = userId;
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
     * @param assignedAt data i czas przypisania do ustawienia
     */
    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }

    /**
     * Pobiera nazwę przypisanego zadania.
     *
     * @return nazwa zadania
     */
    public String getTaskName() {
        return taskName;
    }

    /**
     * Ustawia nazwę przypisanego zadania.
     *
     * @param taskName nazwa zadania do ustawienia
     */
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    /**
     * Pobiera imię i nazwisko przypisanego użytkownika.
     *
     * @return pełna nazwa użytkownika
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Ustawia imię i nazwisko przypisanego użytkownika.
     *
     * @param userName pełna nazwa użytkownika do ustawienia
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }
}