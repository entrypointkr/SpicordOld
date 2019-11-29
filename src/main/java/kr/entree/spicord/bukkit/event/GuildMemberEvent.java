package kr.entree.spicord.bukkit.event;

import kr.entree.spicord.bukkit.structure.Guild;
import kr.entree.spicord.bukkit.structure.Member;
import kr.entree.spicord.bukkit.structure.MemberProvider;
import kr.entree.spicord.discord.Discord;

/**
 * Created by JunHyung Lim on 2019-11-29
 */
public abstract class GuildMemberEvent extends GuildEvent implements MemberProvider {
    private final Member member;

    public GuildMemberEvent(Discord discord, Guild guild, Member member) {
        super(discord, guild);
        this.member = member;
    }

    @Override
    public Member getMember() {
        return member;
    }
}
