package kr.entree.spicord.option;

import lombok.experimental.Delegate;

import java.util.function.LongSupplier;

/**
 * Created by JunHyung Lim on 2019-12-01
 */
public class NumberOption implements Option<Number>, LongSupplier {
    @Delegate
    private final Option<Number> option;

    public NumberOption(Option<Number> option) {
        this.option = option;
    }

    public long getLong() {
        return get().longValue();
    }

    @Override
    public long getAsLong() {
        return getLong();
    }

    public int getInt() {
        return get().intValue();
    }
}
