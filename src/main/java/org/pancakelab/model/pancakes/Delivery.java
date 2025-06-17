package org.pancakelab.model.pancakes;

import org.pancakelab.model.Order;

import java.util.List;

public class Delivery {

    private Order order;
    private List<PancakeRecipe> pancakes;

    public Delivery(Order order, List<PancakeRecipe> pancakes) {
        this.order = order;
        this.pancakes = pancakes;
    }

    public Order getOrder() {
        return order;
    }

    public List<PancakeRecipe> getPancakes() {
        return pancakes;
    }
}
