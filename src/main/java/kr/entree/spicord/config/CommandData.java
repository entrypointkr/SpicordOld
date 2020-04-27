package kr.entree.spicord.config;

import kr.entree.spicord.bukkit.structure.Message;
import kr.entree.spicord.bukkit.util.ConfigurationSections;
import kr.entree.spicord.command.IdentifiedCommand;
import lombok.Data;
import lombok.Getter;
import lombok.val;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

import static java.util.Collections.emptyList;

/**
 * Created by JunHyung Lim on 2020-04-02
 */
@Data
public class CommandData {
    @Getter private final Map<String, IdentifiedCommand> commandById;
    @Getter private final Map<String, List<IdentifiedCommand>> commandsByPrefix;

    public static CommandData parse(ConfigurationSection section, SpicordConfig config) {
        val commandById = new HashMap<String, IdentifiedCommand>();
        val commandsByPrefix = new HashMap<String, List<IdentifiedCommand>>();
        for (String id : section.getKeys(false)) {
            val lowerCasedId = id.toLowerCase();
            ConfigurationSections.getSection(section, id)
                    .map(childSection -> new IdentifiedCommand(lowerCasedId, DiscordCommand.parse(childSection, config)))
                    .peek(command -> {
                        commandById.put(lowerCasedId, command);
                        for (String literal : command.getCommand().getLiterals()) {
                            val commands = commandsByPrefix.computeIfAbsent(literal.substring(0, 1), k -> new ArrayList<>());
                            commands.add(command);
                        }
                    });
        }
        return new CommandData(commandById, commandsByPrefix);
    }

    public Optional<IdentifiedCommand> findCommand(String literal) {
        if (literal.isEmpty()) return Optional.empty();
        val commands = commandsByPrefix.getOrDefault(literal.substring(0, 1), emptyList());
        return commands.stream().filter(cmd -> cmd.getCommand().match(literal)).findFirst();
    }

    public Optional<IdentifiedCommand> findCommand(Message message) {
        return findCommand(message.getContents());
    }
}
