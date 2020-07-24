package kr.entree.spicord.bukkit.util;

import kr.entree.spicord.config.Parameters;
import kr.entree.spicord.config.SpicordConfig;
import kr.entree.spicord.discord.task.channel.handler.PlainMessage;
import kr.entree.spicord.util.Parameter;
import lombok.Data;
import lombok.val;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by JunHyung Lim on 2020-03-02
 */
@Data
public class Chat {
    private final PlayerData playerData;
    private final String message;
    private boolean prefix = true;

    public Chat(PlayerData playerData, String message) {
        this.playerData = playerData;
        this.message = message;
    }

    public Chat prefix(boolean prefix) {
        this.prefix = prefix;
        return this;
    }

    public static Chat create(Player player, String message) {
        val uncolored = ChatColor.stripColor(message);
        val data = new PlayerData(player);
        data.displayName(ChatColor.stripColor(data.getDisplayName()));
        data.name(ChatColor.stripColor(data.getName()));
        return new Chat(data, uncolored);
    }

    public String formatMessage(SpicordConfig config) {
        if (!prefix) {
            return message;
        }
        val parameter = Parameters.putPlayerData(new Parameter(), playerData).put("%message%", message);
        PlainMessage plainMessage = config.getPlainMessage("player-chat", parameter);
        return plainMessage != null ? plainMessage.getMessage().toString() : null;
    }
}
