package zaklad.pogrzebowy.api.controllers;

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
import zaklad.pogrzebowy.api.models.Client;
import zaklad.pogrzebowy.api.services.ClientService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ClientControllerTest {
    private static final Logger logger = LoggerFactory.getLogger(ClientControllerTest.class);

    @InjectMocks
    private ClientController clientController;

    @Mock
    private ClientService clientService;

    @Captor
    private ArgumentCaptor<Client> clientCaptor;

    @Captor
    private ArgumentCaptor<Long> idCaptor;

    private Client testClient;
    private Client testClient2;

    @BeforeEach
    void setUp() {
        logger.info("Inicjalizacja mocków dla testów ClientController");
        MockitoAnnotations.openMocks(this);

        testClient = new Client();
        testClient.setId(1L);
        testClient.setFirstName("Jan");
        testClient.setLastName("Kowalski");
        testClient.setPhone("123456789");

        testClient2 = new Client();
        testClient2.setId(2L);
        testClient2.setFirstName("Anna");
        testClient2.setLastName("Nowak");
        testClient2.setPhone("987654321");
    }

    @Test
    @DisplayName("Gdy wywołano findAll, powinien zwrócić listę wszystkich klientów")
    void whenFindAllCalled_shouldReturnAllClients() {
        // Arrange
        logger.info("Przygotowanie danych testowych dla pobrania wszystkich klientów");
        List<Client> expectedClients = Arrays.asList(testClient, testClient2);
        when(clientService.findAll()).thenReturn(expectedClients);

        // Act
        logger.info("Wywołanie metody findAll");
        List<Client> actualClients = clientController.findAll();

        // Assert
        logger.info("Weryfikacja odpowiedzi");
        assertNotNull(actualClients);
        assertEquals(2, actualClients.size());
        assertEquals(expectedClients, actualClients);
        assertEquals("Jan", actualClients.get(0).getFirstName());
        assertEquals("Anna", actualClients.get(1).getFirstName());

        verify(clientService, times(1)).findAll();

        logger.info("Test zakończony pomyślnie");
    }

    @Test
    @DisplayName("Gdy wywołano findAll i brak klientów, powinien zwrócić pustą listę")
    void whenFindAllCalledAndNoClients_shouldReturnEmptyList() {
        // Arrange
        logger.info("Przygotowanie danych testowych dla pustej listy klientów");
        List<Client> expectedClients = Arrays.asList();
        when(clientService.findAll()).thenReturn(expectedClients);

        // Act
        logger.info("Wywołanie metody findAll dla pustej listy");
        List<Client> actualClients = clientController.findAll();

        // Assert
        logger.info("Weryfikacja odpowiedzi");
        assertNotNull(actualClients);
        assertEquals(0, actualClients.size());
        assertTrue(actualClients.isEmpty());

        verify(clientService, times(1)).findAll();

        logger.info("Test zakończony pomyślnie");
    }

    @Test
    @DisplayName("Gdy wywołano create z poprawnymi danymi, powinien utworzyć nowego klienta")
    void whenCreateCalledWithValidData_shouldCreateNewClient() {
        // Arrange
        logger.info("Przygotowanie danych testowych dla tworzenia klienta");
        Client newClient = new Client();
        newClient.setFirstName("Piotr");
        newClient.setLastName("Nowak");
        newClient.setPhone("555666777");

        Client createdClient = new Client();
        createdClient.setId(3L);
        createdClient.setFirstName("Piotr");
        createdClient.setLastName("Nowak");
        createdClient.setPhone("555666777");

        when(clientService.create(any(Client.class))).thenReturn(createdClient);

        // Act
        logger.info("Wywołanie metody create");
        Client result = clientController.create(newClient);

        // Assert
        logger.info("Weryfikacja odpowiedzi");
        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals("Piotr", result.getFirstName());
        assertEquals("Nowak", result.getLastName());
        assertEquals("555666777", result.getPhone());

        verify(clientService, times(1)).create(clientCaptor.capture());
        Client capturedClient = clientCaptor.getValue();
        assertEquals("Piotr", capturedClient.getFirstName());
        assertEquals("Nowak", capturedClient.getLastName());

        logger.info("Test zakończony pomyślnie");
    }

    @Test
    @DisplayName("Gdy wywołano create z pustymi danymi, powinien przekazać dane do serwisu")
    void whenCreateCalledWithEmptyData_shouldPassDataToService() {
        // Arrange
        logger.info("Przygotowanie danych testowych dla tworzenia klienta z pustymi danymi");
        Client emptyClient = new Client();

        when(clientService.create(any(Client.class))).thenReturn(emptyClient);

        // Act
        logger.info("Wywołanie metody create z pustymi danymi");
        Client result = clientController.create(emptyClient);

        // Assert
        logger.info("Weryfikacja odpowiedzi");
        assertNotNull(result);
        verify(clientService, times(1)).create(emptyClient);

        logger.info("Test zakończony pomyślnie");
    }

    @Test
    @DisplayName("Gdy wywołano update z poprawnymi danymi, powinien zaktualizować klienta")
    void whenUpdateCalledWithValidData_shouldUpdateClient() {
        // Arrange
        logger.info("Przygotowanie danych testowych dla aktualizacji klienta");
        Long clientId = 1L;
        Client updatedClientData = new Client();
        updatedClientData.setFirstName("Jan");
        updatedClientData.setLastName("Kowalski-Nowak");
        updatedClientData.setPhone("111222333");

        Client updatedClient = new Client();
        updatedClient.setId(clientId);
        updatedClient.setFirstName("Jan");
        updatedClient.setLastName("Kowalski-Nowak");
        updatedClient.setPhone("111222333");

        when(clientService.update(eq(clientId), any(Client.class))).thenReturn(updatedClient);

        // Act
        logger.info("Wywołanie metody update");
        Client result = clientController.update(clientId, updatedClientData);

        // Assert
        logger.info("Weryfikacja odpowiedzi");
        assertNotNull(result);
        assertEquals(clientId, result.getId());
        assertEquals("Jan", result.getFirstName());
        assertEquals("Kowalski-Nowak", result.getLastName());
        assertEquals("111222333", result.getPhone());

        verify(clientService, times(1)).update(idCaptor.capture(), clientCaptor.capture());
        assertEquals(clientId, idCaptor.getValue());
        Client capturedClient = clientCaptor.getValue();
        assertEquals("Kowalski-Nowak", capturedClient.getLastName());

        logger.info("Test zakończony pomyślnie");
    }

    @Test
    @DisplayName("Gdy wywołano update z różnymi ID, powinien użyć ID z ścieżki")
    void whenUpdateCalledWithDifferentIds_shouldUsePathId() {
        // Arrange
        logger.info("Przygotowanie danych testowych dla aktualizacji z różnymi ID");
        Long pathId = 5L;
        Client clientWithDifferentId = new Client();
        clientWithDifferentId.setId(99L); // Różne ID w obiekcie
        clientWithDifferentId.setFirstName("Test");

        Client updatedClient = new Client();
        updatedClient.setId(pathId);
        updatedClient.setFirstName("Test");

        when(clientService.update(eq(pathId), any(Client.class))).thenReturn(updatedClient);

        // Act
        logger.info("Wywołanie metody update z różnymi ID");
        Client result = clientController.update(pathId, clientWithDifferentId);

        // Assert
        logger.info("Weryfikacja odpowiedzi");
        assertNotNull(result);
        assertEquals(pathId, result.getId());

        verify(clientService, times(1)).update(eq(pathId), any(Client.class));

        logger.info("Test zakończony pomyślnie");
    }

    @Test
    @DisplayName("Gdy wywołano delete z poprawnym ID, powinien usunąć klienta")
    void whenDeleteCalledWithValidId_shouldDeleteClient() {
        // Arrange
        logger.info("Przygotowanie danych testowych dla usunięcia klienta");
        Long clientId = 1L;
        doNothing().when(clientService).delete(clientId);

        // Act
        logger.info("Wywołanie metody delete");
        clientController.delete(clientId);

        // Assert
        logger.info("Weryfikacja wywołania serwisu");
        verify(clientService, times(1)).delete(idCaptor.capture());
        assertEquals(clientId, idCaptor.getValue());

        logger.info("Test zakończony pomyślnie");
    }

    @Test
    @DisplayName("Gdy wywołano delete z ID równym zero, powinien przekazać do serwisu")
    void whenDeleteCalledWithZeroId_shouldPassToService() {
        // Arrange
        logger.info("Przygotowanie danych testowych dla usunięcia klienta z ID = 0");
        Long clientId = 0L;
        doNothing().when(clientService).delete(clientId);

        // Act
        logger.info("Wywołanie metody delete z ID = 0");
        clientController.delete(clientId);

        // Assert
        logger.info("Weryfikacja wywołania serwisu");
        verify(clientService, times(1)).delete(clientId);

        logger.info("Test zakończony pomyślnie");
    }

    @Test
    @DisplayName("Gdy wywołano delete z ujemnym ID, powinien przekazać do serwisu")
    void whenDeleteCalledWithNegativeId_shouldPassToService() {
        // Arrange
        logger.info("Przygotowanie danych testowych dla usunięcia klienta z ujemnym ID");
        Long clientId = -1L;
        doNothing().when(clientService).delete(clientId);

        // Act
        logger.info("Wywołanie metody delete z ujemnym ID");
        clientController.delete(clientId);

        // Assert
        logger.info("Weryfikacja wywołania serwisu");
        verify(clientService, times(1)).delete(clientId);

        logger.info("Test zakończony pomyślnie");
    }
}