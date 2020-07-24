package kr.entree.spicord.discord.task.guild.handler;


import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Created by JunHyung Lim on 2019-12-01
 */
public class AddRole implements BiConsumer<Guild, Member> {
    private final Function<Guild, Collection<Role>> roleGetter;

    public AddRole(Function<Guild, Collection<Role>> roleGetter) {
        this.roleGetter = roleGetter;
    }

    @Override
    public void accept(Guild guild, Member member) {
        for (Role role : roleGetter.apply(guild)) {
            guild.addRoleToMember(member, role).queue();
        }
    }
}
