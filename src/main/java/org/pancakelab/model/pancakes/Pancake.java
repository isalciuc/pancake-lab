package org.pancakelab.model.pancakes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Base Pancake class that implements PancakeRecipe interface.
 */
public class Pancake implements PancakeRecipe {

    private UUID orderId;
    private final List<String> ingredients;

    protected Pancake() {
        this.ingredients = new ArrayList<>();
    }

    @Override
    public UUID getOrderId() {
        return orderId;
    }

    @Override
    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    @Override
    public List<String> ingredients() {
        return new ArrayList<>(ingredients);
    }

    /**
     * Adds an ingredient to the pancake
     * @param ingredient ingredient to add
     */
    public void addIngredient(String ingredient) {
        if (ingredient != null && !ingredient.trim().isEmpty()) {
            this.ingredients.add(ingredient);
        }
    }

    @Override
    public String description() {
        return "Delicious pancake with %s!".formatted(String.join(", ", ingredients()));
    }
}