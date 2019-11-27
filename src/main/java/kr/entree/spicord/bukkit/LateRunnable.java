package kr.entree.spicord.bukkit;

import kr.entree.spicord.Spicord;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * Created by JunHyung Lim on 2019-11-19
 */
public class LateRunnable implements Runnable {
    private final Plugin plugin;
    private final Runnable runnable;

    private LateRunnable(Plugin plugin, Runnable runnable) {
        this.plugin = plugin;
        this.runnable = runnable;
    }

    public static LateRunnable of(Plugin plugin, Runnable runnable) {
        return new LateRunnable(plugin, runnable);
    }

    public static LateRunnable of(Runnable runnable) {
        return of(Spicord.get(), runnable);
    }

    @Override
    public void run() {
        Bukkit.getScheduler().runTask(plugin, runnable);
    }
}
