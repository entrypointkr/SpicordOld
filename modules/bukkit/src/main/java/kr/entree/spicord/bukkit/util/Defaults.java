package kr.entree.spicord.bukkit.util;

import lombok.experimental.UtilityClass;
import lombok.val;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class Defaults {
    private static final Map<Class<?>, Object> DEFAULTS = new HashMap<>();

    static {
        DEFAULTS.put(boolean.class, false);
        DEFAULTS.put(char.class, '\0');
        DEFAULTS.put(byte.class, (byte) 0);
        DEFAULTS.put(short.class, (short) 0);
        DEFAULTS.put(int.class, 0);
        DEFAULTS.put(long.class, 0L);
        DEFAULTS.put(float.class, 0f);
        DEFAULTS.put(double.class, 0d);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> T defaultValue(Class<T> type) {
        val ret = DEFAULTS.get(type);
        return ret != null ? (T) ret : null;
    }
}
