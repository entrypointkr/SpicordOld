package kr.entree.spicord.property;

/**
 * Created by JunHyung Lim on 2019-12-01
 */
public interface Property<T> {
    T get();

    void set(T value);
}
