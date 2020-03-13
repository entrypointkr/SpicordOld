package kr.entree.spicord.util;

import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by JunHyung Lim on 2020-03-13
 */
public class Result<T> {
    private final T result;
    private final Exception exception;
    private static final Result<?> EMPTY = new Result<>(null, null);

    public Result(@Nullable T result, @Nullable Exception exception) {
        this.result = result;
        this.exception = exception;
    }

    @SuppressWarnings("unchecked")
    public static <T> Result<T> empty() {
        return (Result<T>) EMPTY;
    }

    public static <T> Result<T> error(Exception exception) {
        return new Result<>(null, exception);
    }

    public static <T> Result<T> run(Supplier<T> supplier) {
        T result = null;
        Exception exception = null;
        try {
            result = supplier.get();
        } catch (Exception ex) {
            exception = ex;
        }
        return new Result<>(result, exception);
    }

    public Result<T> onSuccess(Consumer<T> receiver) {
        if (result != null) {
            receiver.accept(result);
        }
        return this;
    }

    public Result<T> onFailure(Consumer<Exception> failure) {
        if (exception != null) {
            failure.accept(exception);
        }
        return this;
    }

    public T orElse(T defaultValue) {
        return result != null ? result : defaultValue;
    }
}
