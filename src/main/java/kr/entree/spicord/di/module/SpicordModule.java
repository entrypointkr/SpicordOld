package kr.entree.spicord.di.module;

import dagger.Module;
import dagger.Provides;
import kr.entree.spicord.Spicord;
import kr.entree.spicord.bukkit.util.Cooldown;
import kr.entree.spicord.bukkit.util.FixedCooldown;
import kr.entree.spicord.di.qualifier.MessengerCooldown;
import kr.entree.spicord.discord.Discord;
import org.bukkit.plugin.Plugin;

import javax.inject.Named;
import java.time.Duration;

/**
 * Created by JunHyung Lim on 2020-03-04
 */
@Module(includes = MessengerModule.class)
public class SpicordModule {
    private final Spicord spicord;
    private final Duration flushPeriod;

    public SpicordModule(Spicord spicord, Duration flushPeriod) {
        this.spicord = spicord;
        this.flushPeriod = flushPeriod;
    }

    @Provides
    public Spicord provideSpicord() {
        return spicord;
    }

    @Provides
    public Plugin providePlugin() {
        return spicord;
    }

    @Provides
    @Named("flushPeriod")
    public Duration provideFlushPeriod() {
        return flushPeriod;
    }

    @Provides
    @MessengerCooldown
    public FixedCooldown provideFlushCooldown() {
        return new FixedCooldown(provideFlushPeriod(), new Cooldown());
    }

    @Provides
    @Named("spicordThread")
    public Thread provideSpicordThread(Discord discord) {
        return new Thread(discord, "SpicordThread");
    }
}
