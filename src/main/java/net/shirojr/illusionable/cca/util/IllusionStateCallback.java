package net.shirojr.illusionable.cca.util;

import net.shirojr.illusionable.cca.component.IllusionComponent;

public interface IllusionStateCallback {
    default void illusionable$onIllusionStateChanged(IllusionComponent component, boolean isIllusion) {

    }
}
