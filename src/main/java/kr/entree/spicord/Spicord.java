package kr.entree.spicord;

import kr.entree.spicord.bukkit.MinecraftToDiscord;
import kr.entree.spicord.bukkit.SpicordCommand;
import kr.entree.spicord.bukkit.UserRestricter;
import kr.entree.spicord.bukkit.VerifiedMemberManager;
import kr.entree.spicord.bukkit.util.Compatibles;
import kr.entree.spicord.config.LangConfig;
import kr.entree.spicord.config.Parameter;
import kr.entree.spicord.config.SpicordConfig;
import kr.entree.spicord.discord.Discord;
import kr.entree.spicord.discord.WebhookManager;
import kr.entree.spicord.discord.task.CompleterBuilder;
import lombok.Getter;
import lombok.val;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by JunHyung Lim on 2019-11-16
 */
public class Spicord extends JavaPlugin {
    @Getter
    private final SpicordConfig spicordConfig = new SpicordConfig(this);
    @Getter
    private final LangConfig langConfig = new LangConfig(this);
    @Getter
    private final VerifiedMemberManager verifiedManager = new VerifiedMemberManager(this);
    @Getter
    private final Discord discord = new Discord(this, spicordConfig, langConfig, verifiedManager);
    @Getter
    private final WebhookManager webhookManager = new WebhookManager(this);
    private final Thread discordThread = new Thread(discord, "SpicordThread");

    @Override
    public void onEnable() {
        saveDefaultConfig();
        initConfigs();
        initCommands();
        initFunctions();
        initMetrics();
    }

    @Override
    public void onDisable() {
        getLogger().info("Waiting discord thread...");
        awaitDiscordThread();
        interruptDiscordThread();
        getLogger().info("Waiting webhooks...");
        if (!webhookManager.await()) {
            getLogger().info("Timeout");
        }
        saveConfigs();
    }

    public void loadConfigs() {
        spicordConfig.load();
        spicordConfig.update(discord);
        langConfig.load();
    }

    public void saveConfigs() {
        verifiedManager.save(this);
        langConfig.save();
    }

    private void initConfigs() {
        loadConfigs();
        verifiedManager.load(this);
        val period = 15L * 60L * 20L;
        Bukkit.getScheduler().runTaskTimer(this, verifiedManager::saveAsync, period, period);
    }

    private void initFunctions() {
        registerEvents(
                new MinecraftToDiscord(discord, spicordConfig, verifiedManager, webhookManager),
                new UserRestricter(verifiedManager, spicordConfig, langConfig)
        );
        discordThread.start();
        discord.addTask(spicordConfig.getServerOnMessage());
    }

    private void initCommands() {
        val pluginCommand = Objects.requireNonNull(getCommand("spicord"));
        val spicordCommand = new SpicordCommand(this, discord);
        pluginCommand.setExecutor(spicordCommand);
        pluginCommand.setTabCompleter(spicordCommand);
    }

    private void initMetrics() {
        new Metrics(this);
    }

    private void registerEvents(Listener... listeners) {
        for (Listener listener : listeners) {
            Bukkit.getPluginManager().registerEvents(listener, this);
        }
    }

    private void awaitDiscordThread() {
        val latch = new CountDownLatch(1);
        discord.addTask(new CompleterBuilder(spicordConfig.getServerOffMessage(
                Parameter.of().put("%players%", Compatibles.getOnlinePlayers().size())
        )).latch(latch).build());
        try {
            latch.await(15, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void interruptDiscordThread() {
        discordThread.interrupt();
        try {
            discordThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static Spicord get() {
        return (Spicord) Bukkit.getPluginManager().getPlugin("Spicord");
    }

    public static Logger logger() {
        return get().getLogger();
    }

    public static void log(Exception ex) {
        logger().log(Level.WARNING, "Exception!", ex);
    }
}
