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
import org.jacpfx.api.annotations.Resource;
import org.jacpfx.api.annotations.component.DeclarativeView;
import org.jacpfx.api.annotations.lifecycle.PostConstruct;
import org.jacpfx.api.annotations.lifecycle.PreDestroy;
import org.jacpfx.api.message.Message;
import org.jacpfx.petstore.dto.ProductListDTO;
import org.jacpfx.petstore.gui.backoffice.configuration.BaseConfig;
import org.jacpfx.petstore.gui.backoffice.fragments.ProductBoxFragment;
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

    private BorderPane mainPane;

    private TilePane tile = new TilePane(Orientation.HORIZONTAL);

    private ObservableList<Node> products = FXCollections.emptyObservableList();
    private List<ManagedFragmentHandler<ProductBoxFragment>> fragmentList = new CopyOnWriteArrayList<>();
    private List<ManagedFragmentHandler<ProductBoxFragment>> fragmentSubList = new CopyOnWriteArrayList<>();

    @Override
    /**
     * The handle method always runs outside the main application thread. You can create new nodes, execute long running tasks but you are not allowed to manipulate existing nodes here.
     */
    public Node handle(final Message<Event, Object> message) {
        // runs in worker thread

        if (message.isMessageBodyTypeOf(ProductListDTO.class)) {
            ProductListDTO dto = message.getTypedMessageBody(ProductListDTO.class);

            final List<ManagedFragmentHandler<ProductBoxFragment>> collect = dto.getProducts().parallelStream().map(p -> createOrderFragment(p)).collect(Collectors.toList());
            if (dto.getState().equals(ProductListDTO.State.ALL)) {
                fragmentList.clear();
                fragmentList.addAll(collect);
            } else {
                fragmentSubList.clear();
                fragmentSubList.addAll(collect);


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
        if (message.isMessageBodyTypeOf(ProductListDTO.class)) {
            ProductListDTO dto = message.getTypedMessageBody(ProductListDTO.class);
            if (dto.getState().equals(ProductListDTO.State.ALL)) {
                final List<Node> collect = fragmentList.parallelStream().map(fragment -> fragment.getFragmentNode()).collect(Collectors.toList());
                tile.getChildren().clear();
                tile.getChildren().addAll(collect);
            } else {
                addOrReplaceProduct();
            }

        }
        return null;
    }

    private void addOrReplaceProduct() {
        final Map<Integer, ManagedFragmentHandler<ProductBoxFragment>> indexMap = findProductsToReplace();
        if (indexMap.isEmpty()) {
            // add new products
            final List<Node> collect1 = fragmentSubList.parallelStream().map(fragment -> fragment.getFragmentNode()).collect(Collectors.toList());
            tile.getChildren().addAll(collect1);
        } else {
            // replace product
            indexMap.entrySet().forEach(entry -> {
                fragmentList.remove(entry.getKey());
                tile.getChildren().set(entry.getKey(), entry.getValue().getFragmentNode());
            });
        }

        fragmentList.addAll(fragmentSubList);
    }

    private Map<Integer, ManagedFragmentHandler<ProductBoxFragment>> findProductsToReplace() {
        final Map<Integer, ManagedFragmentHandler<ProductBoxFragment>> indexMap = new HashMap<>();
        fragmentSubList.forEach(p -> {
            final Optional<ManagedFragmentHandler<ProductBoxFragment>> first = fragmentList.parallelStream().filter(fragment -> fragment.getController().equals(p.getController().getProduct())).findFirst();
            if (first.isPresent()) {
                int index = fragmentList.indexOf(first.get());
                indexMap.put(index, p);
            }

        });
        return indexMap;
    }

    @PostConstruct
    /**
     * The @OnStart annotation labels methods executed when the component switch from inactive to active state
     * @param arg0
     * @param resourceBundle
     */
    public void onStartComponent(final FXComponentLayout arg0,
                                 final ResourceBundle resourceBundle) {
        createUI();

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
        this.mainPane = new BorderPane();

        tile.setTileAlignment(Pos.CENTER);
        tile.setVgap(20);
        tile.setHgap(20);
        tile.setPadding(new Insets(20, 0, 20, 20));

        ScrollPane scrollPane = new ScrollPane(tile);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        this.mainPane.setCenter(scrollPane);
        return this.mainPane;
    }

    private ManagedFragmentHandler<ProductBoxFragment> createOrderFragment(Product p) {
        ManagedFragmentHandler<ProductBoxFragment> fragment = context.getManagedFragmentHandler(ProductBoxFragment.class);
        fragment.getController().init(p);
        return fragment;
    }


}
