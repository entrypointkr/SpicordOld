package kr.entree.spicord.option;

import lombok.experimental.Delegate;

/**
 * Created by JunHyung Lim on 2019-12-01
 */
public class NumberOption implements Option<Number> {
    @Delegate
    private final Option<Number> option;

    public NumberOption(Option<Number> option) {
        this.option = option;
    }

    public long getLong() {
        return get().longValue();
    }

    public int getInt() {
        return get().intValue();
    }
}
