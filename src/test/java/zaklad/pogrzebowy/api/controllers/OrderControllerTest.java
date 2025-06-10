package zaklad.pogrzebowy.api.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zaklad.pogrzebowy.api.models.Order;
import zaklad.pogrzebowy.api.services.OrderService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class OrderControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(OrderControllerTest.class);

    @InjectMocks
    private OrderController orderController;

    @Mock
    private OrderService orderService;

    @Captor
    private ArgumentCaptor<Order> orderCaptor;

    @Captor
    private ArgumentCaptor<Long> idCaptor;

    private Order order1;
    private Order order2;

    @BeforeEach
    void setUp() {
        logger.info("Inicjalizacja mocków dla testów OrderController");
        MockitoAnnotations.openMocks(this);

        order1 = new Order();
        order1.setId(1L);
        order1.setCadaverFirstName("Jan");
        order1.setCadaverLastName("Kowalski");
        order1.setOrderDate(LocalDateTime.now().minusDays(5));
        order1.setStatus(Order.Status.pending);

        order2 = new Order();
        order2.setId(2L);
        order2.setCadaverFirstName("Anna");
        order2.setCadaverLastName("Nowak");
        order2.setOrderDate(LocalDateTime.now().minusDays(2));
        order2.setStatus(Order.Status.completed);
    }

    @Test
    @DisplayName("Gdy wywołano getAllOrders, powinien zwrócić listę wszystkich zleceń")
    void whenGetAllOrdersCalled_shouldReturnAllOrders() {
        logger.info("Przygotowanie danych testowych dla pobrania wszystkich zleceń");
        List<Order> expectedOrders = Arrays.asList(order1, order2);
        when(orderService.findAll()).thenReturn(expectedOrders);

        logger.info("Wywołanie metody getAllOrders");
        List<Order> actualOrders = orderController.getAllOrders();

        logger.info("Weryfikacja odpowiedzi");
        assertNotNull(actualOrders);
        assertEquals(2, actualOrders.size());
        assertEquals(expectedOrders, actualOrders);
        assertEquals("Jan", actualOrders.get(0).getCadaverFirstName());
        assertEquals("Anna", actualOrders.get(1).getCadaverFirstName());

        verify(orderService, times(1)).findAll();
        logger.info("Test zakończony pomyślnie");
    }

    @Test
    @DisplayName("Gdy wywołano getOrderById z poprawnym ID, powinien zwrócić zlecenie")
    void whenGetOrderByIdCalled_shouldReturnOrder() {
        Long id = 1L;
        when(orderService.findById(id)).thenReturn(Optional.of(order1));

        Optional<Order> result = orderController.getOrderById(id);

        assertTrue(result.isPresent());
        assertEquals(order1, result.get());

        verify(orderService, times(1)).findById(id);
    }

    @Test
    @DisplayName("Gdy wywołano getOrderById z nieistniejącym ID, powinien zwrócić pusty Optional")
    void whenGetOrderByIdCalledWithNonExistingId_shouldReturnEmpty() {
        Long id = 99L;
        when(orderService.findById(id)).thenReturn(Optional.empty());

        Optional<Order> result = orderController.getOrderById(id);

        assertFalse(result.isPresent());

        verify(orderService, times(1)).findById(id);
    }

    @Test
    @DisplayName("Gdy wywołano getOrdersByStatus, powinien zwrócić listę zleceń o podanym statusie")
    void whenGetOrdersByStatusCalled_shouldReturnOrdersByStatus() {
        List<Order> pendingOrders = Arrays.asList(order1);
        when(orderService.findByStatus(Order.Status.pending)).thenReturn(pendingOrders);

        List<Order> result = orderController.getOrdersByStatus(Order.Status.pending);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(Order.Status.pending, result.get(0).getStatus());

        verify(orderService, times(1)).findByStatus(Order.Status.pending);
    }

    @Test
    @DisplayName("Gdy wywołano createOrder z poprawnymi danymi, powinien utworzyć nowe zlecenie")
    void whenCreateOrderCalledWithValidData_shouldCreateNewOrder() {
        Order newOrder = new Order();
        newOrder.setCadaverFirstName("Piotr");
        newOrder.setCadaverLastName("Nowak");
        newOrder.setStatus(Order.Status.pending);

        Order createdOrder = new Order();
        createdOrder.setId(3L);
        createdOrder.setCadaverFirstName("Piotr");
        createdOrder.setCadaverLastName("Nowak");
        createdOrder.setStatus(Order.Status.pending);

        when(orderService.create(any(Order.class))).thenReturn(createdOrder);

        Order result = orderController.createOrder(newOrder);

        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals("Piotr", result.getCadaverFirstName());
        assertEquals(Order.Status.pending, result.getStatus());

        verify(orderService, times(1)).create(orderCaptor.capture());
        Order capturedOrder = orderCaptor.getValue();
        assertEquals("Piotr", capturedOrder.getCadaverFirstName());
    }

    @Test
    @DisplayName("Gdy wywołano updateOrder z poprawnymi danymi, powinien zaktualizować zlecenie")
    void whenUpdateOrderCalledWithValidData_shouldUpdateOrder() {
        Long orderId = 1L;

        Order updatedOrderData = new Order();
        updatedOrderData.setCadaverFirstName("Jan");
        updatedOrderData.setCadaverLastName("Kowalski-Nowak");
        updatedOrderData.setStatus(Order.Status.completed);

        Order updatedOrder = new Order();
        updatedOrder.setId(orderId);
        updatedOrder.setCadaverFirstName("Jan");
        updatedOrder.setCadaverLastName("Kowalski-Nowak");
        updatedOrder.setStatus(Order.Status.completed);

        when(orderService.update(eq(orderId), any(Order.class))).thenReturn(updatedOrder);

        Order result = orderController.updateOrder(orderId, updatedOrderData);

        assertNotNull(result);
        assertEquals(orderId, result.getId());
        assertEquals("Kowalski-Nowak", result.getCadaverLastName());
        assertEquals(Order.Status.completed, result.getStatus());

        verify(orderService, times(1)).update(idCaptor.capture(), orderCaptor.capture());
        assertEquals(orderId, idCaptor.getValue());
        assertEquals("Kowalski-Nowak", orderCaptor.getValue().getCadaverLastName());
    }

    @Test
    @DisplayName("Gdy wywołano deleteOrder z poprawnym ID, powinien usunąć zlecenie")
    void whenDeleteOrderCalledWithValidId_shouldDeleteOrder() {
        Long orderId = 1L;
        doNothing().when(orderService).delete(orderId);

        orderController.deleteOrder(orderId);

        verify(orderService, times(1)).delete(idCaptor.capture());
        assertEquals(orderId, idCaptor.getValue());
    }
}
