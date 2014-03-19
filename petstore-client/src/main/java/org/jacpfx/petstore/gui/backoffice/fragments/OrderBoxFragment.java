package org.jacpfx.petstore.gui.backoffice.fragments;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import org.jacpfx.api.annotations.Resource;
import org.jacpfx.api.annotations.fragment.Fragment;
import org.jacpfx.api.fragment.Scope;
import org.jacpfx.petstore.gui.backoffice.configuration.BaseConfig;
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
    private GridPane rootPane;
    @FXML
    private Label amountLabel;
    @FXML
    private Label priceLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private ImageView productImage;
    private Product p;

    public void init(Product p) {
        this.p = p;
        rootPane.setOnMousePressed((event) -> {
            rootPane.setEffect(new GaussianBlur());
            context.send(BaseConfig.PRODUCT_DETAIL_COMPONENT_ID, p);
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
        amountLabel.setText("1");
        priceLabel.setText(Double.toString(p.getPrice()));
        nameLabel.setText(p.getName());
        descriptionLabel.setText(p.getDescription());
        amountLabel.setText(Integer.toString(p.getAmount()));

        if (p.getImageURL() != null && p.getImageURL().length() > 1)
            productImage.setImage(new Image("/images/products/" + p.getImageURL()));
    }
}
