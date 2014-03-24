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
    public static final String ORDER_PERSPECTIVE_ID = "p02";
    // ***** COMPONENTS *****
    public static final String PRODUCT_DETAIL_COMPONENT_ID = "c01";
    public static final String PRODUCT_COMPONENT_ID = "c02";
    public static final String WSPRODUCT_COMPONENT_ID = "c03";
    public static final String ORDER_COMPONENT_ID = "c04";
    public static final String WSORDER_COMPONENT_ID = "c05";
    public static final String ORDER_DETAIL_COMPONENT_ID = "c06";

    // ***** FRAGMENTS *****
    public static final String PRODUCT_BOX_FRAGMENT = "d01";
    public static final String ORDER_BOX_FRAGMENT = "d02";
    // ***** TARGETS *****
    public static final String TARGET_CUSTOMER_COMPONENT_ID = "customerComponent";
    public static final String TARGET_PRODUCT_COMPONENT_ID = "productComponent";
    public static final String TARGET_ORDER_DETAIL_COMPONENT_ID = "detailComponent";
    public static final String TARGET_ORDER_COMPONENT_ID = "orderComponent";

    public static String getGlobalId(final String perspectiveId, final String componentId) {
        return perspectiveId.concat(".").concat(componentId);
    }
}
