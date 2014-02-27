package org.jacpfx.petstore.dto;

import org.jacpfx.petstore.model.Product;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Andy Moncsek on 27.02.14.
 */
public class ProductListDTO implements Serializable {

    private final List<Product> products;

    public ProductListDTO(final List<Product> products) {
        this.products = products;
    }

    public List<Product> getProducts() {
        return products;
    }
}
