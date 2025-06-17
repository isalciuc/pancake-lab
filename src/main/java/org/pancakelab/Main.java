package org.pancakelab;

import org.pancakelab.model.Order;
import org.pancakelab.model.pancakes.Delivery;
import org.pancakelab.model.pancakes.PancakeBuilder;
import org.pancakelab.service.PancakeService;

import java.text.MessageFormat;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        System.out.println("Welcome to Pancake Lab!");
        PancakeService service = new PancakeService();

        // Demo: Create an order
        Order order = service.createOrder(5, 101);
        System.out.println(MessageFormat.format("Created order: {0} for Building {1}, Room {2}", order.getId(), order.getBuilding(), order.getRoom()));

        // Add some standard pancakes
        service.addDarkChocolatePancake(order.getId(), 2);
        service.addMilkChocolateHazelnutsPancake(order.getId(), 1);

        // Demo using the builder pattern for custom pancakes
        PancakeBuilder customBuilder = new PancakeBuilder()
            .withMilkChocolate()
            .withWhippedCream()
            .withIngredient("strawberries");

        service.addCustomPancake(order.getId(), customBuilder);

        // View the order
        List<String> orderContents = service.viewOrder(order.getId());
        System.out.println("\nOrder contents:");
        orderContents.forEach(pancake -> System.out.println("- " + pancake));

        // Complete the order
        service.completeOrder(order.getId());
        System.out.println("\nOrder completed and ready for preparation.");
        // Chef prepares the order
        service.prepareOrder(order.getId());
        System.out.println("Order prepared and ready for delivery.");
        // Deliver the order
        Delivery delivered = service.deliverOrder(order.getId());
        Order deliveredOrder = delivered.getOrder();
        System.out.printf("Order delivered to Building %s, Room %s\n", deliveredOrder.getBuilding(), deliveredOrder.getRoom());

        System.out.println("\nThank you for using Pancake Lab!");
    }
}