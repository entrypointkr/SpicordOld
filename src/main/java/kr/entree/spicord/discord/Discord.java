package kr.entree.spicord.discord;

import kr.entree.spicord.bukkit.DiscordEventToBukkit;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.bukkit.plugin.Plugin;

import javax.security.auth.login.LoginException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;

/**
 * Created by JunHyung Lim on 2019-11-16
 */
public class Discord implements Runnable {
    private final Plugin plugin;
    private final BlockingQueue<JDAHandler> consumers = new LinkedBlockingQueue<>();
    @Getter
    private JDA jda = null;
    @Getter
    @Setter
    private String token = null;

    public Discord(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                preProcess();
                takeAndNotifyConsumer();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        shutdownJDA();
    }

    public void addTask(JDAHandler consumer) {
        consumers.add(consumer);
    }

    private void shutdownJDA() {
        if (jda != null) {
            jda.shutdownNow();
        }
    }

    private void preProcess() throws InterruptedException {
        if (token != null && (jda == null || !jda.getToken().contains(token))) {
            shutdownJDA();
            JDA newJda;
            try {
                newJda = new JDABuilder(token)
                        .addEventListeners(new DiscordEventToBukkit(plugin, this))
                        .build();
                newJda.awaitReady();
            } catch (LoginException e) {
                plugin.getLogger().log(Level.WARNING, String.format("Error while starting discord bot with token: \"%s\"", token), e);
                token = null;
                jda = null;
                return;
            }
            jda = newJda;
        }
    }

    private void takeAndNotifyConsumer() throws InterruptedException {
        if (jda == null) {
            Thread.sleep(1000);
            return;
        }
        val consumer = consumers.take();
        try {
            consumer.handle(jda);
        } catch (Exception ex) {
            plugin.getLogger().log(Level.WARNING, ex, () -> "Exception");
        }
    }
}
