package kr.entree.spicord.command;

import kr.entree.spicord.config.DiscordCommand;
import lombok.Data;

@Data
public class IdentifiedCommand {
    private final String id;
    private final DiscordCommand command;
}
