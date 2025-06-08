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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @GetMapping
    public ResponseEntity<List<TaskDTO>> getAllTasks() {
        List<Task> tasks = taskService.findAll();
        List<TaskDTO> taskDTOs = tasks.stream()
            .map(TaskDTO::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(taskDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable Long id) {
        return taskService.findById(id)
            .map(task -> ResponseEntity.ok(new TaskDTO(task)))
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<TaskDTO>> getTasksByStatus(@PathVariable Task.Status status) {
        List<Task> tasks = taskService.findByStatus(status);
        List<TaskDTO> taskDTOs = tasks.stream()
            .map(TaskDTO::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(taskDTOs);
    }

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