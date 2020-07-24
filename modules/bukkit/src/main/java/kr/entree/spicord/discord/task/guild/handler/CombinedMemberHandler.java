package kr.entree.spicord.discord.task.guild.handler;

import lombok.val;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Created by JunHyung Lim on 2019-12-29
 */
public class CombinedMemberHandler implements BiConsumer<Guild, Member> {
    private final List<BiConsumer<Guild, Member>> handlers;


    public CombinedMemberHandler(List<BiConsumer<Guild, Member>> handlers) {
        this.handlers = handlers;
    }

    public CombinedMemberHandler() {
        this(new ArrayList<>());
    }

    public CombinedMemberHandler add(BiConsumer<Guild, Member> handler) {
        this.handlers.add(handler);
        return this;
    }

    @Override
    public void accept(Guild guild, Member member) {
        for (val handler : handlers) {
            handler.accept(guild, member);
        }
    }
}
