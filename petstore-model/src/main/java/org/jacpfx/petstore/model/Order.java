package org.jacpfx.petstore.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by amo on 12.03.14.
 */
// TODO add id and update hashCode
public class Order implements Serializable {
    private Basket basket;
    private Customer customer;
    private double amount;

    public Order() {

    }

    public Order(final double amount, final Customer customer, final List<Product> products) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order)) return false;

        Order order = (Order) o;

        if (Double.compare(order.amount, amount) != 0) return false;
        if (!customer.equals(order.customer)) return false;
        if (!basket.equals(order.basket)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = basket.hashCode();
        result = 31 * result + customer.hashCode();
        temp = Double.doubleToLongBits(amount);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Order{" +
                "basket=" + basket +
                ", customer=" + customer +
                ", amount=" + amount +
                '}';
    }
}
