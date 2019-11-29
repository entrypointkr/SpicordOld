package kr.entree.spicord.bukkit;

import kr.entree.spicord.config.Lang;
import kr.entree.spicord.config.LangConfig;
import kr.entree.spicord.config.Parameter;
import kr.entree.spicord.config.SpicordConfig;
import kr.entree.spicord.config.VerificationConfig;
import kr.entree.spicord.discord.Discord;
import kr.entree.spicord.discord.handler.PlainMessage;
import kr.entree.spicord.discord.supplier.PrivateChannelSupplier;
import kr.entree.spicord.discord.task.ChannelHandler;
import lombok.val;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by JunHyung Lim on 2019-11-20
 */
public class UserVerifier extends ListenerAdapter {
    private final Plugin plugin;
    private final Discord discord;
    private final SpicordConfig spicordConfig;
    private final LangConfig langConfig;
    private final VerifiedMemberManager manager;
    private final Map<Long, Verification> verifyMap = new ConcurrentHashMap<>();
    private final Random random = new Random();

    public UserVerifier(Plugin plugin, Discord discord, SpicordConfig spicordConfig, LangConfig langConfig, VerifiedMemberManager manager) {
        this.plugin = plugin;
        this.discord = discord;
        this.spicordConfig = spicordConfig;
        this.langConfig = langConfig;
        this.manager = manager;
    }

    private VerificationConfig getConfig() {
        return spicordConfig.getVerification();
    }

    private boolean isVerified(Member member) {
        return manager.getMinecraft(member.getIdLong()) != null;
    }

    private void verified(Member member, Verification verification) {
        val id = member.getIdLong();
        Optional<Player> playerOpt = verification.getPlayer();
        if (playerOpt.isPresent()) {
            Player player = playerOpt.get();
            Bukkit.getScheduler().runTask(plugin, () -> {
                manager.put(id, verification.getUuid());
                getConfig().executeCommands(Bukkit.getConsoleSender(), player);
            });
            discord.addTask(jda -> {
                Guild guild = member.getGuild();
                for (Role role : getConfig().getDiscordRoles(guild)) {
                    guild.addRoleToMember(member, role).queue();
                }
            });
        } else {
            sendMessage(member.getUser(), langConfig.format(
                    Lang.NO_PLAYER_FOUND,
                    new Parameter().put("%player%", verification.getName())
            ));
        }
    }

    private static ChannelHandler<PrivateChannel> createMessage(Member member, String message) {
        return ChannelHandler.of(new PrivateChannelSupplier(member), new PlainMessage<>(message));
    }

    private void requestVerify(Member member, String mcName) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            if (isVerified(member)) {
                sendMessage(member.getUser(), langConfig.format(Lang.ALREADY_VERIFIED));
                return;
            }
            Player player = Bukkit.getPlayer(mcName);
            if (player != null) {
                String code = generateCode();
                verifyMap.put(member.getIdLong(), new Verification(player, code));
                player.sendMessage(langConfig.format(
                        Lang.VERIFY_MESSAGE,
                        new Parameter().put(player).put("%discord%", member.getEffectiveName())
                                .put("%code%", code)
                ));
            } else {
                discord.addTask(createMessage(member, langConfig.format(
                        Lang.NO_PLAYER_FOUND,
                        new Parameter().put("%player%", mcName)
                )));
            }
        });
    }

    public String generateCode() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            builder.append(random.nextInt(10));
        }
        return builder.toString();
    }

    private void sendMessage(User user, String message) {
        user.openPrivateChannel().queue(channel -> channel.sendMessage(message).queue());
    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        val member = event.getMember();
        User user = event.getAuthor();
        if (event.isWebhookMessage() || member == null || user.isBot()) {
            return;
        }
        if (!getConfig().isEnabled()) {
            return;
        }
        val prefix = getConfig().getVerificationCommandPrefix();
        val message = event.getMessage();
        val content = message.getContentDisplay();
        val parameter = new Parameter().put("%command%", prefix);
        if (content.startsWith(prefix) && content.length() >= prefix.length() + 1) {
            val argument = content.substring(prefix.length() + 1);
            val verification = verifyMap.remove(member.getIdLong());
            if (verification != null) {
                if (verification.match(argument)) {
                    verified(member, verification);
                } else {
                    sendMessage(user, langConfig.format(Lang.NOT_MATCHES_CODE, parameter));
                }
            } else {
                requestVerify(member, argument);
            }
        } else {
            sendMessage(user, langConfig.format(Lang.VERIFY_USAGE, parameter));
        }
    }
}
