package kr.entree.spicord.bukkit.bootstrap;

import kr.entree.spicord.config.SpicordConfig;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import javax.inject.Inject;

/**
 * Created by JunHyung Lim on 2020-03-13
 */
public class RichPresenceUpdater implements Listener {
    private final Plugin plugin;
    private final SpicordConfig config;

    @Inject
    public RichPresenceUpdater(Plugin plugin, SpicordConfig config) {
        this.plugin = plugin;
        this.config = config;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Bukkit.getScheduler().runTask(plugin, config::updateRichPresence);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Bukkit.getScheduler().runTask(plugin, config::updateRichPresence);
    }
}
