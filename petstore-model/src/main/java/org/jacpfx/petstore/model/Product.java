package org.jacpfx.petstore.model;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by amo on 16.01.14.
 */
public class Product implements Serializable {

    private Long id;
    private String name;
    private String imageURL;
    private String description;
    private int amount;
    private double price;

    public Product() {

    }

    public Product(final Long id, final String name, final String imageURL, final double price, int amount, String description) {
        this.id = id;
        this.name = name;
        this.imageURL = imageURL;
        this.price = price;
        this.description = description;
        this.amount = amount;

    }

    public Product(final String name, final String imageURL, final double price, int amount, String description) {
        this.id = UUID.randomUUID().getMostSignificantBits();
        this.name = name;
        this.imageURL = imageURL;
        this.price = price;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;

        Product product = (Product) o;

        if (id != null ? !id.equals(product.id) : product.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", imageURL='" + imageURL + '\'' +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                ", price=" + price +
                '}';
    }
}
