package kr.entree.spicord.bukkit.messenger;

import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import kr.entree.spicord.Spicord;
import kr.entree.spicord.bukkit.discord.WebMessage;
import kr.entree.spicord.bukkit.util.Chat;
import kr.entree.spicord.bukkit.util.PlayerData;
import kr.entree.spicord.config.DataStorage;
import kr.entree.spicord.config.SpicordConfig;
import kr.entree.spicord.discord.Discord;
import kr.entree.spicord.discord.WebhookManager;
import lombok.val;

import java.time.Duration;
import java.time.LocalTime;
import java.util.logging.Level;

/**
 * Created by JunHyung Lim on 2020-03-03
 */
public class WebhookMessenger implements Messenger, Runnable {
    private final Duration threshold;
    private final WebhookManager webhookManager;
    private final DataStorage dataStorage;
    private final TextMessenger textMessenger;
    private final Discord discord;
    private final SpicordConfig config;
    private LocalTime lastFlushedTime = LocalTime.MIN;

    private final StringBuilder messageBuilder = new StringBuilder();
    private PlayerData author = null;

    public WebhookMessenger(Duration threshold, WebhookManager webhookManager, DataStorage dataStorage, TextMessenger textMessenger, Discord discord, SpicordConfig config) {
        this.threshold = threshold;
        this.webhookManager = webhookManager;
        this.dataStorage = dataStorage;
        this.textMessenger = textMessenger;
        this.discord = discord;
        this.config = config;
    }

    @Override
    public void chat(Chat chat) {
        appendChat(chat);
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
        sendWebhookMessage();
        reset();
        lastFlushedTime = LocalTime.now();
    }

    private void sendWebhookMessage() {
        if (author == null || messageBuilder.length() == 0) return;
        val message = messageBuilder.toString();
        val builder = new WebhookMessageBuilder()
                .setUsername(author.getDisplayName())
                .setAvatarUrl(createAvatarUrl(author.getId()))
                .setContent(message);
        val sendMessage = new WebMessage(
                webhookManager,
                builder.build(),
                dataStorage.getPlayerChatWebhookId(),
                throwable -> failedWebhook(throwable, new PlayerData(author), message)
        );
        discord.addTask(config.getFeature("player-chat", sendMessage));
    }

    private void reset() {
        messageBuilder.setLength(0);
        author = null;
    }

    private void failedWebhook(Throwable throwable, PlayerData player, String message) {
        Spicord.logger().log(Level.SEVERE, throwable, () ->
                "Failed creating a webhook. This feature will be disabled.");
        config.getFakeProfilePlayerChat().set(false);
        if (author == null) return;
        textMessenger.sendChat(new Chat(player, message).formatMessage(config));
    }

    private void appendChat(Chat chat) {
        if (author == null) {
            author = chat.getPlayerData();
        }
        if (author.equals(chat.getPlayerData())) {
            appendMessage(chat.getMessage());
        } else {
            val message = chat.formatMessage(config);
            if (message == null) return;
            appendMessage(message);
        }
    }

    private void appendMessage(String message) {
        if (messageBuilder.length() > 0) {
            messageBuilder.append('\n');
        }
        messageBuilder.append(message);
    }

    private boolean checkFlushTime() {
        return Duration.between(lastFlushedTime, LocalTime.now()).compareTo(threshold) > 0;
    }

    public static String createAvatarUrl(Object uuid) {
        return String.format("https://crafatar.com/avatars/%s?overlay=true", uuid);
    }
}
