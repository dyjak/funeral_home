package zaklad.pogrzebowy.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zaklad.pogrzebowy.api.models.User;  
import zaklad.pogrzebowy.api.repositories.UserRepository;  
import zaklad.pogrzebowy.api.models.Task;
import zaklad.pogrzebowy.api.security.JwtUtil;
import zaklad.pogrzebowy.api.services.TaskService;
import zaklad.pogrzebowy.api.dto.TaskDTO;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Kontroler REST do zarządzania zadaniami.
 *
 * Udostępnia następujące operacje na zasobie zadania:
 * <ul>
 *   <li>Pobieranie wszystkich zadań (GET /tasks)</li>
 *   <li>Pobieranie zadania po ID (GET /tasks/{id})</li>
 *   <li>Pobieranie zadań po statusie (GET /tasks/status/{status})</li>
 *   <li>Tworzenie nowego zadania (POST /tasks)</li>
 *   <li>Aktualizacja zadania (PUT /tasks/{id})</li>
 *   <li>Usuwanie zadania (DELETE /tasks/{id})</li>
 *   <li>Pobieranie zadań przypisanych do zalogowanego użytkownika (GET /tasks/assigned)</li>
 * </ul>
 *
 * Wstrzykiwane zależności:
 * <ul>
 *   <li>TaskService – logika biznesowa związana z zadaniami</li>
 *   <li>JwtUtil – obsługa tokenów JWT</li>
 *   <li>UserRepository – dostęp do danych użytkowników</li>
 * </ul>
 *
 * Kontroler umożliwia dostęp z dowolnego pochodzenia (CORS).
 */
@RestController
@RequestMapping("/tasks")
@CrossOrigin(origins = "*")
public class TaskController {

    @Autowired
    private TaskService taskService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserRepository userRepository;

    /**
     * Pobiera listę wszystkich zadań.
     * @return lista zadań w postaci DTO
     */
    @GetMapping
    public ResponseEntity<List<TaskDTO>> getAllTasks() {
        List<Task> tasks = taskService.findAll();
        List<TaskDTO> taskDTOs = tasks.stream()
            .map(TaskDTO::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(taskDTOs);
    }

    /**
     * Pobiera zadanie o podanym identyfikatorze.
     * @param id identyfikator zadania
     * @return zadanie w postaci DTO lub 404 jeśli nie znaleziono
     */
    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable Long id) {
        return taskService.findById(id)
            .map(task -> ResponseEntity.ok(new TaskDTO(task)))
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Pobiera zadania o określonym statusie.
     * @param status status zadania
     * @return lista zadań o danym statusie w postaci DTO
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<TaskDTO>> getTasksByStatus(@PathVariable Task.Status status) {
        List<Task> tasks = taskService.findByStatus(status);
        List<TaskDTO> taskDTOs = tasks.stream()
            .map(TaskDTO::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(taskDTOs);
    }

    /**
     * Tworzy nowe zadanie.
     * @param taskDTO dane nowego zadania
     * @return utworzone zadanie w postaci DTO lub błąd 400 w przypadku niepowodzenia
     */
    @PostMapping
    public ResponseEntity<TaskDTO> createTask(@RequestBody TaskDTO taskDTO) {
        try {
            Task task = taskDTO.toEntity();
            Task savedTask = taskService.create(task);
            return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new TaskDTO(savedTask));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .build();
        }
    }

    /**
     * Aktualizuje istniejące zadanie.
     * @param id identyfikator zadania
     * @param taskDTO nowe dane zadania
     * @return zaktualizowane zadanie w postaci DTO lub błąd 400 w przypadku niepowodzenia
     */
    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable Long id, @RequestBody TaskDTO taskDTO) {
        try {
            // Log received data
            System.out.println("Received update request for task " + id + ": " + taskDTO);
            
            // Convert DTO to entity
            Task taskToUpdate = taskDTO.toEntity();
            
            // Update the task
            Task updatedTask = taskService.update(id, taskToUpdate);
            
            // Convert back to DTO and return
            TaskDTO responseDTO = new TaskDTO(updatedTask);
            System.out.println("Sending response: " + responseDTO);
            
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Usuwa zadanie o podanym identyfikatorze.
     * @param id identyfikator zadania do usunięcia
     * @return odpowiedź bez treści lub 404 jeśli nie znaleziono zadania
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        try {
            taskService.delete(id);
            return ResponseEntity
                .noContent()
                .build();
        } catch (Exception e) {
            return ResponseEntity
                .notFound()
                .build();
        }
    }

    /**
     * Pobiera zadania przypisane do zalogowanego użytkownika na podstawie tokena JWT.
     * @param token nagłówek autoryzacyjny JWT
     * @return lista zadań przypisanych do użytkownika w postaci DTO lub błąd 401 w przypadku braku autoryzacji
     */
    @GetMapping("/assigned")
    public ResponseEntity<List<TaskDTO>> getAssignedTasks(@RequestHeader("Authorization") String token) {
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            String jwt = token.substring(7);
            String userEmail = jwtUtil.extractUsername(jwt);
            User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

            // Get tasks assigned to the user
            List<Task> tasks = taskService.findTasksAssignedToUser(user.getId());
            List<TaskDTO> taskDTOs = tasks.stream()
                .map(TaskDTO::new)
                .collect(Collectors.toList());

            return ResponseEntity.ok(taskDTOs);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}