package kr.entree.spicord.discord.supplier;

import kr.entree.spicord.config.SpicordConfig;
import kr.entree.spicord.discord.ChannelSupplier;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.configuration.ConfigurationSection;

import java.util.function.Consumer;

/**
 * Created by JunHyung Lim on 2019-11-24
 */
public class TextChannelSupplier implements ChannelSupplier<TextChannel> {
    private final String channelid;

    private TextChannelSupplier(String channelid) {
        this.channelid = channelid;
    }

    @Override
    public void get(JDA jda, Consumer<TextChannel> consumer) {
        consumer.accept(jda.getTextChannelById(channelid));
    }

    public static TextChannelSupplier of(String channelid) {
        return new TextChannelSupplier(channelid);
    }

    public static TextChannelSupplier ofConfigurized(SpicordConfig config, String channelId) {
        ConfigurationSection channelSection = config.getChannelSection();
        if (channelSection != null) {
            String channel = channelSection.getString(channelId);
            if (channel != null) {
                channelId = channel;
            }
        }
        return of(channelId);
    }
}
