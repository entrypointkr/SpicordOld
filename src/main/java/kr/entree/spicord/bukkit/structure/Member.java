package kr.entree.spicord.bukkit.structure;

import lombok.Data;
import lombok.val;
import net.dv8tion.jda.api.Permission;
import org.jetbrains.annotations.Nullable;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by JunHyung Lim on 2019-11-29
 */
@Data
public class Member {
    private final long id;
    private final Guild guild;
    private final String nick;
    private final OffsetDateTime timeJoined;
    private final OffsetDateTime timeBoosted;
    private final Map<String, Role> roles;
    private final User user;
    private final Set<Integer> permissions;

    public static Member of(long id, Guild guild, @Nullable String nick, OffsetDateTime timeJoined,
                            OffsetDateTime timeBoosted, Map<String, Role> roles, User user, Set<Permission> permissions) {
        val permissionIds = permissions.stream().map(Permission::getOffset).collect(Collectors.toSet());
        return new Member(id, guild, nick, timeJoined, timeBoosted, roles, user, permissionIds);
    }

    public static Member of(net.dv8tion.jda.api.entities.Member member) {
        return of(
                member.getIdLong(),
                Guild.of(member.getGuild()),
                member.getNickname(),
                member.getTimeJoined(),
                member.getTimeBoosted(),
                member.getRoles().stream()
                        .map(Role::of)
                        .collect(Collectors.toMap(Role::getName, role -> role)),
                User.of(member.getUser()),
                member.getPermissions()
        );
    }

    public String getName() {
        return nick != null ? nick : user.getName();
    }

    public boolean isAdmin() {
        return getPermissions().contains(Permission.ADMINISTRATOR.getOffset());
    }

    public boolean isOwner() {
        return guild.getOwner() == id;
    }
}
