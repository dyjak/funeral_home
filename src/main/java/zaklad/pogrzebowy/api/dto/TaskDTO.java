package zaklad.pogrzebowy.api.dto;

import zaklad.pogrzebowy.api.models.Task;
import zaklad.pogrzebowy.api.models.Order;
import zaklad.pogrzebowy.api.models.User;
import zaklad.pogrzebowy.api.models.TaskAssignment;
import java.time.LocalDateTime;

/**
 * Klasa Data Transfer Object (DTO) reprezentująca zadanie (Task).
 * Umożliwia przenoszenie danych zadania między warstwami aplikacji
 * bez bezpośredniej ekspozycji encji.
 * 
 * Zawiera podstawowe informacje o zadaniu, takie jak nazwa, opis,
 * termin wykonania, priorytet, status oraz powiązane dane użytkownika i zamówienia.
 * 
 * Dodatkowo przechowuje datę przypisania zadania do użytkownika (assignedAt).
 */
public class TaskDTO {
    /**
     * Unikalny identyfikator zadania.
     */
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
     * Priorytet zadania (np. wysoki, średni, niski) w formie tekstowej.
     */
    private String priority;

    /**
     * Status zadania (np. nowy, w trakcie, zakończony) w formie tekstowej.
     */
    private String status;

    /**
     * Użytkownik przypisany do zadania (DTO).
     */
    private UserDTO assignedUser;

    /**
     * Zamówienie powiązane z zadaniem (DTO).
     */
    private OrderDTO order;

    /**
     * Data i godzina ostatniego przypisania zadania do użytkownika.
     */
    private LocalDateTime assignedAt;

    /**
     * Konstruktor domyślny.
     */
    public TaskDTO() {}

    /**
     * Konstruktor tworzący obiekt TaskDTO na podstawie encji Task.
     * 
     * @param task encja zadania, z której kopiowane są dane
     */
    public TaskDTO(Task task) {
        this.id = task.getId();
        this.taskName = task.getTaskName();
        this.description = task.getDescription();
        this.dueDate = task.getDueDate();
        this.priority = task.getPriority().toString();
        this.status = task.getStatus().toString();
        this.assignedUser = task.getAssignedUser() != null ? new UserDTO(task.getAssignedUser()) : null;
        this.order = task.getOrder() != null ? new OrderDTO(task.getOrder()) : null;
        this.assignedAt = task.getAssignments().stream()
            .map(TaskAssignment::getAssignedAt)
            .max(LocalDateTime::compareTo)
            .orElse(null);
    }

    // Gettery i settery z opisami

    /**
     * Pobiera identyfikator zadania.
     * 
     * @return id zadania
     */
    public Long getId() {
        return id;
    }

    /**
     * Ustawia identyfikator zadania.
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
     * @return priorytet w formie tekstowej
     */
    public String getPriority() {
        return priority;
    }

    /**
     * Ustawia priorytet zadania.
     * 
     * @param priority priorytet w formie tekstowej
     */
    public void setPriority(String priority) {
        this.priority = priority;
    }

    /**
     * Pobiera status zadania.
     * 
     * @return status w formie tekstowej
     */
    public String getStatus() {
        return status;
    }

    /**
     * Ustawia status zadania.
     * 
     * @param status status w formie tekstowej
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Pobiera użytkownika przypisanego do zadania.
     * 
     * @return obiekt UserDTO przypisanego użytkownika lub null
     */
    public UserDTO getAssignedUser() {
        return assignedUser;
    }

    /**
     * Ustawia użytkownika przypisanego do zadania.
     * 
     * @param assignedUser obiekt UserDTO przypisanego użytkownika
     */
    public void setAssignedUser(UserDTO assignedUser) {
        this.assignedUser = assignedUser;
    }

    /**
     * Pobiera zamówienie powiązane z zadaniem.
     * 
     * @return obiekt OrderDTO zamówienia lub null
     */
    public OrderDTO getOrder() {
        return order;
    }

    /**
     * Ustawia zamówienie powiązane z zadaniem.
     * 
     * @param order obiekt OrderDTO zamówienia
     */
    public void setOrder(OrderDTO order) {
        this.order = order;
    }

    /**
     * Pobiera datę i godzinę ostatniego przypisania zadania do użytkownika.
     * 
     * @return data przypisania lub null, jeśli brak przypisania
     */
    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    /**
     * Ustawia datę i godzinę przypisania zadania.
     * 
     * @param assignedAt data przypisania
     */
    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }

    /**
     * Konwertuje obiekt TaskDTO na encję Task.
     * Mapuje pola DTO na odpowiednie pola encji,
     * tworzy także powiązane obiekty User i Order z ich identyfikatorami.
     * 
     * @return obiekt encji Task odpowiadający temu DTO
     */
    public Task toEntity() {
        Task task = new Task();
        
        task.setId(this.id);
        task.setTaskName(this.taskName);
        task.setDescription(this.description);
        task.setDueDate(this.dueDate);
        
        if (this.priority != null && !this.priority.isEmpty()) {
            task.setPriority(Task.Priority.valueOf(this.priority.toLowerCase()));
        }
        if (this.status != null && !this.status.isEmpty()) {
            task.setStatus(Task.Status.valueOf(this.status.toLowerCase()));
        }
        
        if (this.order != null) {
            Order orderEntity = new Order();
            orderEntity.setId(this.order.getId());
            task.setOrder(orderEntity);
        }
        
        if (this.assignedUser != null) {
            User userEntity = new User();
            userEntity.setId(this.assignedUser.getId());
            task.setAssignedUser(userEntity);
        }
        
        return task;
    }
}
