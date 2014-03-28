package org.jacpfx.petstore.gui.backoffice.components;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import org.jacpfx.api.annotations.Resource;
import org.jacpfx.api.annotations.component.DeclarativeView;
import org.jacpfx.api.annotations.lifecycle.PostConstruct;
import org.jacpfx.api.annotations.lifecycle.PreDestroy;
import org.jacpfx.api.message.Message;
import org.jacpfx.api.util.ToolbarPosition;
import org.jacpfx.petstore.commons.ProductUtil;
import org.jacpfx.petstore.gui.backoffice.configuration.BaseConfig;
import org.jacpfx.petstore.model.Product;
import org.jacpfx.rcp.component.FXComponent;
import org.jacpfx.rcp.componentLayout.FXComponentLayout;
import org.jacpfx.rcp.components.toolBar.JACPHoverMenu;
import org.jacpfx.rcp.components.toolBar.JACPOptionButton;
import org.jacpfx.rcp.components.toolBar.JACPToolBar;
import org.jacpfx.rcp.context.Context;
import org.jacpfx.rcp.util.CSSUtil;
import org.springframework.util.StringUtils;

import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * @author Patrick Symmangk (pete.jacp@gmail.com)
 */

@DeclarativeView(id = BaseConfig.PRODUCT_DETAIL_COMPONENT_ID,
        name = "SimpleView",
        active = true,
        resourceBundleLocation = "bundles.languageBundle",
        initialTargetLayoutId = BaseConfig.TARGET_CUSTOMER_COMPONENT_ID,
        viewLocation = "/fxml/ProductDetail.fxml")
public class ProductDetailComponent implements FXComponent {

    private static final Logger LOGGER = Logger.getLogger(ProductDetailComponent.class.getName());

    private static final String DEFAULT_PRODUCT = "default.png";

    @Resource
    private Context context;

    @FXML
    private TextField name;
    @FXML
    private TextField amount;
    @FXML
    private TextField price;
    @FXML
    private TextField description;
    @FXML
    private BorderPane imageViewPanel;
    @FXML
    private BorderPane buttonPanel;

    private Button save = new Button("save");
    private Button create = new Button("create");
    private ImageView imageView = new ImageView();

    private Product current;


    @Override
    /**
     * The handle method always runs outside the main application thread. You can create new nodes, execute long running tasks but you are not allowed to manipulate existing nodes here.
     */
    public Node handle(final Message<Event, Object> message) {
        // runs in worker thread
        return null;
    }

    @Override
    /**
     * The postHandle method runs always in the main application thread.
     */
    public Node postHandle(final Node arg0,
                           final Message<Event, Object> message) {
        // runs in FX application thread
        if (message.isMessageBodyTypeOf(Product.class)) {
            save.setDisable(true);
            current = message.getTypedMessageBody(Product.class);
            init(current);
        }
        return null;
    }

    private void init(Product product) {
        name.setText(product.getName());
        price.setText(Double.toString(product.getPrice()));
        amount.setText(Integer.toString(product.getAmount()));
        description.setText(product.getDescription());
        String imageUrl = !StringUtils.isEmpty(product.getImageURL()) ? product.getImageURL() : DEFAULT_PRODUCT;
        imageView.setImage(new Image(ProductUtil.getProductImageURL(imageUrl)));
    }

    @PostConstruct
    /**
     * The @OnStart annotation labels methods executed when the component switch from inactive to active state
     * @param arg0
     * @param resourceBundle
     */
    public void onStartComponent(final FXComponentLayout layout,
                                 final ResourceBundle resourceBundle) {
        JACPToolBar toolbar = layout.getRegisteredToolBar(ToolbarPosition.NORTH);
        save.setDisable(true);
        save.setOnMousePressed((event) -> {
            if (current != null) {
                final Product p = save(current);
                context.send(BaseConfig.WSPRODUCT_COMPONENT_ID, p);
                save.setDisable(true);
            }
        });

        create.setOnMousePressed((event) -> {
            current = new Product("", "", 0, 0, "");
            init(current);
            save.setDisable(true);
        });

        toolbar.addAllOnEnd("productDetail", save, create);

        name.setOnKeyReleased((listener) -> {
            if (current != null) {
                if (!name.getText().equals(current.getName())) {
                    save.setDisable(false);
                } else {
                    save.setDisable(true);
                }
            }
        });

        price.setOnKeyReleased((listener) -> {
            if (current != null) {
                if (!price.getText().equals(Double.toString(current.getPrice()))) {
                    save.setDisable(false);
                } else {
                    save.setDisable(true);
                }
            }
        });

        amount.setOnKeyReleased((listener) -> {
            if (current != null) {
                if (!amount.getText().equals(Integer.toString(current.getAmount()))) {
                    save.setDisable(false);
                } else {
                    save.setDisable(true);
                }
            }
        });

        description.setOnKeyReleased((listener) -> {
            if (current != null) {
                if (!description.getText().equals(current.getDescription())) {
                    save.setDisable(false);
                } else {
                    save.setDisable(true);
                }
            }
        });
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(100);
        imageView.setFitWidth(100);
        final Pane seperator = new Pane();
        VBox.setVgrow(seperator, Priority.ALWAYS);
        this.imageViewPanel.setCenter(imageView);
        this.buttonPanel.setCenter(createImageOptionButton(layout));
    }

    private ImageView createImageView(final Image image) {
        ImageView iv = new ImageView(image);
        CSSUtil.addCSSClass("product-picker", iv);
        iv.setFitHeight(100);
        iv.setFitWidth(100);
        return iv;
    }

    private JACPHoverMenu createImageOptionButton(final FXComponentLayout layout) {
        TilePane tile = new TilePane(4, 4);
        tile.setPadding(new Insets(10));
        JACPHoverMenu options = new JACPHoverMenu("Product Image", layout);

        String[] images = new String[]{"default", "camel", "cat", "cebra", "elephant", "giraffe", "hippo", "icebear", "lion", "monkey", "penguine", "snake", "tiger", "turtle", "whale"};

        for (String imageName : images) {

            String fileName = ProductUtil.createProductImage(imageName);
            String imageUrl = ProductUtil.getProductImageURL(fileName);
            Image img = new Image(imageUrl);

            Button imageButton = new Button("", this.createImageView(img));
            imageButton.setMaxSize(30, 30);
            imageButton.setOnMousePressed((event) -> {
                if (current != null) {
                    current.setImageURL(fileName);
                    imageView.setImage(img);
                    save.setDisable(false);
                }
                options.hide();
            });
            tile.getChildren().add(imageButton);
        }

        options.setAlignment(Pos.BOTTOM_LEFT);
        options.getContentPane().getChildren().add(tile);
        return options;
    }

    private Product save(Product p) {
        p.setName(name.getText());
        p.setAmount(Integer.valueOf(amount.getText()));
        p.setDescription(description.getText());
        p.setPrice(Double.valueOf(price.getText()));

        return p;
    }

    @PreDestroy
    /**
     * The @OnTearDown annotations labels methods executed when the component is set to inactive
     * @param arg0
     */
    public void onTearDownComponent(final FXComponentLayout arg0) {
        LOGGER.info("run on tear down of ProductDetailComponent ");

    }


}
