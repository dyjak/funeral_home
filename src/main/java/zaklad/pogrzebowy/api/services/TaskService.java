package zaklad.pogrzebowy.api.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zaklad.pogrzebowy.api.models.Task;
import zaklad.pogrzebowy.api.models.Order;
import zaklad.pogrzebowy.api.models.User;
import zaklad.pogrzebowy.api.models.TaskAssignment;
import zaklad.pogrzebowy.api.repositories.TaskRepository;
import zaklad.pogrzebowy.api.repositories.OrderRepository;
import zaklad.pogrzebowy.api.repositories.UserRepository;
import zaklad.pogrzebowy.api.repositories.TaskAssignmentRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Serwis odpowiedzialny za zarządzanie zadaniami w systemie zakładu pogrzebowego.
 * Implementuje interfejs ITaskService i obsługuje operacje CRUD na zadaniach oraz ich powiązaniach.
 * 
 * @author INF_CZARNI
 * @version 1.0
 */
@Service
@Transactional
public class TaskService implements ITaskService {

    /**
     * Repozytorium zadań.
     */
    @Autowired
    private TaskRepository taskRepository;
    
    /**
     * Repozytorium zamówień.
     */
    @Autowired
    private OrderRepository orderRepository;
    
    /**
     * Repozytorium użytkowników.
     */
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Repozytorium przypisań zadań.
     */
    @Autowired
    private TaskAssignmentRepository taskAssignmentRepository;

    /**
     * Pobiera wszystkie zadania z systemu.
     *
     * @return Lista wszystkich zadań
     */
    @Override
    @Transactional(readOnly = true)
    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    /**
     * Wyszukuje zadanie po identyfikatorze.
     *
     * @param id Identyfikator zadania
     * @return Optional zawierający znalezione zadanie lub pusty Optional
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Task> findById(Long id) {
        return taskRepository.findById(id);
    }

    /**
     * Wyszukuje zadania o określonym statusie.
     *
     * @param status Status zadań do wyszukania
     * @return Lista zadań o podanym statusie
     */
    @Override
    @Transactional(readOnly = true)
    public List<Task> findByStatus(Task.Status status) {
        return taskRepository.findByStatus(status);
    }

    /**
     * Tworzy nowe zadanie w systemie.
     * Obsługuje powiązania z zamówieniem i użytkownikiem oraz tworzy wpis o przypisaniu zadania.
     *
     * @param task Nowe zadanie do utworzenia
     * @return Utworzone zadanie z przypisanym identyfikatorem
     * @throws RuntimeException Gdy wystąpi błąd podczas tworzenia zadania
     */
    @Override
    @Transactional
    public Task create(Task task) {
        try {
            // Create a new Task entity
            Task newTask = new Task();
            newTask.setTaskName(task.getTaskName());
            newTask.setDescription(task.getDescription());
            newTask.setDueDate(task.getDueDate());
            newTask.setPriority(task.getPriority());
            newTask.setStatus(task.getStatus());
    
            // Handle order association
            if (task.getOrder() != null && task.getOrder().getId() != null) {
                Order order = orderRepository.findById(task.getOrder().getId())
                    .orElseThrow(() -> new RuntimeException("Order not found with id: " + task.getOrder().getId()));
                newTask.setOrder(order);
                System.out.println("Setting order: " + order.getId());
            }
    
            // Handle user assignment
            if (task.getAssignedUser() != null && task.getAssignedUser().getId() != null) {
                User user = userRepository.findById(task.getAssignedUser().getId())
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + task.getAssignedUser().getId()));
                newTask.setAssignedUser(user);
                System.out.println("Setting user: " + user.getId());
            }
    
            // Save the task only once after setting all relationships
            Task savedTask = taskRepository.save(newTask);
            System.out.println("Saved task ID: " + savedTask.getId());
            System.out.println("Saved task order ID: " + (savedTask.getOrder() != null ? savedTask.getOrder().getId() : "null"));
            System.out.println("Saved task user ID: " + (savedTask.getAssignedUser() != null ? savedTask.getAssignedUser().getId() : "null"));
    
            // Create task assignment if user is assigned
            if (savedTask.getAssignedUser() != null) {
                TaskAssignment assignment = new TaskAssignment();
                assignment.setTask(savedTask);
                assignment.setUser(savedTask.getAssignedUser());
                assignment.setAssignedAt(LocalDateTime.now());
                
                TaskAssignment savedAssignment = taskAssignmentRepository.save(assignment);
                System.out.println("Saved task assignment ID: " + savedAssignment.getId());
            }
    
            return savedTask; // Return the task without saving it again
        } catch (Exception e) {
            System.out.println("Error creating task: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error creating task: " + e.getMessage(), e);
        }
    }

    /**
     * Aktualizuje istniejące zadanie.
     * Obsługuje zmianę powiązań z zamówieniem i użytkownikiem oraz aktualizuje wpisy o przypisaniu zadania.
     *
     * @param id Identyfikator zadania do aktualizacji
     * @param updatedTask Zaktualizowane dane zadania
     * @return Zaktualizowane zadanie
     * @throws RuntimeException Gdy zadanie nie istnieje lub wystąpi błąd podczas aktualizacji
     */
    @Override
    @Transactional
    public Task update(Long id, Task updatedTask) {
        try {
            Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));

            System.out.println("Existing task before update: " + existingTask);
            System.out.println("Update request data: " + updatedTask);

            // Update fields only if they are present in the request
            if (updatedTask.getTaskName() != null) {
                existingTask.setTaskName(updatedTask.getTaskName());
            }
            if (updatedTask.getDescription() != null) {
                existingTask.setDescription(updatedTask.getDescription());
            }
            if (updatedTask.getDueDate() != null) {
                existingTask.setDueDate(updatedTask.getDueDate());
            }
            if (updatedTask.getPriority() != null) {
                existingTask.setPriority(updatedTask.getPriority());
            }
            if (updatedTask.getStatus() != null) {
                existingTask.setStatus(updatedTask.getStatus());
            }

            // Handle order relationship
            if (updatedTask.getOrder() != null) {
                if (updatedTask.getOrder().getId() != null) {
                    Order order = orderRepository.findById(updatedTask.getOrder().getId())
                        .orElseThrow(() -> new RuntimeException("Order not found"));
                    existingTask.setOrder(order);
                } else {
                    existingTask.setOrder(null);
                }
            }

            // Handle user assignment
            if (updatedTask.getAssignedUser() != null) {
                handleUserAssignment(existingTask, updatedTask.getAssignedUser());
            }

            Task savedTask = taskRepository.save(existingTask);
            System.out.println("Task after update: " + savedTask);
            return savedTask;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to update task: " + e.getMessage(), e);
        }
    }

    /**
     * Metoda pomocnicza do obsługi przypisania użytkownika do zadania.
     * Tworzy lub usuwa wpisy o przypisaniu zadania w zależności od przekazanych danych.
     *
     * @param existingTask Istniejące zadanie
     * @param newUser Nowy użytkownik do przypisania lub null aby usunąć przypisanie
     * @throws RuntimeException Gdy wystąpi błąd podczas obsługi przypisania
     */
    private void handleUserAssignment(Task existingTask, User newUser) {
        try {
            if (newUser.getId() != null) {
                User user = userRepository.findById(newUser.getId())
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + newUser.getId()));
                
                // Update user assignment
                existingTask.setAssignedUser(user);
                
                // Update task assignment record
                taskAssignmentRepository.deleteAllByTaskId(existingTask.getId());
                TaskAssignment assignment = new TaskAssignment();
                assignment.setTask(existingTask);
                assignment.setUser(user);
                assignment.setAssignedAt(LocalDateTime.now());
                taskAssignmentRepository.save(assignment);
                
                System.out.println("Updated user assignment: " + user.getId());
            } else {
                // Remove user assignment
                existingTask.setAssignedUser(null);
                taskAssignmentRepository.deleteAllByTaskId(existingTask.getId());
                System.out.println("Removed user assignment");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error handling user assignment: " + e.getMessage(), e);
        }
    }

    /**
     * Usuwa zadanie z systemu.
     * Czyści powiązania z użytkownikiem i zamówieniem przed usunięciem.
     *
     * @param id Identyfikator zadania do usunięcia
     * @throws RuntimeException Gdy zadanie nie istnieje
     */
    @Override
    public void delete(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        // Clear the assigned user reference
        if (task.getAssignedUser() != null) {
            task.setAssignedUser(null);
            taskRepository.save(task);
        }

        // Clear the order reference if exists
        if (task.getOrder() != null) {
            task.setOrder(null);
            taskRepository.save(task);
        }

        taskRepository.deleteById(id);
    }

    /**
     * Wyszukuje zadania przypisane do określonego użytkownika.
     *
     * @param userId Identyfikator użytkownika
     * @return Lista zadań przypisanych do użytkownika
     */
    @Override
    @Transactional(readOnly = true)
    public List<Task> findTasksAssignedToUser(Long userId) {
        return taskRepository.findByAssignedUserId(userId);
    }
}