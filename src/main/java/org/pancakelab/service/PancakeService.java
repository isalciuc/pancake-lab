package org.pancakelab.service;

import org.pancakelab.exception.InvalidInputException;
import org.pancakelab.exception.OrderNotFoundException;
import org.pancakelab.model.Order;
import org.pancakelab.model.pancakes.Delivery;
import org.pancakelab.model.pancakes.Pancake;
import org.pancakelab.model.pancakes.PancakeBuilder;
import org.pancakelab.model.pancakes.PancakeRecipe;
import org.pancakelab.utils.ValidationUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class PancakeService {

    // Thread-safe collections
    private final List<Order> orders = new CopyOnWriteArrayList<>();
    private final Set<UUID> completedOrders = ConcurrentHashMap.newKeySet();
    private final Set<UUID> preparedOrders = ConcurrentHashMap.newKeySet();
    private final List<PancakeRecipe> pancakes = new CopyOnWriteArrayList<>();

    // Lock for complex operations that need atomicity across multiple collections
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * Creates a new order with validation
     *
     * @param building the building number
     * @param room     the room number
     * @return the created order
     * @throws InvalidInputException if input is invalid
     */
    public Order createOrder(int building, int room) {
        try {
            Order order = new Order(building, room);
            orders.add(order);
            return order;
        } catch (InvalidInputException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidInputException("Error creating order: " + e.getMessage(), e);
        }
    }

    /**
     * Adds a dark chocolate pancake to an order
     *
     * @param orderId the order ID
     * @param count   the number of pancakes to add
     * @throws InvalidInputException  if count is invalid
     * @throws OrderNotFoundException if order is not found
     */
    public void addDarkChocolatePancake(UUID orderId, int count) {
        ValidationUtils.validateOrderId(orderId);
        ValidationUtils.validateCount(count);

        Order order = findOrderById(orderId);

        for (int i = 0; i < count; ++i) {
            Pancake pancake = new PancakeBuilder()
                    .withDarkChocolate()
                    .forOrder(orderId)
                    .build();

            addPancake(pancake, order);
        }
    }

    /**
     * Adds a milk chocolate pancake to an order
     *
     * @param orderId the order ID
     * @param count   the number of pancakes to add
     * @throws InvalidInputException  if count is invalid
     * @throws OrderNotFoundException if order is not found
     */
    public void addMilkChocolatePancake(UUID orderId, int count) {
        ValidationUtils.validateOrderId(orderId);
        ValidationUtils.validateCount(count);

        Order order = findOrderById(orderId);

        for (int i = 0; i < count; ++i) {
            Pancake pancake = new PancakeBuilder()
                    .withMilkChocolate()
                    .forOrder(orderId)
                    .build();

            addPancake(pancake, order);
        }
    }

    /**
     * Adds a milk chocolate with hazelnuts pancake to an order
     *
     * @param orderId the order ID
     * @param count   the number of pancakes to add
     * @throws InvalidInputException  if count is invalid
     * @throws OrderNotFoundException if order is not found
     */
    public void addMilkChocolateHazelnutsPancake(UUID orderId, int count) {
        ValidationUtils.validateOrderId(orderId);
        ValidationUtils.validateCount(count);

        Order order = findOrderById(orderId);

        for (int i = 0; i < count; ++i) {
            Pancake pancake = new PancakeBuilder()
                    .withMilkChocolate()
                    .withHazelnuts()
                    .forOrder(orderId)
                    .build();

            addPancake(pancake, order);
        }
    }

    /**
     * Add a custom pancake using the builder
     *
     * @param orderId the order ID
     * @param builder the pancake builder with configured ingredients
     * @throws OrderNotFoundException if order is not found
     */
    public void addCustomPancake(UUID orderId, PancakeBuilder builder) {
        ValidationUtils.validateOrderId(orderId);
        ValidationUtils.validateNotNull(builder, "Pancake builder");

        Order order = findOrderById(orderId);
        Pancake pancake = builder.forOrder(orderId).build();
        addPancake(pancake, order);
    }

    public List<String> viewOrder(UUID orderId) {
        return pancakes.stream()
                .filter(pancake -> pancake.getOrderId().equals(orderId))
                .map(PancakeRecipe::description)
                .toList();
    }

    public List<PancakeRecipe> getOrderPancakes(UUID orderId) {
        return pancakes.stream()
                .filter(pancake -> pancake.getOrderId().equals(orderId))
                .toList();
    }

    /**
     * Adds a pancake to an order
     *
     * @param pancake the pancake to add
     * @param order   the order to add the pancake to
     */
    private void addPancake(PancakeRecipe pancake, Order order) {
        ValidationUtils.validateNotNull(pancake, "Pancake");
        ValidationUtils.validateNotNull(order, "Order");

        pancake.setOrderId(order.getId());
        pancakes.add(pancake);

        OrderLog.logAddPancake(order, pancake.description(), pancakes);
    }

    /**
     * Removes pancakes from an order
     *
     * @param description the pancake description
     * @param orderId     the order ID
     * @param count       the number of pancakes to remove
     * @throws InvalidInputException  if count is invalid
     * @throws OrderNotFoundException if order is not found
     */
    public void removePancakes(String description, UUID orderId, int count) {
        ValidationUtils.validateNotEmpty(description, "Description");
        ValidationUtils.validateOrderId(orderId);
        ValidationUtils.validateCount(count);

        Order order = findOrderById(orderId);

        final AtomicInteger removedCount = new AtomicInteger(0);
        pancakes.removeIf(pancake -> {
            return pancake.getOrderId().equals(orderId) &&
                    pancake.description().equals(description) &&
                    removedCount.getAndIncrement() < count;
        });

        OrderLog.logRemovePancakes(order, description, removedCount.get(), pancakes);
    }

    /**
     * Cancels an order
     *
     * @param orderId the order ID
     * @throws OrderNotFoundException if order is not found
     */
    public void cancelOrder(UUID orderId) {
        ValidationUtils.validateOrderId(orderId);

        Order order = findOrderById(orderId);

        // Use lock to ensure atomicity of the entire operation
        lock.writeLock().lock();
        try {
            OrderLog.logCancelOrder(order, this.pancakes);

            pancakes.removeIf(pancake -> pancake.getOrderId().equals(orderId));
            orders.removeIf(o -> o.getId().equals(orderId));
            completedOrders.removeIf(u -> u.equals(orderId));
            preparedOrders.removeIf(u -> u.equals(orderId));

            OrderLog.logCancelOrder(order, pancakes);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Completes an order
     *
     * @param orderId the order ID
     * @throws OrderNotFoundException if order is not found
     */
    public void completeOrder(UUID orderId) {
        ValidationUtils.validateOrderId(orderId);

        // Check if order exists
        findOrderById(orderId);
        completedOrders.add(orderId);
    }

    /**
     * Lists all completed orders
     *
     * @return set of completed order IDs
     */
    public Set<UUID> listCompletedOrders() {
        return new HashSet<>(completedOrders);
    }

    /**
     * Prepares an order
     *
     * @param orderId the order ID
     * @throws OrderNotFoundException if order is not found
     */
    public void prepareOrder(UUID orderId) {
        ValidationUtils.validateOrderId(orderId);

        // Check if order exists
        findOrderById(orderId);

        // Use lock to ensure atomicity
        lock.writeLock().lock();
        try {
            preparedOrders.add(orderId);
            completedOrders.removeIf(u -> u.equals(orderId));
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Lists all prepared orders
     *
     * @return set of prepared order IDs
     */
    public Set<UUID> listPreparedOrders() {
        return new HashSet<>(preparedOrders);
    }

    /**
     * Delivers an order
     *
     * @param orderId the order ID
     * @return array containing the order and list of pancakes
     * @throws OrderNotFoundException if order is not found or not prepared
     */
    public Delivery deliverOrder(UUID orderId) {
        ValidationUtils.validateOrderId(orderId);

        if (!preparedOrders.contains(orderId)) {
            throw new OrderNotFoundException("Order not found or not prepared: " + orderId);
        }

        // Use lock to ensure atomicity
        lock.writeLock().lock();
        try {
            Order order = findOrderById(orderId);
            List<PancakeRecipe> pancakesToDeliver = getOrderPancakes(orderId);
            OrderLog.logDeliverOrder(order, this.pancakes);

            pancakes.removeIf(pancake -> pancake.getOrderId().equals(orderId));
            orders.removeIf(o -> o.getId().equals(orderId));
            preparedOrders.removeIf(u -> u.equals(orderId));

            return new Delivery(order, pancakesToDeliver);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Finds an order by ID
     *
     * @param orderId the order ID
     * @return the order
     * @throws OrderNotFoundException if order is not found
     */
    private Order findOrderById(UUID orderId) {
        return orders.stream()
                .filter(o -> o.getId().equals(orderId))
                .findFirst()
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }
}
