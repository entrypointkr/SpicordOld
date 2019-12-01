package kr.entree.spicord.bukkit.bootstrap;

import kr.entree.spicord.config.Parameter;
import kr.entree.spicord.config.SpicordConfig;
import kr.entree.spicord.discord.Discord;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;

/**
 * Created by JunHyung Lim on 2019-12-01
 */
public class BukkitToDiscord implements Listener {
    private final SpicordConfig config;
    private final Discord discord;

    public BukkitToDiscord(SpicordConfig config, Discord discord) {
        this.config = config;
        this.discord = discord;
    }

    @EventHandler
    public void onKick(PlayerKickEvent e) {
        Player player = e.getPlayer();
        discord.addTask(config.getSendMessage(
                "player-kick",
                new Parameter().put(player)
                        .put("%reason%", e.getReason())
        ));
    }
}
