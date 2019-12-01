package kr.entree.spicord.option;

import lombok.experimental.Delegate;

/**
 * Created by JunHyung Lim on 2019-12-01
 */
public class BooleanOption implements Option<Boolean> {
    @Delegate
    private final Option<Boolean> option;

    public BooleanOption(Option<Boolean> option) {
        this.option = option;
    }

    public boolean isTrue() {
        return Boolean.TRUE.equals(get());
    }
}
