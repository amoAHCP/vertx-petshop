package org.jacpfx.petstore.model;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/**
 * Created by amo on 12.03.14.
 */
// TODO add id and update hashCode
public class Order implements Serializable {
    private Basket basket;
    private Long id;
    private Customer customer;
    private double amount;

    public Order() {

    }

    public Order(final double amount, final Customer customer, final Basket basket) {
        this.id = UUID.randomUUID().getMostSignificantBits();
        this.amount = amount;
        this.customer = customer;
        this.basket = basket;
    }


    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Basket getBasket() {
        return basket;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order)) return false;

        Order order = (Order) o;

        if (Double.compare(order.amount, amount) != 0) return false;
        if (customer != null ? !customer.equals(order.customer) : order.customer != null) return false;
        if (!id.equals(order.id)) return false;
        if (basket != null ? !basket.equals(order.basket) : order.basket != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", basket=" + basket +
                ", customer=" + customer +
                ", amount=" + amount +
                '}';
    }
}
