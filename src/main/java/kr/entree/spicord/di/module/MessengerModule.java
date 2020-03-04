package kr.entree.spicord.di.module;

import dagger.Binds;
import dagger.Module;
import dagger.Reusable;
import kr.entree.spicord.bukkit.messenger.Messenger;
import kr.entree.spicord.bukkit.messenger.TextMessenger;
import kr.entree.spicord.bukkit.messenger.WebhookMessenger;

import javax.inject.Named;

/**
 * Created by JunHyung Lim on 2020-03-04
 */
@Module
public abstract class MessengerModule {
    @Binds
    @Named("textMessenger")
    @Reusable
    public abstract Messenger textMessenger(TextMessenger messenger);

    @Binds
    @Named("webhookMessenger")
    @Reusable
    public abstract Messenger webhookMessenger(WebhookMessenger messenger);
}
