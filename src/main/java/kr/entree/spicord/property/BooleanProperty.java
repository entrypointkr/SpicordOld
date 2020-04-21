package kr.entree.spicord.property;

import lombok.experimental.Delegate;

/**
 * Created by JunHyung Lim on 2019-12-01
 */
public class BooleanProperty implements Property<Boolean> {
    @Delegate
    private final Property<Boolean> option;

    public BooleanProperty(Property<Boolean> option) {
        this.option = option;
    }

    public boolean isTrue() {
        return Boolean.TRUE.equals(get());
    }
}
