package kr.entree.spicord.util;

import lombok.experimental.UtilityClass;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

@UtilityClass
public class Proxies {
    @SuppressWarnings("unchecked")
    public <T> T create(Class<T> type, InvocationHandler handler) {
        return (T) Proxy.newProxyInstance(Proxies.class.getClassLoader(), new Class[]{type}, handler);
    }
}
