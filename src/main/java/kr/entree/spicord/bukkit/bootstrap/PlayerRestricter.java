package kr.entree.spicord.bukkit.bootstrap;

import kr.entree.spicord.bukkit.VerifiedMemberManager;
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
import java.util.UUID;

/**
 * Created by JunHyung Lim on 2019-11-27
 */
public class PlayerRestricter implements Listener {
    private final VerifiedMemberManager manager;
    private final SpicordConfig config;
    private final LangConfig langConfig;
    private final CooldownMap<UUID> cools = new CooldownMap<>();

    public PlayerRestricter(VerifiedMemberManager manager, SpicordConfig config, LangConfig langConfig) {
        this.manager = manager;
        this.config = config;
        this.langConfig = langConfig;
    }

    private void tryRestrict(Player player, Cancellable cancellable, RestrictType type) {
        if (player.isOp() || player.hasPermission("spicord.verify.bypass")) {
            return;
        }
        VerificationConfig verifyConfig = config.getVerification();
        if (!verifyConfig.isEnabled()) {
            return;
        }
        Long discordId = manager.getDiscord(player.getUniqueId());
        if (discordId == null) {
            Set<RestrictType> types = verifyConfig.getPlayerRestricts();
            if (types.contains(type)) {
                cancellable.setCancelled(true);
                if (cools.action(player.getUniqueId(), 3000) <= 0) {
                    player.sendMessage(langConfig.format(
                            Lang.VERIFY_NEEDS,
                            new Parameter().put(player)
                                    .put("%command%", verifyConfig.getVerificationCommandPrefix())
                    ));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent e) {
        tryRestrict(e.getPlayer(), e, RestrictType.CHAT);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent e) {
        tryRestrict(e.getPlayer(), e, RestrictType.BUILD);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreak(BlockBreakEvent e) {
        tryRestrict(e.getPlayer(), e, RestrictType.BUILD);
    }

    @EventHandler(ignoreCancelled = true)
    public void onWalk(PlayerMoveEvent e) {
        Location to = e.getTo();
        if (to == null) {
            return;
        }
        Location from = e.getFrom();
        Player player = e.getPlayer();
        if (from.getBlockX() != to.getBlockX()
                || from.getBlockY() != to.getBlockY()
                || from.getBlockZ() != to.getBlockZ()) {
            tryRestrict(player, e, RestrictType.MOVE);
            if (e.isCancelled()) {
                e.setCancelled(false);
                e.setTo(new Location(
                        from.getWorld(),
                        from.getBlockX(), from.getBlockY(), from.getBlockZ(),
                        to.getYaw(), to.getPitch()
                ));
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player) {
            tryRestrict((Player) e.getDamager(), e, RestrictType.PVP);
        }
        if (e.getEntity() instanceof Player) {
            tryRestrict((Player) e.getEntity(), e, RestrictType.PVP);
        }
    }
}
