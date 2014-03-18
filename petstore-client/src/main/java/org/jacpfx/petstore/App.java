package org.jacpfx.petstore;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.jacpfx.petstore.gui.backoffice.configuration.BaseConfig;
import org.jacpfx.petstore.gui.backoffice.workbench.PetstoreWorkbench;
import org.jacpfx.rcp.workbench.FXWorkbench;
import org.jacpfx.spring.launcher.AFXSpringJavaConfigLauncher;

/**
 * Hello world!
 */
public class App extends AFXSpringJavaConfigLauncher {
    public static final String[] STYLE_FILES = {"/styles/style_light.css"};

    public App() {

    }

    @Override
    protected Class<?>[] getConfigClasses() {
        return new Class<?>[]{BaseConfig.class};
    }

    /**
     * @param args aa  ddd
     */
    public static void main(final String[] args) {
        Application.launch(args);


    }

    @Override
    protected Class<? extends FXWorkbench> getWorkbenchClass() {
        return PetstoreWorkbench.class;
    }

    @Override
    protected String[] getBasePackages() {
        return new String[]{"org.jacpfx.petstore"};
    }

    @Override
    protected void postInit(Stage stage) {
        final Scene scene = stage.getScene();
    }
}
