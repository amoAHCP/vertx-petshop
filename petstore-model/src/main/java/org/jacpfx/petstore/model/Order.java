package org.jacpfx.petstore.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by amo on 12.03.14.
 */
public class Order implements Serializable {
    private List<Product> products;
    private Customer customer;
    private double amount;

    public Order() {

    }

    public Order(final double amount, final Customer customer, final List<Product> products) {
        this.amount = amount;
        this.customer = customer;
        this.products = products;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
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
        if (!products.equals(order.products)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = products.hashCode();
        result = 31 * result + customer.hashCode();
        temp = Double.doubleToLongBits(amount);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Order{" +
                "products=" + products +
                ", customer=" + customer +
                ", amount=" + amount +
                '}';
    }
}
