package kr.entree.spicord.util;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Predicate;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Option<T> {
    private static final Option<Object> EMPTY = new Option<>(null);
    private final T value;

    public static <T> Option<T> of(@Nullable T value) {
        return value != null ? new Option<>(value) : empty();
    }

    @SuppressWarnings("unchecked")
    public static <T> Option<T> empty() {
        return (Option<T>) EMPTY;
    }

    public boolean isPresent() {
        return value != null;
    }

    public void ifPresent(Consumer<T> consumer) {
        if (isPresent()) {
            consumer.accept(value);
        }
    }

    public T or(T elseValue) {
        return isPresent() ? value : elseValue;
    }

    public boolean is(Predicate<T> predicate) {
        return isPresent() && predicate.test(value);
    }
}
