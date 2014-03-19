package org.jacpfx.petstore.model;

import java.io.Serializable;

/**
 * Created by amo on 16.01.14.
 */
public class Basket implements Serializable {

    private Long id;
    private String name;
    private String imageURL;
    private double price;

    public Basket() {

    }

    public Basket(final Long id, final String name, final String imageURL, final double price) {
        this.id = id;
        this.name = name;
        this.imageURL = imageURL;
        this.price = price;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
