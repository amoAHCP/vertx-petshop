package org.jacpfx.petstore.gui.backoffice.components;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.jacpfx.api.annotations.Resource;
import org.jacpfx.api.annotations.component.View;
import org.jacpfx.api.annotations.lifecycle.PostConstruct;
import org.jacpfx.api.annotations.lifecycle.PreDestroy;
import org.jacpfx.api.message.Message;
import org.jacpfx.petstore.dto.ProductListDTO;
import org.jacpfx.petstore.gui.backoffice.configuration.BaseConfig;
import org.jacpfx.petstore.gui.backoffice.fragments.OrderBoxFragment;
import org.jacpfx.petstore.model.Product;
import org.jacpfx.rcp.component.FXComponent;
import org.jacpfx.rcp.componentLayout.FXComponentLayout;
import org.jacpfx.rcp.components.managedFragment.ManagedFragmentHandler;
import org.jacpfx.rcp.context.Context;
import org.jacpfx.rcp.util.CSSUtil;
import org.jacpfx.rcp.util.FXUtil;

import java.util.List;
import java.util.ResourceBundle;
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

@View(id = BaseConfig.PRODUCT_COMPONENT_ID, name = "SimpleView", active = true, resourceBundleLocation = "bundles.languageBundle", initialTargetLayoutId = BaseConfig.TARGET_PRODUCT_COMPONENT_ID)
public class ProductComponent implements FXComponent {



    private static final Logger LOGGER = Logger.getLogger(ProductComponent.class.getName());

    @Resource
    private Context context;

    private BorderPane mainPane;

    TilePane tile = new TilePane(Orientation.HORIZONTAL);

    private ObservableList<Node> products = FXCollections.emptyObservableList();
    private List<ManagedFragmentHandler<OrderBoxFragment>> fragmentList = new CopyOnWriteArrayList<>();
    private List<ManagedFragmentHandler<OrderBoxFragment>> fragmentSubList = new CopyOnWriteArrayList<>();

    @Override
    /**
     * The handle method always runs outside the main application thread. You can create new nodes, execute long running tasks but you are not allowed to manipulate existing nodes here.
     */
    public Node handle(final Message<Event, Object> message) {
        // runs in worker thread

        if (message.messageBodyEquals(FXUtil.MessageUtil.INIT)) {
            return createUI();
        } else if(message.isMessageBodyTypeOf(ProductListDTO.class)){
            ProductListDTO dto = message.getTypedMessageBody(ProductListDTO.class);

            final List<ManagedFragmentHandler<OrderBoxFragment>> collect = dto.getProducts().parallelStream().map(p -> createOrderFragment(p)).collect(Collectors.toList());
            if(dto.getState().equals(ProductListDTO.State.ALL)) {
                fragmentList.clear();
                fragmentList.addAll(collect);
            } else {
                fragmentSubList.clear();
                fragmentSubList.addAll(collect);
                fragmentList.addAll(collect);

            }

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
        if (message.messageBodyEquals(FXUtil.MessageUtil.INIT)) {
            this.mainPane = (BorderPane) arg0;
        } else if(message.isMessageBodyTypeOf(ProductListDTO.class)){
            ProductListDTO dto = message.getTypedMessageBody(ProductListDTO.class);
            if(dto.getState().equals(ProductListDTO.State.ALL)) {
                final List<Node> collect = fragmentList.parallelStream().map(fragment -> fragment.getFragmentNode()).collect(Collectors.toList());
                tile.getChildren().clear();
                tile.getChildren().addAll(collect);
            } else {
                final List<Node> collect1 = fragmentSubList.parallelStream().map(fragment -> fragment.getFragmentNode()).collect(Collectors.toList());
                tile.getChildren().addAll(collect1);
            }

        }
        return this.mainPane;
    }

    @PostConstruct
    /**
     * The @OnStart annotation labels methods executed when the component switch from inactive to active state
     * @param arg0
     * @param resourceBundle
     */
    public void onStartComponent(final FXComponentLayout arg0,
                                 final ResourceBundle resourceBundle) {


    }

    @PreDestroy
    /**
     * The @OnTearDown annotations labels methods executed when the component is set to inactive
     * @param arg0
     */
    public void onTearDownComponent(final FXComponentLayout arg0) {
        LOGGER.info("run on tear down of ComponentLeft ");

    }

    /**
     * create the UI on first call
     *
     * @return
     */
    private Node createUI() {
        this.mainPane =  new BorderPane();

        tile.setTileAlignment(Pos.CENTER);
        tile.setVgap(20);
        tile.setHgap(20);
        tile.setPadding(new Insets(20,0,20,20));


       /* int i = 42;
        while (--i >= 0) {
            tile.getChildren().add(this.createRectangle());
        }*/
        ScrollPane scrollPane = new ScrollPane(tile);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        this.mainPane.setCenter(scrollPane);
        return this.mainPane;
    }

    private ManagedFragmentHandler<OrderBoxFragment> createOrderFragment(Product p) {
        ManagedFragmentHandler<OrderBoxFragment> fragment = context.getManagedFragmentHandler(OrderBoxFragment.class);
        fragment.getController().init(p);
        return fragment;
    }


}
