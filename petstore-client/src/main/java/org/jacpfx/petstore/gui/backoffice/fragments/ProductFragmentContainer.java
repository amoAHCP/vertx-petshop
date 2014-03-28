package org.jacpfx.petstore.gui.backoffice.fragments;

import org.jacpfx.controls.flip.FlippingPanel;
import org.jacpfx.rcp.components.managedFragment.ManagedFragmentHandler;

/**
 * @author Patrick Symmangk (pete.jacp@gmail.com)
 */
public class ProductFragmentContainer {

    private ManagedFragmentHandler<ProductBoxFragment> productBoxFragment;

    private ManagedFragmentHandler<ProductInformationBoxFragment> productInformationBoxFragment;

    private FlippingPanel flip;

    public ProductFragmentContainer(ManagedFragmentHandler<ProductBoxFragment> productBoxFragment, ManagedFragmentHandler<ProductInformationBoxFragment> productInformationBoxFragment) {
        this.productBoxFragment = productBoxFragment;
        this.productInformationBoxFragment = productInformationBoxFragment;
    }

    public ManagedFragmentHandler<ProductBoxFragment> getProductBoxFragment() {
        return productBoxFragment;
    }

    public ManagedFragmentHandler<ProductInformationBoxFragment> getProductInformationBoxFragment() {
        return productInformationBoxFragment;
    }

    public void setFlippingPanel(final FlippingPanel flip) {
        this.flip = flip;
    }

    public FlippingPanel getFlip() {
        return flip;
    }
}
