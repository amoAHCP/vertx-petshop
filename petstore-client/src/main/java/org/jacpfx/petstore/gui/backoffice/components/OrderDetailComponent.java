package org.jacpfx.petstore.gui.backoffice.components;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.jacpfx.api.annotations.Resource;
import org.jacpfx.api.annotations.component.DeclarativeView;
import org.jacpfx.api.annotations.lifecycle.PostConstruct;
import org.jacpfx.api.annotations.lifecycle.PreDestroy;
import org.jacpfx.api.message.Message;
import org.jacpfx.api.util.ToolbarPosition;
import org.jacpfx.petstore.gui.backoffice.configuration.BaseConfig;
import org.jacpfx.petstore.model.BasketItem;
import org.jacpfx.petstore.model.Order;
import org.jacpfx.petstore.model.Product;
import org.jacpfx.rcp.component.FXComponent;
import org.jacpfx.rcp.componentLayout.FXComponentLayout;
import org.jacpfx.rcp.components.toolBar.JACPOptionButton;
import org.jacpfx.rcp.components.toolBar.JACPToolBar;
import org.jacpfx.rcp.context.Context;

import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * @author Patrick Symmangk (pete.jacp@gmail.com)
 */

@DeclarativeView(id = BaseConfig.ORDER_DETAIL_COMPONENT_ID,
        name = "SimpleView",
        active = true,
        resourceBundleLocation = "bundles.languageBundle",
        initialTargetLayoutId = BaseConfig.TARGET_ORDER_DETAIL_COMPONENT_ID,
        viewLocation = "/fxml/OrderDetail.fxml")
public class OrderDetailComponent implements FXComponent {

    private static final Logger LOGGER = Logger.getLogger(OrderDetailComponent.class.getName());

    @Resource
    private Context context;

    @FXML
    private Label firstName;
    @FXML
    private Label lastName;
    @FXML
    private Label street;
    @FXML
    private Label zip;
    @FXML
    private Label city;
    @FXML
    private Label country;
    @FXML
    private TitledPane productPane;

    private ListView<BasketItem> productList;





    private ImageView imageView = new ImageView();

    private Order current;


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
        if (message.isMessageBodyTypeOf(Order.class)) {
            current = message.getTypedMessageBody(Order.class);
            init(current);
        }
        return null;
    }

    private void init(Order order) {
        firstName.setText(order.getCustomer().getFirstname());
        lastName.setText(order.getCustomer().getLastname());
        street.setText(order.getCustomer().getHomeAddress().getStreet1());
        zip.setText(order.getCustomer().getHomeAddress().getZipcode());
        city.setText(order.getCustomer().getHomeAddress().getCity());
        country.setText(order.getCustomer().getHomeAddress().getCountry());
        createListView();
        productList.getItems().addAll(FXCollections.observableList(order.getBasket().getBasketItems()));
    }

    @PostConstruct
    /**
     * The @OnStart annotation labels methods executed when the component switch from inactive to active state
     * @param arg0
     * @param resourceBundle
     */
    public void onStartComponent(final FXComponentLayout layout,
                                 final ResourceBundle resourceBundle) {
        createListView();
    }

    private void createListView() {
        productList = new ListView<>();
        VBox.setVgrow(productList,Priority.ALWAYS);
        productList.setCellFactory(list -> new BasketCell()
        );
        productPane.setContent(productList);
    }

    static class BasketCell extends ListCell<BasketItem> {
        @Override
        public void updateItem(BasketItem item, boolean empty) {
            super.updateItem(item, empty);
           if (item != null) {
               HBox box = new HBox();
               ImageView image = new ImageView(new Image("/images/products/" + item.getProduct().getImageURL()));
               image.setFitWidth(20);
               image.setFitHeight(20);
               Label label = new Label(" "+item.getProduct().getName()+", "+item.getAmount()+", total: "+item.getAmount()*item.getProduct().getPrice());
               box.getChildren().addAll(image,label);
               setGraphic(box);
            }
        }
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
