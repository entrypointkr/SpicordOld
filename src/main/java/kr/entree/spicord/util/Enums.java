package kr.entree.spicord.util;

import lombok.experimental.UtilityClass;
import lombok.val;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by JunHyung Lim on 2020-03-13
 */
@SuppressWarnings("unchecked")
@UtilityClass
public class Enums {
    public static <T extends Enum<T>> T valueOf(Class<T> enumClass, String name) {
        try {
            return Enum.valueOf(enumClass, name.toUpperCase());
        } catch (IllegalArgumentException ex) {
            val enumNames = Stream.of(values(enumClass)).map(Enum::name).collect(Collectors.toList());
            val message = "Available enum constants: " + String.join(", ", enumNames);
            throw new IllegalArgumentException(message, ex);
        }
    }

    public static <T extends Enum<T>> T[] values(Class<T> enumClass) {
        try {
            val valuesMethod = enumClass.getMethod("values");
            return (T[]) valuesMethod.invoke(null);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            // Ignore
        }
        return (T[]) Array.newInstance(enumClass, 0);
    }
}
