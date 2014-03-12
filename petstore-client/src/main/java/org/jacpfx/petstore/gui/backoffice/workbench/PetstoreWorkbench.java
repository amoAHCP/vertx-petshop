package org.jacpfx.petstore.gui.backoffice.workbench;

import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import org.jacpfx.api.annotations.workbench.Workbench;
import org.jacpfx.api.componentLayout.WorkbenchLayout;
import org.jacpfx.api.message.Message;
import org.jacpfx.api.util.ToolbarPosition;
import org.jacpfx.petstore.gui.backoffice.configuration.BaseConfig;
import org.jacpfx.rcp.componentLayout.FXComponentLayout;
import org.jacpfx.rcp.components.menuBar.JACPMenuBar;
import org.jacpfx.rcp.workbench.FXWorkbench;

/**
 * Created with IntelliJ IDEA.
 * User: PETE
 * Date: 11/03/14
 * Time: 07:11
 * To change this template use File | Settings | File Templates.
 */

@Workbench(id = BaseConfig.WORKBENCH_ID, name = "workbench", perspectives = BaseConfig.PETSTORE_PERSPECTIVE_ID)
public class PetstoreWorkbench implements FXWorkbench {
    private Stage stage;

    @Override
    public void postHandle(FXComponentLayout layout) {
        final JACPMenuBar menu = layout.getMenu();
        final Menu menuFile = new Menu("File");
        menuFile.getItems().addAll(this.createExitEntry());
        menu.getMenus().addAll(menuFile);
    }

    @Override
    public void handleInitialLayout(Message<Event, Object> action, WorkbenchLayout<Node> layout, Stage stage) {
        this.stage = stage;
        layout.setWorkbenchXYSize(1280, 1024);
        layout.registerToolBars(ToolbarPosition.NORTH);
        layout.setMenuEnabled(true);
    }

    private MenuItem createExitEntry() {
        final MenuItem itemExit = new MenuItem("Exit");
        itemExit.setOnAction((event) -> System.exit(0));
        return itemExit;
    }

}

