package kr.entree.spicord.discord.task.guild.handler;


import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

/**
 * Created by JunHyung Lim on 2019-12-01
 */
public class Rename implements BiConsumer<Guild, Member> {
    private final String newName;

    public Rename(@NotNull String newName) {
        this.newName = newName;
    }

    @Override
    public void accept(Guild guild, Member member) {
        if (member.hasPermission(Permission.ADMINISTRATOR)) {
            return;
        }
        if (!newName.equals(member.getNickname())) {
            member.modifyNickname(newName).queue();
        }
    }
}
