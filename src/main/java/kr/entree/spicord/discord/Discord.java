package kr.entree.spicord.discord;

import kr.entree.spicord.bukkit.DiscordEventToBukkit;
import kr.entree.spicord.bukkit.UserVerifier;
import kr.entree.spicord.bukkit.VerifiedMemberManager;
import kr.entree.spicord.config.LangConfig;
import kr.entree.spicord.config.SpicordConfig;
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
    private final SpicordConfig config;
    private final LangConfig langConfig;
    private final VerifiedMemberManager verifiedManager;
    private final BlockingQueue<JDAHandler> consumers = new LinkedBlockingQueue<>();
    private JDA jda = null;
    @Getter
    @Setter
    private String token = null;

    public Discord(Plugin plugin, SpicordConfig config, LangConfig langConfig, VerifiedMemberManager verifiedManager) {
        this.plugin = plugin;
        this.config = config;
        this.langConfig = langConfig;
        this.verifiedManager = verifiedManager;
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
            try {
                jda = new JDABuilder(token)
                        .addEventListeners(
                                new DiscordEventToBukkit(plugin),
                                new UserVerifier(plugin, this, config, langConfig, verifiedManager)
                        )
                        .build();
                jda.awaitReady();
            } catch (LoginException e) {
                plugin.getLogger().log(Level.WARNING, String.format("Error while starting discord bot with token: \"%s\"", token), e);
                token = null;
            }
        }
    }

    private void takeAndNotifyConsumer() throws InterruptedException {
        if (jda == null) {
            Thread.sleep(500);
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
