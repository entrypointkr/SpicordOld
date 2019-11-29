package kr.entree.spicord.bukkit.structure;

import lombok.Getter;
import net.dv8tion.jda.api.utils.cache.MemberCacheView;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by JunHyung Lim on 2019-11-29
 */
public class Guild {
    @Getter
    private final long id;
    @Getter
    private final String name;
    @Getter
    private final String description;
    @Getter
    private final int boostCount;
    @Getter
    private final int maxMembers;
    private final MemberCacheView members;
    private final long owner;

    public Guild(long id, String name, String description, int boostCount, int maxMembers, MemberCacheView members, long owner) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.boostCount = boostCount;
        this.maxMembers = maxMembers;
        this.members = members;
        this.owner = owner;
    }

    public static Guild of(long id, String name, String description, int boostCount,
                           int maxMembers, MemberCacheView members, long owner) {
        return new Guild(id, name, description, boostCount, maxMembers, members, owner);
    }

    public static Guild of(net.dv8tion.jda.api.entities.Guild guild) {
        return of(guild.getIdLong(), guild.getName(), guild.getDescription(), guild.getBoostCount(),
                guild.getMaxMembers(), guild.getMemberCache(), guild.getOwnerIdLong());
    }

    public List<Member> getMembers() {
        return members.asList().stream()
                .map(Member::of)
                .collect(Collectors.toList());
    }

    public Optional<Member> getOwner() {
        return Optional.ofNullable(members.getElementById(owner))
                .map(Member::of);
    }

    public List<Member> getBoosters() {
        return getMembers().stream()
                .filter(member -> member.getTimeBoosted() != null)
                .sorted(Comparator.comparing(Member::getTimeBoosted))
                .collect(Collectors.toList());
    }
}
