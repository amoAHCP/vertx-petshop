package org.jacpfx.petstore.dto;

import org.jacpfx.petstore.model.Product;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * Created by Andy Moncsek on 27.02.14.
 */
public class ProductListDTO implements Serializable {

    private final Set<Product> products;
    private final State state;

    public ProductListDTO(final State state,final Set<Product> products) {
        this.state = state;
        this.products = products;
    }

    public Set<Product> getProducts() {
        return products;
    }

    public State getState() {
        return state;
    }

    public enum State {
        ALL, UPDATE

    }
}
