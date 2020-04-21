package kr.entree.spicord.bukkit.bootstrap;

import kr.entree.spicord.Spicord;
import kr.entree.spicord.bukkit.event.GuildChatEvent;
import kr.entree.spicord.bukkit.proxy.CommandSenderProxyHandler;
import kr.entree.spicord.bukkit.structure.Member;
import kr.entree.spicord.bukkit.structure.Message;
import kr.entree.spicord.command.DiscordCommandContext;
import kr.entree.spicord.config.Parameter;
import kr.entree.spicord.config.SpicordConfig;
import kr.entree.spicord.discord.task.channel.ChannelTask;
import kr.entree.spicord.util.Emojis;
import kr.entree.spicord.util.Result;
import lombok.val;
import org.bukkit.Bukkit;
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
        val message = e.getMessage();
        val commands = config.getCommandConfig().get();
        val matchedCommand = commands.findCommand(message).orElse(null);
        if (matchedCommand == null) return;
        if (matchedCommand.getCommand().match(message)) {
            execute(DiscordCommandContext.parse(message, matchedCommand));
        } else {
            ChannelTask.ofReaction(message, Emojis.NO_ENTRY_SIGN).queue();
        }
    }

    private void execute(DiscordCommandContext context) {
        switch (context.getId()) {
            case "players":
                executePlayerList(context);
                break;
            case "execute":
                executeBukkitCommand(context);
                break;
            case "sudo":
                executeSudoCommand(context);
                break;
        }
    }

    private void executePlayerList(DiscordCommandContext context) {
        val message = context.getMessage();
        val parameter = createParameter(message);
        val handler = config.getMessage(context.getDiscordCommand().getMessageId(), parameter);
        Spicord.discord().addTask(ChannelTask.ofText(message.getChannelId(), handler));
    }

    private void executeBukkitCommand(DiscordCommandContext context) {
        val message = context.getMessage();
        val parameter = createParameter(message);
        val output = new StringBuilder();
        val sender = CommandSenderProxyHandler.createProxy(
                output::append,
                () -> isOwner(context.getMessage())
        );
        execute(sender, parameter.format(String.join(" ", context.getArgs())))
                .onSuccess(bool -> {
                    val handler = config.getMessage(context.getDiscordCommand().getMessageId(), parameter.put("%output%", output.toString()));
                    ChannelTask.ofText(message.getChannelId(), handler).queue();
                })
                .onFailure(ex -> {
                    Spicord.log(ex);
                    ChannelTask.ofReaction(message.getChannelId(), message.getId(), Emojis.X).queue();
                });
    }

    private void executeSudoCommand(DiscordCommandContext context) {
        val message = context.getMessage();
        if (!isOwner(message) && context.getDiscordCommand().getPermissions().isEmpty()) {
            ChannelTask.ofReaction(context.getMessage(), Emojis.NO_ENTRY_SIGN).queue();
            return;
        }
        val parameter = createParameter(message);
        val output = new StringBuilder();
        val sender = CommandSenderProxyHandler.createProxy(output::append, () -> true);
        String cmdline = parameter.format(String.join(" ", context.getArgs()));
        execute(sender, cmdline)
                .onSuccess(bool -> {
                    val handler = config.getMessage(context.getDiscordCommand().getMessageId(), parameter.put("%output%", output.toString()));
                    Spicord.discord().addTask(ChannelTask.ofText(message.getChannelId(), handler));
                })
                .onFailure(ex -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmdline));
    }

    private static Result<Boolean> execute(CommandSender sender, String commandLine) {
        return Result.run(() -> Bukkit.dispatchCommand(sender, commandLine));
    }

    private static Parameter createParameter(Message message) {
        return new Parameter().put("%name%", message.getAuthor().getName())
                .putPlayerList()
                .putServer();
    }

    private static boolean isOwner(Message message) {
        return message.getMember().is(Member::isOwner);
    }
}
