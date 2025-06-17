package org.pancakelab.model.pancakes;

import java.util.UUID;

/**
 * Builder for creating customized pancakes.
 */
public class PancakeBuilder {

    private final Pancake pancake;

    public PancakeBuilder() {
        this.pancake = new Pancake();
    }

    /**
     * Adds dark chocolate to the pancake
     * @return the builder
     */
    public PancakeBuilder withDarkChocolate() {
        pancake.addIngredient("dark chocolate");
        return this;
    }

    /**
     * Adds milk chocolate to the pancake
     * @return the builder
     */
    public PancakeBuilder withMilkChocolate() {
        pancake.addIngredient("milk chocolate");
        return this;
    }

    /**
     * Adds whipped cream to the pancake
     * @return the builder
     */
    public PancakeBuilder withWhippedCream() {
        pancake.addIngredient("whipped cream");
        return this;
    }

    /**
     * Adds hazelnuts to the pancake
     * @return the builder
     */
    public PancakeBuilder withHazelnuts() {
        pancake.addIngredient("hazelnuts");
        return this;
    }

    /**
     * Adds a custom ingredient to the pancake
     * @param ingredient the ingredient to add
     * @return the builder
     * @throws IllegalArgumentException if ingredient is null or empty
     */
    public PancakeBuilder withIngredient(String ingredient) {
        if (ingredient == null || ingredient.trim().isEmpty()) {
            throw new IllegalArgumentException("Ingredient cannot be null or empty");
        }
        pancake.addIngredient(ingredient);
        return this;
    }

    /**
     * Sets the order ID for this pancake
     * @param orderId the UUID of the order
     * @return the builder
     */
    public PancakeBuilder forOrder(UUID orderId) {
        pancake.setOrderId(orderId);
        return this;
    }

    /**
     * Builds the pancake with the configured ingredients
     * @return the built Pancake instance
     */
    public Pancake build() {
        return pancake;
    }
}