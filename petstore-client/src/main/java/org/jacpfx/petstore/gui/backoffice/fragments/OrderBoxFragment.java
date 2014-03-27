package org.jacpfx.petstore.gui.backoffice.fragments;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.jacpfx.api.annotations.Resource;
import org.jacpfx.api.annotations.fragment.Fragment;
import org.jacpfx.api.fragment.Scope;
import org.jacpfx.petstore.gui.backoffice.configuration.BaseConfig;
import org.jacpfx.petstore.model.Order;
import org.jacpfx.petstore.model.Product;
import org.jacpfx.rcp.context.Context;

/**
 * Created by amo on 18.03.14.
 */
@Fragment(id = BaseConfig.ORDER_BOX_FRAGMENT,
        viewLocation = "/fxml/Order.fxml",
        resourceBundleLocation = "bundles.languageBundle",
        localeID = "en_US",
        scope = Scope.PROTOTYPE)
public class OrderBoxFragment {
    @Resource
    private Context context;

    @FXML
    private VBox rootPane;
    @FXML
    private Label amountLabel;
    @FXML
    private Label priceLabel;
    @FXML
    private Label customerLabel;
    @FXML
    private Label itemLabel;
    @FXML
    private Label orderId;

    private Order order;

    public void init(Order order) {
        this.order = order;
        rootPane.setOnMousePressed((event) -> {
            rootPane.setEffect(new GaussianBlur());
            context.send(BaseConfig.ORDER_DETAIL_COMPONENT_ID, order);
        });

        rootPane.setOnMouseEntered((event) -> {
            rootPane.setEffect(new DropShadow());
        });

        rootPane.setOnMouseExited((event) -> {
            rootPane.setEffect(null);
        });
        rootPane.setOnMouseReleased((event) -> {
            rootPane.setEffect(null);
        });
        final int sum =order.getBasket().getBasketItems().parallelStream().mapToInt(item->item.getAmount()).sum();
        this.amountLabel.setText(Integer.toString(sum));
        final double price = order.getBasket().getBasketItems().parallelStream().mapToDouble(item->item.getAmount()*item.getProduct().getPrice()).sum();
        this.priceLabel.setText(Double.toString(price));
        this.customerLabel.setText(order.getCustomer().getFirstname()+", "+order.getCustomer().getLastname());
        this.itemLabel.setText(Integer.toString(order.getBasket().getBasketItems().size()));
        this.orderId.setText(order.getOrderId());


    }

    public Order getOrder() {
        return order;
    }

    @Override
    public boolean equals(Object o) {
        if (order == o) return true;
        if (!(o instanceof Product)) return false;

        Product that = (Product) o;

        if (order != null ? !order.equals(that) : that != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return order != null ? order.hashCode() : 0;
    }
}
