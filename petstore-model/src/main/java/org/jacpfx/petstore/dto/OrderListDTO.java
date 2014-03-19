package org.jacpfx.petstore.dto;

import org.jacpfx.petstore.model.Order;

import java.io.Serializable;
import java.util.Set;

/**
 * Created by amo on 17.03.14.
 */
public class OrderListDTO implements Serializable {

    private final Set<Order> orders;
    private final State state;

    public OrderListDTO(final Set<Order> orders, final State state) {
        this.orders = orders;
        this.state = state;
    }

    public Set<Order> getOrders() {
        return orders;
    }

    public State getState() {
        return state;
    }

    public enum State {
        ALL, UPDATE

    }
}
