package org.jacpfx.petstore.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by amo on 16.01.14.
 */
public class Basket implements Serializable {

    private Long id;

    private List<BasketItem> basketItems;


    public Basket() {

    }

    public Basket(List<BasketItem> basketItems) {
        this.basketItems = basketItems;
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

    public List<BasketItem> getBasketItems() {
        return basketItems;
    }

    public void setBasketItems(List<BasketItem> basketItems) {
        this.basketItems = basketItems;
    }
}
