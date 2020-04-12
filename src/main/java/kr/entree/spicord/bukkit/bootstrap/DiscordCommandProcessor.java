package kr.entree.spicord.bukkit.bootstrap;

import kr.entree.spicord.Spicord;
import kr.entree.spicord.bukkit.event.GuildChatEvent;
import kr.entree.spicord.bukkit.structure.Message;
import kr.entree.spicord.bukkit.util.Defaults;
import kr.entree.spicord.bukkit.util.Proxies;
import kr.entree.spicord.bukkit.util.Strings;
import kr.entree.spicord.config.DiscordCommand;
import kr.entree.spicord.config.Parameter;
import kr.entree.spicord.config.SpicordConfig;
import kr.entree.spicord.discord.task.channel.ChannelTask;
import kr.entree.spicord.discord.task.channel.handler.PlainMessage;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.inject.Inject;

/**
 * Created by JunHyung Lim on 2020-04-02
 */
public class DiscordCommandProcessor implements Listener {
    private final SpicordConfig config;

    @Inject
    public DiscordCommandProcessor(SpicordConfig config) {
        this.config = config;
    }

    @EventHandler
    public void onChat(GuildChatEvent e) {
        val commands = config.getCommandConfig().get();
        if (commands.getPlayerListCommand().check(e.getMessage())) {
            executePlayerList(e.getMessage(), commands.getPlayerListCommand());
        } else if (commands.getExecuteCommand().check(e.getMessage())) {
            executeExecuter(e.getMessage(), commands.getPlayerListCommand());
        }
    }

    private static Parameter createParameter(Message message) {
        return new Parameter().put("%name%", message.getAuthor().getName())
                .putPlayerList()
                .putServer();
    }

    private void executePlayerList(Message message, DiscordCommand command) {
        val parameter = createParameter(message);
        val handler = config.getMessage(command.getMessageId(), parameter);
        Spicord.discord().addTask(ChannelTask.ofText(message.getChannelId(), handler));
    }

    private void executeExecuter(Message message, DiscordCommand command) {
        val parameter = createParameter(message);
        val builder = new StringBuilder();
        val sender = Proxies.create(CommandSender.class, (proxy, method, args) -> {
            if ("sendMessage".equals(method.getName())) {
                builder.append(ChatColor.stripColor(args[0].toString()));
            }
            return Defaults.defaultValue(method.getReturnType());
        });
        Bukkit.dispatchCommand(sender, parameter.format(Strings.substringAfter(message.getContents(), " ")));
        Spicord.discord().addTask(ChannelTask.ofText(message.getChannelId(), new PlainMessage(builder)));
    }
}
