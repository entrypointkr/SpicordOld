package kr.entree.spicord.bukkit.bootstrap;

import kr.entree.spicord.Spicord;
import kr.entree.spicord.bukkit.event.GuildChatEvent;
import kr.entree.spicord.bukkit.structure.Message;
import kr.entree.spicord.config.DiscordCommand;
import kr.entree.spicord.config.Parameter;
import kr.entree.spicord.config.SpicordConfig;
import kr.entree.spicord.discord.task.channel.ChannelTask;
import lombok.val;
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
        }
    }

    private void executePlayerList(Message message, DiscordCommand command) {
        val parameter = new Parameter().put("%name%", message.getAuthor().getName())
                .putPlayerList()
                .putServer();
        val handler = config.getMessage(command.getMessageId(), parameter);
        Spicord.discord().addTask(ChannelTask.ofText(message.getChannelId(), handler));
    }
}
