package org.jacpfx.petstore.gui.backoffice.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Andy Moncsek on 31.01.14.
 * The spring java configuration and component id config
 */
@Configuration
@ComponentScan
public class BaseConfig {
    // ***** WORKBENCH *****
    public static final String WORKBENCH_ID = "w01";
    // ***** PERSPECTIVES *****
    public static final String PETSTORE_PERSPECTIVE_ID = "p01";
    // ***** COMPONENTS *****
    public static final String CUSTOMER_COMPONENT_ID = "c01";
    public static final String PRODUCT_COMPONENT_ID = "c02";

    // ***** TARGETS *****
    public static final String TARGET_CUSTOMER_COMPONENT_ID = "customerComponent";
    public static final String TARGET_PRODUCT_COMPONENT_ID = "productComponent";

    public static String getGlobalId(final String perspectiveId, final String componentId) {
        return perspectiveId.concat(".").concat(componentId);
    }
}
