package kr.entree.spicord;

import kr.entree.spicord.bukkit.SpicordCommand;
import kr.entree.spicord.bukkit.VerifiedMemberManager;
import kr.entree.spicord.bukkit.bootstrap.*;
import kr.entree.spicord.bukkit.messenger.TextMessenger;
import kr.entree.spicord.bukkit.messenger.WebhookMessenger;
import kr.entree.spicord.bukkit.util.Compatibles;
import kr.entree.spicord.config.DataStorage;
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

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by JunHyung Lim on 2019-11-16
 */
public class Spicord extends JavaPlugin {
    private static final int BSTATS_ID = 5977;
    private final SpicordConfig spicordConfig = new SpicordConfig(this);
    @Getter
    private final LangConfig langConfig = new LangConfig(this);
    @Getter
    private final DataStorage dataStorage = new DataStorage(this);
    @Getter
    private final VerifiedMemberManager verifiedManager = new VerifiedMemberManager(this);
    private final Discord discord = new Discord(this);
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
        stopDiscordThread();
        getLogger().info("Waiting webhooks...");
        if (!webhookManager.stop()) {
            getLogger().info("Timeout");
        }
        saveConfigs();
    }

    public void loadConfigs() {
        spicordConfig.load();
        spicordConfig.update(discord);
        langConfig.load();
        dataStorage.load();
    }

    public void saveConfigs() {
        verifiedManager.save(this);
        langConfig.save();
        dataStorage.save();
    }

    private void initConfigs() {
        loadConfigs();
        verifiedManager.load(this);
    }

    private void initFunctions() {
        val flushPeriod = Duration.ofSeconds(2);
        val flushPeriodTicks = flushPeriod.toMillis() / 50;
        val textMessenger = new TextMessenger(flushPeriod, discord, spicordConfig);
        val webhookMessenger = new WebhookMessenger(
                flushPeriod,
                webhookManager,
                dataStorage,
                textMessenger,
                discord,
                spicordConfig
        );
        registerEvents(
                new ChatToDiscord(this, spicordConfig, textMessenger, webhookMessenger),
                new DiscordToBukkit(this, spicordConfig),
                new DiscordToDiscord(spicordConfig, verifiedManager),
                new BukkitToDiscord(spicordConfig, discord, verifiedManager),
                new PlayerVerifier(this, spicordConfig, langConfig, verifiedManager),
                new PlayerRestricter(verifiedManager, spicordConfig, langConfig)
        );
        runTaskTimer(flushPeriodTicks, textMessenger);
        runTaskTimer(flushPeriodTicks, webhookMessenger);
        discordThread.setContextClassLoader(getClassLoader());
        discordThread.start();
        discord.addTask(spicordConfig.getServerOnMessage());
        if (spicordConfig.getVerification().isDeleteIfQuitFromGuild()) {
            verifiedManager.queuePurgeTask(discord, spicordConfig.getGuild());
        }
    }

    private void initCommands() {
        val pluginCommand = Objects.requireNonNull(getCommand("spicord"));
        val spicordCommand = new SpicordCommand(this, discord);
        pluginCommand.setExecutor(spicordCommand);
        pluginCommand.setTabCompleter(spicordCommand);
    }

    private void initMetrics() {
        new Metrics(this, BSTATS_ID);
    }

    private void registerEvents(Listener... listeners) {
        for (Listener listener : listeners) {
            Bukkit.getPluginManager().registerEvents(listener, this);
        }
    }

    private void awaitDiscordThread() {
        if (discord.getJda() == null) {
            return;
        }
        val latch = new CountDownLatch(1);
        discord.addTask(new CompleterBuilder(spicordConfig.getServerOffMessage(
                new Parameter().put("%players%", Compatibles.getOnlinePlayers().size())
        )).latch(latch).build());
        try {
            latch.await(15, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void stopDiscordThread() {
        discordThread.interrupt();
        try {
            discordThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void runTaskTimer(long periodTicks, Runnable runnable) {
        Bukkit.getScheduler().runTaskTimer(this, runnable, periodTicks, periodTicks);
    }

    public static Spicord get() {
        return (Spicord) Bukkit.getPluginManager().getPlugin("Spicord");
    }

    public static Discord getDiscord() {
        return get().discord;
    }

    public static SpicordConfig getSpicordConfig() {
        return get().spicordConfig;
    }

    public static Logger logger() {
        return get().getLogger();
    }

    public static void log(Throwable ex) {
        logger().log(Level.WARNING, "Exception!", ex);
    }
}