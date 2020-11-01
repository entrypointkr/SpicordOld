package kr.entree.spicord.bukkit.messenger;

import dagger.Reusable;
import kr.entree.spicord.bukkit.util.Chat;
import kr.entree.spicord.bukkit.util.FixedCooldown;
import kr.entree.spicord.config.SpicordConfig;
import kr.entree.spicord.di.qualifier.MessengerCooldown;
import kr.entree.spicord.discord.Discord;
import kr.entree.spicord.discord.task.channel.handler.PlainMessage;
import lombok.val;

import javax.inject.Inject;

import static org.bukkit.ChatColor.stripColor;

/**
 * Created by JunHyung Lim on 2020-03-02
 */
@Reusable
public class TextMessenger implements Messenger, Runnable {
    private final FixedCooldown cooldown;
    private final Discord discord;
    private final SpicordConfig config;
    private final StringBuilder builder = new StringBuilder();

    @Inject
    public TextMessenger(@MessengerCooldown FixedCooldown cooldown, Discord discord, SpicordConfig config) {
        this.cooldown = cooldown;
        this.discord = discord;
        this.config = config;
    }

    @Override
    public void chat(Chat chat) {
        val message = stripColor(chat.formatMessage(config));
        if (message == null) return;
        appendChat(message);
        flushChatIfTime();
    }

    @Override
    public void run() {
        flushChatIfTime();
    }

    private void flushChatIfTime() {
        if (cooldown.actions()) {
            flushChat();
        }
    }

    private void flushChat() {
        if (builder.length() == 0) return;
        sendChat(builder.toString());
        builder.setLength(0);
    }

    private void appendChat(String message) {
        if (builder.length() > 0) {
            builder.append('\n');
        }
        builder.append(message);
    }

    public void sendChat(String message) {
        discord.addTask(config.getFeature("player-chat", new PlainMessage(message)));
    }
}
