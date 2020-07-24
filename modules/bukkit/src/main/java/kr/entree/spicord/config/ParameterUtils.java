package kr.entree.spicord.config;

import kr.entree.spicord.bukkit.util.Chat;
import kr.entree.spicord.bukkit.util.Platform;
import kr.entree.spicord.bukkit.util.PlayerData;
import kr.entree.spicord.util.Parameter;
import lombok.val;
import org.bukkit.entity.Player;

import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by JunHyung Im on 2020-07-23
 */
public class ParameterUtils {

    public static Parameter putServer(Parameter parameter) {
        return parameter.put("%players%", Platform.getOnlinePlayers().size());
    }

    public static Parameter put(Parameter parameter, Chat chat) {
        ParameterUtils.put(parameter, chat.getPlayerData());
        parameter.put("%message%", chat.getMessage());
        return parameter;
    }

    public static Parameter put(Parameter parameter, PlayerData player) {
        parameter.put("%name%", player.getName());
        parameter.put("%display-name%", player.getDisplayNameOrDefault());
        parameter.put("%player%", player.getName());
        parameter.put("%uuid%", player.getId());
        return parameter;
    }

    public static Parameter putPlayer(Parameter parameter, Player player) {
        return put(parameter, new PlayerData(player));
    }

    public static Parameter putPlayerList(Parameter parameter) {
        return parameter.put("%player-list%", (Supplier<String>) () -> {
            val names = Platform.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .collect(Collectors.toList());
            return String.join(", ", names);
        });
    }

    private ParameterUtils() {
    }
}
