package zaklad.pogrzebowy.api.db;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import zaklad.pogrzebowy.api.controllers.TaskController;
import zaklad.pogrzebowy.api.dto.TaskDTO;
import zaklad.pogrzebowy.api.models.Task;
import zaklad.pogrzebowy.api.repositories.TaskRepository;
import zaklad.pogrzebowy.api.services.TaskService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;



class TaskControllerTest {
    private static final Logger logger = LoggerFactory.getLogger(TaskControllerTest.class);

    @InjectMocks
    private TaskController taskController;

    @Mock
    private TaskService taskService;

    @Mock
    private TaskRepository taskRepository;

    @Captor
    private ArgumentCaptor<Task> taskCaptor;

    private Task testTask;
    private TaskDTO testTaskDTO;

    @BeforeEach
    void setUp() {
        logger.info("Inicjalizacja mocków dla testów TaskController");
        MockitoAnnotations.openMocks(this);

        // Setup test task
        testTask = new Task();
        testTask.setId(1L);
        testTask.setTaskName("Test Task");
        testTask.setDescription("Test Description");
        testTask.setStatus(Task.Status.pending);
        testTask.setPriority(Task.Priority.medium);

        // Setup test DTO
        testTaskDTO = new TaskDTO(testTask);
    }

    @Test
    @DisplayName("Gdy aktualizujemy tylko status zadania, pozostałe pola nie powinny się zmienić")
    void whenUpdatingOnlyStatus_otherFieldsShouldRemainUnchanged() {
        // Arrange
        logger.info("Przygotowanie danych do aktualizacji statusu");
        TaskDTO updateRequest = new TaskDTO();
        updateRequest.setId(1L);
        updateRequest.setStatus("in_progress");

        when(taskService.update(eq(1L), any(Task.class))).thenAnswer(invocation -> {
            Task updatedTask = invocation.getArgument(1);
            testTask.setStatus(Task.Status.valueOf(updateRequest.getStatus()));
            return testTask;
        });

        // Act
        logger.info("Wywołanie metody updateTask");
        ResponseEntity<TaskDTO> response = taskController.updateTask(1L, updateRequest);

        // Assert
        logger.info("Weryfikacja odpowiedzi");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test Task", response.getBody().getTaskName());
        assertEquals("Test Description", response.getBody().getDescription());
        assertEquals("in_progress", response.getBody().getStatus());
        assertEquals("medium", response.getBody().getPriority());

        verify(taskService).update(eq(1L), taskCaptor.capture());
        Task capturedTask = taskCaptor.getValue();
        assertEquals("in_progress", capturedTask.getStatus().toString());
        
        logger.info("Test zakończony pomyślnie");
    }

    @Test
    @DisplayName("DTO to Entity conversion should preserve non-null fields")
    void whenConvertingDtoToEntity_shouldPreserveNonNullFields() {
        // Arrange
        logger.info("Przygotowanie DTO z częściowymi danymi");
        TaskDTO partialUpdate = new TaskDTO();
        partialUpdate.setId(1L);
        partialUpdate.setTaskName("Updated Name");
        
        // Act
        logger.info("Konwersja DTO na encję");
        Task entity = partialUpdate.toEntity();
        
        // Assert
        logger.info("Weryfikacja konwersji");
        assertEquals("Updated Name", entity.getTaskName());
        assertNull(entity.getDescription());
        assertNull(entity.getStatus());
        assertNull(entity.getPriority());
        
        logger.info("Test zakończony pomyślnie");
    }

    @Test
    @DisplayName("Service should only update provided fields")
    void whenUpdatingTask_shouldOnlyUpdateProvidedFields() {
        // Arrange
        logger.info("Przygotowanie częściowej aktualizacji");
        Task existingTask = new Task();
        existingTask.setId(1L);
        existingTask.setTaskName("Original Name");
        existingTask.setDescription("Original Description");
        existingTask.setStatus(Task.Status.pending);
        existingTask.setPriority(Task.Priority.medium);

        TaskDTO updateRequest = new TaskDTO();
        updateRequest.setId(1L);
        updateRequest.setTaskName("Updated Name");

        when(taskService.findById(1L)).thenReturn(java.util.Optional.of(existingTask));
        when(taskService.update(eq(1L), any(Task.class))).thenReturn(existingTask);

        // Act
        logger.info("Wywołanie metody updateTask");
        ResponseEntity<TaskDTO> response = taskController.updateTask(1L, updateRequest);

        // Assert
        logger.info("Weryfikacja aktualizacji");
        verify(taskService).update(eq(1L), taskCaptor.capture());
        Task capturedTask = taskCaptor.getValue();
        
        assertEquals("Updated Name", capturedTask.getTaskName());
        assertNull(capturedTask.getDescription());
        assertNull(capturedTask.getStatus());
        assertNull(capturedTask.getPriority());
        
        logger.info("Test zakończony pomyślnie");
    }

    @Test
    @DisplayName("TaskDTO should properly convert between DTO and Entity")
    void taskDtoConversionShouldWorkCorrectly() {
        // Test Entity to DTO
        Task task = new Task();
        task.setId(1L);
        task.setTaskName("Test Task");
        task.setDescription("Test Description");
        task.setStatus(Task.Status.pending);
        task.setPriority(Task.Priority.medium);

        TaskDTO dto = new TaskDTO(task);
        assertEquals(1L, dto.getId());
        assertEquals("Test Task", dto.getTaskName());
        assertEquals("Test Description", dto.getDescription());
        assertEquals("pending", dto.getStatus());
        assertEquals("medium", dto.getPriority());

        // Test DTO to Entity
        Task convertedTask = dto.toEntity();
        assertEquals(1L, convertedTask.getId());
        assertEquals("Test Task", convertedTask.getTaskName());
        assertEquals("Test Description", convertedTask.getDescription());
        assertEquals(Task.Status.pending, convertedTask.getStatus());
        assertEquals(Task.Priority.medium, convertedTask.getPriority());
    }

    @Test
    @DisplayName("TaskService should handle partial updates correctly")
    void taskServiceShouldHandlePartialUpdates() {
        // Arrange
        logger.info("Przygotowanie częściowej aktualizacji");
        Task existingTask = new Task();
        existingTask.setId(1L);
        existingTask.setTaskName("Original Name");
        existingTask.setDescription("Original Description");
        existingTask.setStatus(Task.Status.pending);
        existingTask.setPriority(Task.Priority.medium);

        TaskDTO updateRequest = new TaskDTO();
        updateRequest.setId(1L);
        updateRequest.setTaskName("New Name");
        updateRequest.setStatus("in_progress");

        // Mock service behavior
        when(taskService.update(eq(1L), any(Task.class))).thenAnswer(invocation -> {
            Task updatedTask = invocation.getArgument(1);
            existingTask.setTaskName(updatedTask.getTaskName());
            existingTask.setStatus(Task.Status.valueOf(updateRequest.getStatus()));
            return existingTask;
        });

        // Act
        logger.info("Wywołanie metody updateTask");
        ResponseEntity<TaskDTO> response = taskController.updateTask(1L, updateRequest);

        // Assert
        logger.info("Weryfikacja aktualizacji");
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("New Name", response.getBody().getTaskName());
        assertEquals("Original Description", response.getBody().getDescription());
        assertEquals("in_progress", response.getBody().getStatus());
        assertEquals("medium", response.getBody().getPriority());

        // Verify service was called with correct data
        verify(taskService).update(eq(1L), taskCaptor.capture());
        Task capturedTask = taskCaptor.getValue();
        assertEquals("New Name", capturedTask.getTaskName());
        assertEquals("in_progress", capturedTask.getStatus().toString());
        
        logger.info("Test zakończony pomyślnie");
    }

    @Test
    @DisplayName("Should handle empty string values correctly")
    void shouldHandleEmptyStrings() {
        // Arrange 
        TaskDTO updateRequest = new TaskDTO();
        updateRequest.setId(1L);
        updateRequest.setTaskName("");
        updateRequest.setDescription("");

        when(taskService.update(eq(1L), any(Task.class))).thenAnswer(invocation -> {
            Task updatedTask = invocation.getArgument(1);
            testTask.setTaskName(updatedTask.getTaskName());
            testTask.setDescription(updatedTask.getDescription());
            return testTask;
        });

        // Act
        ResponseEntity<TaskDTO> response = taskController.updateTask(1L, updateRequest);

        // Assert
        verify(taskService).update(eq(1L), taskCaptor.capture());
        Task capturedTask = taskCaptor.getValue();
        
        assertNotNull(capturedTask.getTaskName());
        assertNotNull(capturedTask.getDescription());
        assertEquals("", capturedTask.getTaskName());
        assertEquals("", capturedTask.getDescription());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        assertNotNull(response.getBody());
        assertEquals("", response.getBody().getTaskName());
        assertEquals("", response.getBody().getDescription());
    }
}