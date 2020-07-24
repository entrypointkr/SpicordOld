package kr.entree.spicord.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.function.Consumer;

/**
 * Created by JunHyung Lim on 2019-11-24
 */
public interface ChannelSupplier {
    void get(JDA jda, Consumer<MessageChannel> consumer);
}
