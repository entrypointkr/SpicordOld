package kr.entree.spicord.bukkit;

import kr.entree.spicord.bukkit.restrict.RestrictType;
import kr.entree.spicord.bukkit.util.CooldownMap;
import kr.entree.spicord.config.Lang;
import kr.entree.spicord.config.LangConfig;
import kr.entree.spicord.config.Parameter;
import kr.entree.spicord.config.SpicordConfig;
import kr.entree.spicord.config.VerificationConfig;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Set;

/**
 * Created by JunHyung Lim on 2019-11-27
 */
public class UserRestricter implements Listener {
    private final VerifiedMemberManager manager;
    private final SpicordConfig config;
    private final LangConfig langConfig;
    private final CooldownMap cools = new CooldownMap();

    public UserRestricter(VerifiedMemberManager manager, SpicordConfig config, LangConfig langConfig) {
        this.manager = manager;
        this.config = config;
        this.langConfig = langConfig;
    }

    private void tryRestrict(Player player, Cancellable cancellable, RestrictType type) {
        VerificationConfig verifyConfig = config.getVerification();
        if (!verifyConfig.isEnabled()) {
            return;
        }
        Long discordId = manager.getDiscord(player.getUniqueId());
        if (discordId == null) {
            Set<RestrictType> types = verifyConfig.getPlayerRestricts();
            if (types.contains(type)) {
                cancellable.setCancelled(true);
                if (cools.action(player.getUniqueId(), 3000)) {
                    player.sendMessage(langConfig.format(
                            Lang.NEEDS_VERIFY,
                            new Parameter().put(player)
                                    .put("%command%", verifyConfig.getVerificationCommandPrefix())
                    ));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent e) {
        tryRestrict(e.getPlayer(), e, RestrictType.CHAT);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        tryRestrict(e.getPlayer(), e, RestrictType.BUILD);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        tryRestrict(e.getPlayer(), e, RestrictType.BUILD);
    }

    @EventHandler
    public void onWalk(PlayerMoveEvent e) {
        Location from = e.getFrom();
        Location to = e.getTo();
        if (to == null) {
            return;
        }
        if (from.getBlockX() != to.getBlockX()
                || from.getBlockY() != to.getBlockY()
                || from.getBlockZ() != to.getBlockZ()) {
            tryRestrict(e.getPlayer(), e, RestrictType.MOVE);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player) {
            Player damagerPlayer = ((Player) e.getDamager());
            tryRestrict(damagerPlayer, e, RestrictType.PVP);
        }
    }
}
