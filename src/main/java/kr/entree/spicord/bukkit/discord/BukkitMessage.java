package kr.entree.spicord.bukkit.discord;

import kr.entree.spicord.discord.WebhookManager;
import kr.entree.spicord.discord.handler.MessageChannelHandler;
import lombok.val;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.entity.Player;

/**
 * Created by JunHyung Lim on 2019-11-17
 */
public class BukkitMessage implements MessageChannelHandler<TextChannel> {
    private final WebhookManager manager;
    private final Player player;
    private final String message;

    public BukkitMessage(WebhookManager manager, Player player, String message) {
        this.manager = manager;
        this.player = player;
        this.message = message;
    }

    @Override
    public void handle(TextChannel channel) {
        val data = manager.compute(player, channel);
        val future = data.send(message);
        future.whenComplete((readonlyMessage, throwable) -> {
            if (throwable != null) {
                manager.remove(player);
                manager.compute(player, channel).send(message);
            }
        });
    }
}
