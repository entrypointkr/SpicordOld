package kr.entree.spicord.option;

/**
 * Created by JunHyung Lim on 2019-12-01
 */
public interface Option<T> {
    T get();

    void set(T value);
}
