package org.jacpfx.petstore.gui.backoffice.fragments;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
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

    public void init(Product p) {
       rootPane.setOnMouseClicked((event)->{
           System.out.println("Mouse Event: " + this);
       });
        amountLabel.setText("1");
        priceLabel.setText(Double.toString(p.getPrice()));
        nameLabel.setText(p.getName());
        if(p.getImageURL()!=null && p.getImageURL().length()>1)productImage.setImage(new Image("/images/products/"+p.getImageURL()));
    }
}
