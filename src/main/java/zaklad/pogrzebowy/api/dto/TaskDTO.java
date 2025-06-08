package zaklad.pogrzebowy.api.dto;

import zaklad.pogrzebowy.api.models.Task;
import zaklad.pogrzebowy.api.models.Order;
import zaklad.pogrzebowy.api.models.User;
import zaklad.pogrzebowy.api.models.TaskAssignment;
import java.time.LocalDateTime;

public class TaskDTO {
    private Long id;
    private String taskName;
    private String description;
    private LocalDateTime dueDate;
    private String priority;
    private String status;
    private UserDTO assignedUser;
    private OrderDTO order;
    private LocalDateTime assignedAt; // Add this field

    // Constructors
    public TaskDTO() {}

    public TaskDTO(Task task) {
        this.id = task.getId();
        this.taskName = task.getTaskName();
        this.description = task.getDescription();
        this.dueDate = task.getDueDate();
        this.priority = task.getPriority().toString();
        this.status = task.getStatus().toString();
        this.assignedUser = task.getAssignedUser() != null ? new UserDTO(task.getAssignedUser()) : null;
        this.order = task.getOrder() != null ? new OrderDTO(task.getOrder()) : null;
        // Get assignedAt from the most recent task assignment
        this.assignedAt = task.getAssignments().stream()
            .map(TaskAssignment::getAssignedAt)
            .max(LocalDateTime::compareTo)
            .orElse(null);
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public String getPriority() {
        return priority;
    }

    public String getStatus() {
        return status;
    }

    public UserDTO getAssignedUser() {
        return assignedUser;
    }

    public OrderDTO getOrder() {
        return order;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setAssignedUser(UserDTO assignedUser) {
        this.assignedUser = assignedUser;
    }

    public void setOrder(OrderDTO order) {
        this.order = order;
    }

    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }

    public Task toEntity() {
        Task task = new Task();
        
        // Set basic fields
        task.setId(this.id);
        task.setTaskName(this.taskName);
        task.setDescription(this.description);
        task.setDueDate(this.dueDate);
        
        // Only set enums if they have valid values
        if (this.priority != null && !this.priority.isEmpty()) {
            task.setPriority(Task.Priority.valueOf(this.priority.toUpperCase()));
        }
        if (this.status != null && !this.status.isEmpty()) {
            task.setStatus(Task.Status.valueOf(this.status.toUpperCase()));
        }
        
        // Handle relationships
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