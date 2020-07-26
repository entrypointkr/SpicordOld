package kr.entree.spicord.discord;

import dagger.Reusable;
import io.vavr.collection.Stream;
import io.vavr.control.Try;
import kr.entree.spicord.Spicord;
import kr.entree.spicord.bukkit.DiscordEventToBukkit;
import kr.entree.spicord.bukkit.util.ChatStyles;
import kr.entree.spicord.bukkit.util.PlayerData;
import kr.entree.spicord.discord.exception.NoChannelFoundException;
import kr.entree.spicord.discord.exception.NoGuildFoundException;
import kr.entree.spicord.discord.exception.NoUserFoundException;
import kr.entree.spicord.discord.task.guild.GuildTask;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import javax.inject.Inject;
import javax.security.auth.login.LoginException;
import java.awt.*;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;
import java.util.logging.Level;

/**
 * Created by JunHyung Lim on 2019-11-16
 */
@Reusable
public class Discord implements Runnable {
    private final Plugin plugin;
    private final Queue<JDAHandler> consumers = new ConcurrentLinkedDeque<>();
    @Getter
    private JDA jda = null;
    @Getter
    @Setter
    private String token = null;
    private static final ReentrantLock COLOR_ROLE_LOCK = new ReentrantLock();

    @Inject
    public Discord(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                preProcess();
                takeAndNotifyConsumer();
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        shutdownJDA();
    }

    public void addTask(JDAHandler consumer) {
        this.consumers.add(consumer);
    }

    private void shutdownJDA() {
        if (jda != null) {
            jda.shutdownNow();
        }
    }

    private void preProcess() throws InterruptedException {
        if (token != null && (jda == null || !jda.getToken().contains(token))) {
            shutdownJDA();
            JDA newJda;
            try {
                newJda = new JDABuilder(token)
                        .addEventListeners(new DiscordEventToBukkit(plugin, this))
                        .build();
                newJda.awaitReady();
            } catch (LoginException e) {
                plugin.getLogger().log(Level.WARNING, String.format("Error while starting discord bot with token: \"%s\"", token), e);
                token = null;
                jda = null;
                return;
            }
            jda = newJda;
        }
    }

    private void takeAndNotifyConsumer() {
        if (jda == null) {
            return;
        }
        val consumer = consumers.poll();
        if (consumer == null) {
            return;
        }
        val logger = plugin.getLogger();
        try {
            consumer.handle(jda);
        } catch (NoGuildFoundException ex) {
            logger.log(Level.WARNING, "Unknown guild id: {0}", ex.getGuildId());
        } catch (NoChannelFoundException ex) {
            logger.log(Level.WARNING, "Unknown channel id: {0}", ex.getChannelId());
        } catch (NoUserFoundException ex) {
            logger.log(Level.WARNING, "Unknown user id: {0}", ex.getUserId());
        } catch (Exception ex) {
            logger.log(Level.WARNING, ex, () -> "Exception!");
        }
    }

    public void retrieveColorRole(Color color, Guild guild, BiConsumer<JDA, Role> receiver) {
        val name = formatColorToRoleName(color);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            COLOR_ROLE_LOCK.lock();
            val role = guild.getRolesByName(name, false).stream().findFirst().orElse(null);
            if (role != null) {
                addTask(_jda -> receiver.accept(_jda, role));
            } else {
                Try.of(() -> guild.createRole().complete()).andThen(newRole -> {
                    val manager = newRole.getManager();
                    Try.of(() -> manager.setName(name).complete())
                            .andThen(() -> manager.setColor(color).complete())
                            .onSuccess(v -> receiver.accept(jda, newRole))
                            .onFailure(ex -> {
                                Spicord.log(ex);
                                newRole.delete().complete();
                            });
                });
            }
            COLOR_ROLE_LOCK.unlock();
        });
    }

    public void retrieveColorRole(Color color, long guildId, BiConsumer<JDA, Role> receiver) {
        addTask(new GuildTask(guildId, (jda, guild) -> retrieveColorRole(color, guild, receiver)));
    }

    public void colorizeDiscordName(PlayerData data, long discordUserId, long guildId) {
        val displayName = data.getDisplayName();
        val color = ChatStyles.bukkitToAwt(ChatStyles.getFirstColor(displayName));
        Spicord.get().getDiscord().addTask(jda -> {
            val guild = jda.getGuildById(guildId);
            if (guild == null) return;
            val member = guild.getMemberById(discordUserId);
            if (member == null) return;
            if (color != null) {
                retrieveColorRole(color, guild, (_jda, role) -> {
                    guild.addRoleToMember(member, role).queue();
                    Stream.ofAll(member.getRoles()).find(r ->
                            !r.hasPermission(Permission.ADMINISTRATOR)
                    ).peek(r -> guild.modifyRolePositions().selectPosition(role).moveTo(r.getPosition()).queue());
                });
            } else {
                member.getRoles().stream().filter(role -> role.getName().startsWith("RGB "))
                        .forEach(role -> guild.removeRoleFromMember(member, role).queue());
            }
        });
    }

    public static String formatColorToRoleName(Color color) {
        return "RGB " + color.getRGB();
    }
}
