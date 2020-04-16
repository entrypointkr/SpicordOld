package kr.entree.spicord.command;

import kr.entree.spicord.bukkit.structure.Message;
import kr.entree.spicord.config.DiscordCommand;
import lombok.Data;
import lombok.val;

@Data
public class DiscordCommandContext {
    private final IdentifiedCommand command;
    private final Message message;
    private final String[] args;

    public static DiscordCommandContext parse(Message message, IdentifiedCommand command) {
        val input = message.getContents();
        val space = input.indexOf(' ');
        val args = (space >= 0 ? input.substring(space + 1) : input).split(" ");
        return new DiscordCommandContext(command, message, args);
    }

    public String getId() {
        return command.getId();
    }

    public DiscordCommand getDiscordCommand() {
        return command.getCommand();
    }
}
