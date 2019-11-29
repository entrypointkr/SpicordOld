package kr.entree.spicord.bukkit.structure;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by JunHyung Lim on 2019-11-29
 */
public class Member {
    @Getter
    private final long id;
    @Getter
    private final Guild guild;
    @Getter
    @Nullable
    private final String nick;
    @Getter
    private final OffsetDateTime timeJoined;
    @Getter
    private final OffsetDateTime timeBoosted;
    @Getter
    private final List<Role> roles;
    @Getter
    private final User user;

    private Member(long id, Guild guild, @Nullable String nick, OffsetDateTime timeJoined,
                   OffsetDateTime timeBoosted, List<Role> roles, User user) {
        this.id = id;
        this.guild = guild;
        this.nick = nick;
        this.timeJoined = timeJoined;
        this.timeBoosted = timeBoosted;
        this.roles = roles;
        this.user = user;
    }

    public static Member of(long id, Guild guild, @Nullable String nick, OffsetDateTime timeJoined,
                            OffsetDateTime timeBoosted, List<Role> roles, User user) {
        return new Member(id, guild, nick, timeJoined, timeBoosted, roles, user);
    }

    public static Member of(net.dv8tion.jda.api.entities.Member member) {
        net.dv8tion.jda.api.entities.User user = member.getUser();
        return new Member(
                member.getIdLong(),
                Guild.of(member.getGuild()),
                member.getNickname(),
                member.getTimeJoined(),
                member.getTimeBoosted(),
                member.getRoles().stream()
                        .map(Role::of)
                        .collect(Collectors.toList()),
                User.of(user)
        );
    }

    public String getName() {
        return nick != null ? nick : user.getName();
    }
}
