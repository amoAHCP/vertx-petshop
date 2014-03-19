package org.jacpfx.petstore.gui.backoffice.components;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.jacpfx.api.annotations.Resource;
import org.jacpfx.api.annotations.component.DeclarativeView;
import org.jacpfx.api.annotations.lifecycle.PostConstruct;
import org.jacpfx.api.annotations.lifecycle.PreDestroy;
import org.jacpfx.api.component.Injectable;
import org.jacpfx.api.message.Message;
import org.jacpfx.api.util.ToolbarPosition;
import org.jacpfx.petstore.gui.backoffice.configuration.BaseConfig;
import org.jacpfx.petstore.model.Product;
import org.jacpfx.rcp.component.FXComponent;
import org.jacpfx.rcp.componentLayout.FXComponentLayout;
import org.jacpfx.rcp.components.toolBar.JACPToolBar;
import org.jacpfx.rcp.context.Context;

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
        viewLocation = "/fxml/OrderDetail.fxml")
public class ProductDetailComponent implements FXComponent {

    private static final Logger LOGGER = Logger.getLogger(ProductDetailComponent.class.getName());

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

    private Button save = new Button("save");

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
            current = message.getTypedMessageBody(Product.class);
            name.setText(current.getName());
            price.setText(Double.toString(current.getPrice()));
            amount.setText(Integer.toString(current.getAmount()));
            description.setText(current.getDescription());
        } else if(message.messageBodyEquals("SAVE")) {
            if(current!=null) {

            }
        } else if(message.messageBodyEquals("NEW")) {

        }
        return null;
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

       toolbar.add(new Button("save"));

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
