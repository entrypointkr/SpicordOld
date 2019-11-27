package kr.entree.spicord.bukkit.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by JunHyung Lim on 2019-11-25
 */
public class Compatibles {
    private Compatibles() {
    }

    @SuppressWarnings("unchecked")
    public static Collection<? extends Player> getOnlinePlayers() {
        Collection<? extends Player> ret;
        try {
            Method onlinePlayersMethod = Class.forName("org.bukkit.Server").getMethod("getOnlinePlayers");
            ret = onlinePlayersMethod.getReturnType().equals(Collection.class)
                    ? ((Collection<Player>) onlinePlayersMethod.invoke(Bukkit.getServer()))
                    : Arrays.asList((Player[]) onlinePlayersMethod.invoke(Bukkit.getServer()));
        } catch (Exception e) {
            ret = Bukkit.getOnlinePlayers();
        }
        return ret;
    }
}
