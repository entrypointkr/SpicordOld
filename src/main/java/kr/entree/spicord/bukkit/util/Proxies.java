package kr.entree.spicord.bukkit.util;

import lombok.experimental.UtilityClass;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

@UtilityClass
public class Proxies {
    @SuppressWarnings("unchecked")
    public static <T> T create(Class<T> type, InvocationHandler handler) {
        return (T) Proxy.newProxyInstance(Proxies.class.getClassLoader(), new Class[]{type}, handler);
    }
}
