package org.jacpfx.petstore.gui.backoffice.components;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import org.jacpfx.api.annotations.Resource;
import org.jacpfx.api.annotations.component.DeclarativeView;
import org.jacpfx.api.annotations.lifecycle.PostConstruct;
import org.jacpfx.api.annotations.lifecycle.PreDestroy;
import org.jacpfx.api.message.Message;
import org.jacpfx.petstore.dto.OrderListDTO;
import org.jacpfx.petstore.dto.ProductListDTO;
import org.jacpfx.petstore.gui.backoffice.configuration.BaseConfig;
import org.jacpfx.petstore.gui.backoffice.fragments.OrderBoxFragment;
import org.jacpfx.petstore.gui.backoffice.fragments.ProductBoxFragment;
import org.jacpfx.petstore.model.Order;
import org.jacpfx.petstore.model.Product;
import org.jacpfx.rcp.component.FXComponent;
import org.jacpfx.rcp.componentLayout.FXComponentLayout;
import org.jacpfx.rcp.components.managedFragment.ManagedFragmentHandler;
import org.jacpfx.rcp.context.Context;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created with IntelliJ IDEA.
 * User: PETE
 * Date: 11/03/14
 * Time: 16:59
 * To change this template use File | Settings | File Templates.
 */

@DeclarativeView(id = BaseConfig.ORDER_COMPONENT_ID, name = "SimpleView",
        active = true,
        resourceBundleLocation = "bundles.languageBundle",
        initialTargetLayoutId = BaseConfig.TARGET_ORDER_COMPONENT_ID,
        viewLocation = "/fxml/OrderView.fxml")
public class OrderComponent implements FXComponent {


    private static final Logger LOGGER = Logger.getLogger(OrderComponent.class.getName());

    @Resource
    private Context context;


    @FXML
    private TilePane orderPane;
    @FXML
    private Label amountLabel;

    private ObservableList<Node> products = FXCollections.emptyObservableList();
    private List<ManagedFragmentHandler<OrderBoxFragment>> fragmentList = new CopyOnWriteArrayList<>();

    @Override
    /**
     * The handle method always runs outside the main application thread. You can create new nodes, execute long running tasks but you are not allowed to manipulate existing nodes here.
     */
    public Node handle(final Message<Event, Object> message) {
        // runs in worker thread

        if (message.isMessageBodyTypeOf(OrderListDTO.class)) {
            OrderListDTO dto = message.getTypedMessageBody(OrderListDTO.class);
            fragmentList.clear();
            final List<ManagedFragmentHandler<OrderBoxFragment>> orders = dto.getOrders().parallelStream().map(order -> createOrderFragment(order)).collect(Collectors.toList());
            fragmentList.addAll(orders);


        }
        return null;
    }

    @Override
    /**
     * The postHandle method runs always in the main application thread.
     */
    public Node postHandle(final Node arg0,
                           final Message<Event, Object> message) {
        // runs in FX application thread
        if (message.isMessageBodyTypeOf(OrderListDTO.class)) {
            OrderListDTO dto = message.getTypedMessageBody(OrderListDTO.class);
            if(dto.getState().equals(OrderListDTO.State.ALL)){
                orderPane.getChildren().clear();
            }
            final List<Node> collect = fragmentList.parallelStream().map(fragment -> fragment.getFragmentNode()).collect(Collectors.toList());
            collect.forEach(elem -> orderPane.getChildren().add(0, elem));
            amountLabel.setText(Integer.toString(orderPane.getChildren().size()));

        }
        return null;
    }



    @PostConstruct
    /**
     * The @OnStart annotation labels methods executed when the component switch from inactive to active state
     * @param arg0
     * @param resourceBundle
     */
    public void onStartComponent(final FXComponentLayout arg0,
                                 final ResourceBundle resourceBundle) {
        orderPane.setTileAlignment(Pos.CENTER);
        orderPane.setVgap(20);
        orderPane.setHgap(20);
        orderPane.setPadding(new Insets(20, 0, 20, 20));

    }

    @PreDestroy
    /**
     * The @OnTearDown annotations labels methods executed when the component is set to inactive
     * @param arg0
     */
    public void onTearDownComponent(final FXComponentLayout arg0) {
        LOGGER.info("run on tear down of ComponentLeft ");

    }



    private ManagedFragmentHandler<OrderBoxFragment> createOrderFragment(Order p) {
        ManagedFragmentHandler<OrderBoxFragment> fragment = context.getManagedFragmentHandler(OrderBoxFragment.class);
        fragment.getController().init(p);
        return fragment;
    }


}
