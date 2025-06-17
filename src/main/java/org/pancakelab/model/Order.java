package org.pancakelab.model;

import org.pancakelab.utils.ValidationUtils;

import java.text.MessageFormat;
import java.util.Objects;
import java.util.UUID;

public class Order {

    private final UUID id;
    private final int building;
    private final int room;

    public Order(int building, int room) {
        ValidationUtils.validateBuilding(building);
        ValidationUtils.validateRoom(room);

        this.id = UUID.randomUUID();
        this.building = building;
        this.room = room;
    }

    public UUID getId() {
        return id;
    }

    public int getBuilding() {
        return building;
    }

    public int getRoom() {
        return room;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return MessageFormat.format("Order '{'id={0}, building={1}, room={2}'}'", id, building, room);
    }
}
