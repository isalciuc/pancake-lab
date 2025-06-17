package org.pancakelab.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.pancakelab.model.Order;
import org.pancakelab.model.pancakes.Delivery;
import org.pancakelab.model.pancakes.PancakeRecipe;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PancakeServiceTest {

    private final static String DARK_CHOCOLATE_PANCAKE_DESCRIPTION           = "Delicious pancake with dark chocolate!";
    private final static String MILK_CHOCOLATE_PANCAKE_DESCRIPTION           = "Delicious pancake with milk chocolate!";
    private final static String MILK_CHOCOLATE_HAZELNUTS_PANCAKE_DESCRIPTION = "Delicious pancake with milk chocolate, hazelnuts!";

    private PancakeService pancakeService;

    @BeforeEach
    void setup() {
        pancakeService = new PancakeService();
    }

    @Test
    public void GivenOrderDoesNotExist_WhenCreatingOrder_ThenOrderCreatedWithCorrectData_Test() {

        // setup

        // exercise
        Order order = pancakeService.createOrder(10, 20);

        assertEquals(10, order.getBuilding());
        assertEquals(20, order.getRoom());

        // verify

        // tear down
    }

    @Test
    public void GivenOrderExists_WhenAddingPancakes_ThenCorrectNumberOfPancakesAdded_Test() {

        // setup
        Order order = pancakeService.createOrder(10, 20);

        // exercise
        addPancakes(order);

        // verify
        List<String> ordersPancakes = pancakeService.viewOrder(order.getId());

        assertEquals(List.of(DARK_CHOCOLATE_PANCAKE_DESCRIPTION,
                             DARK_CHOCOLATE_PANCAKE_DESCRIPTION,
                             DARK_CHOCOLATE_PANCAKE_DESCRIPTION,
                             MILK_CHOCOLATE_PANCAKE_DESCRIPTION,
                             MILK_CHOCOLATE_PANCAKE_DESCRIPTION,
                             MILK_CHOCOLATE_PANCAKE_DESCRIPTION,
                             MILK_CHOCOLATE_HAZELNUTS_PANCAKE_DESCRIPTION,
                             MILK_CHOCOLATE_HAZELNUTS_PANCAKE_DESCRIPTION,
                             MILK_CHOCOLATE_HAZELNUTS_PANCAKE_DESCRIPTION), ordersPancakes);

        // tear down
    }

    @Test
    public void GivenPancakesExists_WhenRemovingPancakes_ThenCorrectNumberOfPancakesRemoved_Test() {

        // setup
        Order order = pancakeService.createOrder(10, 20);
        addPancakes(order);

        // exercise
        pancakeService.removePancakes(DARK_CHOCOLATE_PANCAKE_DESCRIPTION, order.getId(), 2);
        pancakeService.removePancakes(MILK_CHOCOLATE_PANCAKE_DESCRIPTION, order.getId(), 3);
        pancakeService.removePancakes(MILK_CHOCOLATE_HAZELNUTS_PANCAKE_DESCRIPTION, order.getId(), 1);

        // verify
        List<String> ordersPancakes = pancakeService.viewOrder(order.getId());

        assertEquals(List.of(DARK_CHOCOLATE_PANCAKE_DESCRIPTION,
                             MILK_CHOCOLATE_HAZELNUTS_PANCAKE_DESCRIPTION,
                             MILK_CHOCOLATE_HAZELNUTS_PANCAKE_DESCRIPTION), ordersPancakes);

        // tear down
    }

    @Test
    public void GivenOrderExists_WhenCompletingOrder_ThenOrderCompleted_Test() {

        // setup
        Order order = pancakeService.createOrder(10, 20);

        // exercise
        pancakeService.completeOrder(order.getId());

        // verify
        Set<UUID> completedOrdersOrders = pancakeService.listCompletedOrders();
        assertTrue(completedOrdersOrders.contains(order.getId()));

        // tear down
    }

    @Test
    public void GivenOrderExists_WhenPreparingOrder_ThenOrderPrepared_Test() {

        // setup
        Order order = pancakeService.createOrder(10, 20);

        // exercise
        pancakeService.prepareOrder(order.getId());

        // verify
        Set<UUID> completedOrders = pancakeService.listCompletedOrders();
        assertFalse(completedOrders.contains(order.getId()));

        Set<UUID> preparedOrders = pancakeService.listPreparedOrders();
        assertTrue(preparedOrders.contains(order.getId()));

        // tear down
    }

    @Test
    public void GivenOrderExists_WhenDeliveringOrder_ThenCorrectOrderReturnedAndOrderRemovedFromTheDatabase_Test() {

        // setup
        Order order = pancakeService.createOrder(10, 20);
        addPancakes(order);
        pancakeService.prepareOrder(order.getId());
        List<String> pancakesToDeliver = pancakeService.viewOrder(order.getId());

        // exercise
        Delivery delivery = pancakeService.deliverOrder(order.getId());

        // verify
        Set<UUID> completedOrders = pancakeService.listCompletedOrders();
        assertFalse(completedOrders.contains(order.getId()));

        Set<UUID> preparedOrders = pancakeService.listPreparedOrders();
        assertFalse(preparedOrders.contains(order.getId()));

        List<String> ordersPancakes = pancakeService.viewOrder(order.getId());

        Order deliveredOrder = delivery.getOrder();
        List<PancakeRecipe> deliveredPancakes = delivery.getPancakes();

        assertNotNull(deliveredOrder);
        assertEquals(List.of(), ordersPancakes);
        assertEquals(order.getId(), deliveredOrder.getId());
        assertEquals(pancakesToDeliver, deliveredPancakes.stream().map(PancakeRecipe::description).toList());

    }

    @Test
    public void GivenOrderExists_WhenCancellingOrder_ThenOrderAndPancakesRemoved_Test() {

        // setup
        Order order = pancakeService.createOrder(10, 20);
        addPancakes(order);

        // exercise
        pancakeService.cancelOrder(order.getId());

        // verify
        Set<UUID> completedOrders = pancakeService.listCompletedOrders();
        assertFalse(completedOrders.contains(order.getId()));

        Set<UUID> preparedOrders = pancakeService.listPreparedOrders();
        assertFalse(preparedOrders.contains(order.getId()));

        List<String> ordersPancakes = pancakeService.viewOrder(order.getId());

        assertEquals(List.of(), ordersPancakes);

        // tear down
    }

    private void addPancakes(Order order) {
        pancakeService.addDarkChocolatePancake(order.getId(), 3);
        pancakeService.addMilkChocolatePancake(order.getId(), 3);
        pancakeService.addMilkChocolateHazelnutsPancake(order.getId(), 3);
    }
}
