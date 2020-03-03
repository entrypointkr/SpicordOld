package kr.entree.spicord.bukkit.messenger;

import kr.entree.spicord.bukkit.util.Chat;
import kr.entree.spicord.config.SpicordConfig;
import kr.entree.spicord.discord.Discord;
import kr.entree.spicord.discord.task.channel.handler.PlainMessage;
import lombok.val;

import java.time.Duration;
import java.time.LocalTime;

/**
 * Created by JunHyung Lim on 2020-03-02
 */
public class TextMessenger implements Messenger, Runnable {
    private final Duration threshold;
    private final Discord discord;
    private final SpicordConfig config;
    private LocalTime lastFlushedTime = LocalTime.MIN;
    private final StringBuilder builder = new StringBuilder();

    public TextMessenger(Duration threshold, Discord discord, SpicordConfig config) {
        this.threshold = threshold;
        this.discord = discord;
        this.config = config;
    }

    @Override
    public void chat(Chat chat) {
        val message = chat.formatMessage(config);
        if (message == null) return;
        appendChat(message);
        flushChatIfTime();
    }

    @Override
    public void run() {
        flushChatIfTime();
    }

    private void flushChatIfTime() {
        if (checkFlushTime()) {
            flushChat();
        }
    }

    private void flushChat() {
        if (builder.length() == 0) return;
        sendChat(builder.toString());
        builder.setLength(0);
        lastFlushedTime = LocalTime.now();
    }

    private boolean checkFlushTime() {
        return Duration.between(lastFlushedTime, LocalTime.now()).compareTo(threshold) > 0;
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
