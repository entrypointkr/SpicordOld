package kr.entree.spicord.bukkit.messenger;

import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import dagger.Reusable;
import kr.entree.spicord.Spicord;
import kr.entree.spicord.bukkit.discord.WebMessage;
import kr.entree.spicord.bukkit.util.Chat;
import kr.entree.spicord.bukkit.util.FixedCooldown;
import kr.entree.spicord.bukkit.util.PlayerData;
import kr.entree.spicord.config.DataStorage;
import kr.entree.spicord.config.SpicordConfig;
import kr.entree.spicord.di.qualifier.MessengerCooldown;
import kr.entree.spicord.discord.Discord;
import kr.entree.spicord.discord.WebhookManager;
import lombok.val;

import javax.inject.Inject;
import java.util.logging.Level;

/**
 * Created by JunHyung Lim on 2020-03-03
 */
@Reusable
public class WebhookMessenger implements Messenger, Runnable {
    private final FixedCooldown cooldown;
    private final WebhookManager webhookManager;
    private final DataStorage dataStorage;
    private final TextMessenger textMessenger;
    private final Discord discord;
    private final SpicordConfig config;

    private final StringBuilder messageBuilder = new StringBuilder();
    private PlayerData author = null;

    @Inject
    public WebhookMessenger(@MessengerCooldown FixedCooldown cooldown, WebhookManager webhookManager, DataStorage dataStorage, TextMessenger textMessenger, Discord discord, SpicordConfig config) {
        this.cooldown = cooldown;
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
        if (cooldown.actions()) {
            flushChat();
        }
    }

    private void flushChat() {
        sendWebhookMessage();
        reset();
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

    public static String createAvatarUrl(Object uuid) {
        return String.format("https://crafatar.com/avatars/%s?overlay=true", uuid);
    }
}
