package kr.entree.spicord.bukkit.structure;

import lombok.Getter;
import lombok.val;
import net.dv8tion.jda.api.Permission;
import org.jetbrains.annotations.Nullable;

import java.time.OffsetDateTime;
import java.util.Set;
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
    private final Set<Role> roles;
    @Getter
    private final User user;
    @Getter
    private final Set<Integer> permissions;

    private Member(long id, Guild guild, @Nullable String nick, OffsetDateTime timeJoined,
                   OffsetDateTime timeBoosted, Set<Role> roles, User user, Set<Integer> permissions) {
        this.id = id;
        this.guild = guild;
        this.nick = nick;
        this.timeJoined = timeJoined;
        this.timeBoosted = timeBoosted;
        this.roles = roles;
        this.user = user;
        this.permissions = permissions;
    }

    public static Member of(long id, Guild guild, @Nullable String nick, OffsetDateTime timeJoined,
                            OffsetDateTime timeBoosted, Set<Role> roles, User user, Set<Permission> permissions) {
        val permissionIds = permissions.stream().map(Permission::getOffset).collect(Collectors.toSet());
        return new Member(id, guild, nick, timeJoined, timeBoosted, roles, user, permissionIds);
    }

    public static Member of(net.dv8tion.jda.api.entities.Member member) {
        net.dv8tion.jda.api.entities.User user = member.getUser();
        return of(
                member.getIdLong(),
                Guild.of(member.getGuild()),
                member.getNickname(),
                member.getTimeJoined(),
                member.getTimeBoosted(),
                member.getRoles().stream()
                        .map(Role::of)
                        .collect(Collectors.toSet()),
                User.of(user),
                member.getPermissions()
        );
    }

    public String getName() {
        return nick != null ? nick : user.getName();
    }

    public boolean isAdmin() {
        return getPermissions().contains(3);
    }
}
