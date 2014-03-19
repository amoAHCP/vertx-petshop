package org.jacpfx.petstore.dto;

import org.jacpfx.petstore.model.Order;

import java.io.Serializable;

/**
 * Created by amo on 17.03.14.
 */
public class OrderProcessingDTO implements Serializable {

    private Order order;
    private String wsTextFrameID;

    public OrderProcessingDTO(final Order order, final String wsTextFrameID) {
        this.order = order;
        this.wsTextFrameID = wsTextFrameID;
    }

    public Order getOrder() {
        return order;
    }

    public String getWsTextFrameID() {
        return wsTextFrameID;
    }



}
