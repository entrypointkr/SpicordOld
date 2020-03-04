package kr.entree.spicord;

import kr.entree.spicord.bukkit.SpicordCommand;
import kr.entree.spicord.bukkit.VerifiedMemberManager;
import kr.entree.spicord.bukkit.util.Compatibles;
import kr.entree.spicord.config.DataStorage;
import kr.entree.spicord.config.LangConfig;
import kr.entree.spicord.config.Parameter;
import kr.entree.spicord.config.SpicordConfig;
import kr.entree.spicord.di.component.DaggerSpicordComponent;
import kr.entree.spicord.di.component.SpicordComponent;
import kr.entree.spicord.di.module.SpicordModule;
import kr.entree.spicord.discord.Discord;
import kr.entree.spicord.discord.WebhookManager;
import kr.entree.spicord.discord.task.CompleterBuilder;
import lombok.Getter;
import lombok.val;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import javax.inject.Inject;
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
    @Inject @Getter SpicordConfig spicordConfig;
    @Inject @Getter LangConfig langConfig;
    @Inject @Getter DataStorage dataStorage;
    @Inject @Getter VerifiedMemberManager verifyManager;
    @Inject @Getter Discord discord;
    @Inject @Getter WebhookManager webhookManager;
    Thread discordThread;
    @Getter private SpicordComponent component;

    @Override
    public void onLoad() {
        component = DaggerSpicordComponent.builder()
                .spicordModule(new SpicordModule(this, Duration.ofSeconds(2)))
                .build();
        component.inject(this);
        discordThread = new Thread(discord, "SpicordThread");
    }

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
        verifyManager.save(this);
        langConfig.save();
        dataStorage.save();
    }

    private void initConfigs() {
        loadConfigs();
        verifyManager.load(this);
    }

    private void initFunctions() {
        val flushPeriodTicks = component.flushPeriod().toMillis() / 50;
        val textMessenger = component.textMessenger();
        val webhookMessenger = component.webhookMessenger();
        registerEvents(
                component.chatToDiscord(),
                component.discordToBukkit(),
                component.discordToDiscord(),
                component.bukkitToDiscord(),
                component.playerVerifier(),
                component.playerRestricter()
        );
        runTaskTimer(flushPeriodTicks, textMessenger);
        runTaskTimer(flushPeriodTicks, webhookMessenger);
        discordThread.setContextClassLoader(getClassLoader());
        discordThread.start();
        discord.addTask(spicordConfig.getServerOnMessage());
        if (spicordConfig.getVerification().isDeleteIfQuitFromGuild()) {
            verifyManager.queuePurgeTask(discord, spicordConfig.getGuild());
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

    public static Discord discord() {
        return get().discord;
    }

    public static SpicordConfig spicordConfig() {
        return get().spicordConfig;
    }

    public static Logger logger() {
        return get().getLogger();
    }

    public static void log(Throwable ex) {
        logger().log(Level.WARNING, "Exception!", ex);
    }
}