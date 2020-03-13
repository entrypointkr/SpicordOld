package kr.entree.spicord.di.component;

import dagger.Component;
import kr.entree.spicord.Spicord;
import kr.entree.spicord.bukkit.bootstrap.*;
import kr.entree.spicord.bukkit.messenger.TextMessenger;
import kr.entree.spicord.bukkit.messenger.WebhookMessenger;
import kr.entree.spicord.di.module.SpicordModule;

import javax.inject.Named;
import java.time.Duration;

/**
 * Created by JunHyung Lim on 2020-03-04
 */
@Component(modules = SpicordModule.class)
public interface SpicordComponent {
    void inject(Spicord spicord);

    TextMessenger textMessenger();

    WebhookMessenger webhookMessenger();

    ChatToDiscord chatToDiscord();

    DiscordToBukkit discordToBukkit();

    DiscordToDiscord discordToDiscord();

    BukkitToDiscord bukkitToDiscord();

    PlayerVerifier playerVerifier();

    PlayerRestricter playerRestricter();

    RichPresenceUpdater richPresenceUpdater();

    @Named("flushPeriod")
    Duration flushPeriod();
}
