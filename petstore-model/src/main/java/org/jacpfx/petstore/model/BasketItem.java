package org.jacpfx.petstore.model;

import java.io.Serializable;

/**
 * Created by amo on 16.01.14.
 */
public class BasketItem implements Serializable {

    private Long id;

    private Product product;


    private int amount;


    public BasketItem() {

    }

    public BasketItem(Product product, int amount) {
        this.product = product;
        this.amount = amount;
    }

    /**
     * The Product Id
     *
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the product Id
     *
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {

        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
