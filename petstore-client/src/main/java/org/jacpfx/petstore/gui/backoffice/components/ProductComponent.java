package org.jacpfx.petstore.gui.backoffice.components;

import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.jacpfx.api.annotations.Resource;
import org.jacpfx.api.annotations.component.View;
import org.jacpfx.api.annotations.lifecycle.PostConstruct;
import org.jacpfx.api.annotations.lifecycle.PreDestroy;
import org.jacpfx.api.message.Message;
import org.jacpfx.petstore.gui.backoffice.configuration.BaseConfig;
import org.jacpfx.rcp.component.FXComponent;
import org.jacpfx.rcp.componentLayout.FXComponentLayout;
import org.jacpfx.rcp.context.Context;
import org.jacpfx.rcp.util.CSSUtil;
import org.jacpfx.rcp.util.FXUtil;

import java.util.ResourceBundle;
import java.util.logging.Logger;

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

    @Override
    /**
     * The handle method always runs outside the main application thread. You can create new nodes, execute long running tasks but you are not allowed to manipulate existing nodes here.
     */
    public Node handle(final Message<Event, Object> message) {
        // runs in worker thread
        if (message.messageBodyEquals(FXUtil.MessageUtil.INIT)) {
            return createUI();
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

        TilePane tile = new TilePane(Orientation.HORIZONTAL);
        tile.setTileAlignment(Pos.CENTER);
        tile.setVgap(20);
        tile.setHgap(20);
        tile.setPadding(new Insets(50));

        int i = 42;
        while (--i >= 0) {
            tile.getChildren().add(this.createRectangle());
        }

        this.mainPane.setCenter(tile);
        return this.mainPane;
    }

    private Rectangle createRectangle() {
        Rectangle rect = new Rectangle(120, 120);
        rect.setFill(Color.LIGHTGRAY);
        return rect;
    }


}
