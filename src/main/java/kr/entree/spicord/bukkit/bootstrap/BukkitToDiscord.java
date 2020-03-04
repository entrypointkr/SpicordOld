package kr.entree.spicord.bukkit.bootstrap;

import kr.entree.spicord.bukkit.VerifiedMemberManager;
import kr.entree.spicord.config.Parameter;
import kr.entree.spicord.config.SpicordConfig;
import kr.entree.spicord.discord.Discord;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;

import javax.inject.Inject;

/**
 * Created by JunHyung Lim on 2019-12-01
 */
public class BukkitToDiscord implements Listener {
    private final SpicordConfig config;
    private final Discord discord;
    private final VerifiedMemberManager verifiedManager;

    @Inject
    public BukkitToDiscord(SpicordConfig config, Discord discord, VerifiedMemberManager verifiedManager) {
        this.config = config;
        this.discord = discord;
        this.verifiedManager = verifiedManager;
    }

    @EventHandler
    public void onKick(PlayerKickEvent e) {
        Player player = e.getPlayer();
        discord.addTask(config.getFeature(
                "player-kick",
                new Parameter().put(player)
                        .put("%reason%", e.getReason())
        ));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        verifiedManager.update(e.getPlayer());
    }
}
