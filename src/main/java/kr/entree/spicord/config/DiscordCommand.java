package kr.entree.spicord.config;

import kr.entree.spicord.bukkit.structure.Message;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Created by JunHyung Lim on 2020-04-02
 */
@Data
public class DiscordCommand {
    private final Collection<String> commands;
    private final String channelId;
    private final String messageId;
    @Getter
    @Setter
    private SpicordConfig config;

    public DiscordCommand(Collection<String> commands, String channelId, String messageId) {
        this.commands = commands;
        this.channelId = channelId;
        this.messageId = messageId;
    }

    public static DiscordCommand parse(ConfigurationSection section) {
        val command = section.get("command");
        val channel = section.getString("channel", "");
        val message = section.getString("message", "");
        val commands = new ArrayList<String>();
        if (command instanceof Collection) {
            val collection = ((Collection<?>) command);
            val strings = collection.stream()
                    .map(Object::toString)
                    .collect(Collectors.toList());
            commands.addAll(strings);
        } else if (command != null) {
            commands.add(command.toString());
        }
        return new DiscordCommand(commands, channel, message);
    }

    public static DiscordCommand parse(ConfigurationSection section, SpicordConfig topConfig) {
        val parsed = parse(section);
        parsed.setConfig(topConfig);
        return parsed;
    }

    public boolean isValidChannel(String channelId) {
        return config.remapChannel(this.channelId).equals(channelId);
    }

    public boolean check(Message message) {
        val contents = message.getContents();
        return !commands.isEmpty()
                && isValidChannel(message.getChannelId())
                && commands.stream().anyMatch(contents::startsWith);
    }
}
