package org.jacpfx.petstore.gui.backoffice.perspectives;

/**
 * Created with IntelliJ IDEA.
 * User: PETE
 * Date: 11/03/14
 * Time: 07:12
 * To change this template use File | Settings | File Templates.
 */

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import org.jacpfx.api.annotations.Resource;
import org.jacpfx.api.annotations.lifecycle.OnShow;
import org.jacpfx.api.annotations.lifecycle.PostConstruct;
import org.jacpfx.api.annotations.lifecycle.PreDestroy;
import org.jacpfx.api.annotations.perspective.Perspective;
import org.jacpfx.api.message.Message;
import org.jacpfx.api.util.ToolbarPosition;
import org.jacpfx.controls.optionPane.JACPDialogUtil;
import org.jacpfx.petstore.gui.backoffice.configuration.BaseConfig;
import org.jacpfx.rcp.componentLayout.FXComponentLayout;
import org.jacpfx.rcp.componentLayout.PerspectiveLayout;
import org.jacpfx.rcp.components.toolBar.JACPToolBar;
import org.jacpfx.rcp.context.Context;
import org.jacpfx.rcp.perspective.FXPerspective;
import org.jacpfx.rcp.util.FXUtil;

import java.util.ResourceBundle;


@Perspective(id = BaseConfig.PETSTORE_PERSPECTIVE_ID, name = "contactPerspective",
        components = {BaseConfig.PRODUCT_DETAIL_COMPONENT_ID, BaseConfig.PRODUCT_COMPONENT_ID, BaseConfig.WSPRODUCT_COMPONENT_ID},
        viewLocation = "/fxml/ProductPerspective.fxml",
        resourceBundleLocation = "bundles.languageBundle")
public class ProductPerspective implements FXPerspective {
    @Resource
    public Context context;

    @FXML
    private GridPane customerComponent;
    @FXML
    private GridPane productComponent;


    @Override
    public void handlePerspective(final Message<Event, Object> action,
                                  final PerspectiveLayout perspectiveLayout) {
        if (action.messageBodyEquals(FXUtil.MessageUtil.INIT)) {
            GridPane.setVgrow(perspectiveLayout.getRootComponent(),
                    Priority.ALWAYS);
            GridPane.setHgrow(perspectiveLayout.getRootComponent(),
                    Priority.ALWAYS);
            // register left menu
            perspectiveLayout.registerTargetLayoutComponent(BaseConfig.TARGET_CUSTOMER_COMPONENT_ID, this.customerComponent);
            // register main content
            perspectiveLayout.registerTargetLayoutComponent(BaseConfig.TARGET_PRODUCT_COMPONENT_ID, this.productComponent);
        }


    }


    @OnShow
    public void onShow(final FXComponentLayout layout) {

    }

    @PostConstruct
    /**
     * @OnStart annotated method will be executed when components is activated.
     * @param layout
     * @param resourceBundle
     */
    public void onStartPerspective(final FXComponentLayout layout,
                                   final ResourceBundle resourceBundle) {
        // TODO get message from resource
        // define toolbars and menu entries
        JACPToolBar toolbar = layout.getRegisteredToolBar(ToolbarPosition.NORTH);
        Button pressMe = new Button("press me");
        pressMe.setOnAction((event) -> {
            context.showModalDialog(JACPDialogUtil.createOptionPane("Some Dialog", "This is a Dialog, OK?"));
        });
//       toolbar.add(pressMe);
    }

    @PreDestroy
    /**
     * @OnTearDown annotated method will be executed when components is deactivated.
     * @param arg0
     */
    public void onTearDownPerspective(final FXComponentLayout arg0) {
        // remove toolbars and menu entries when close perspectives

    }

}
