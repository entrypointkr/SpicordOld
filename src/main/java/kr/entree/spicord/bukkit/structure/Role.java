package kr.entree.spicord.bukkit.structure;

import lombok.Getter;

import java.util.Objects;

/**
 * Created by JunHyung Lim on 2019-11-29
 */
public class Role {
    @Getter
    private final long id;
    @Getter
    private final String name;

    public Role(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Role of(net.dv8tion.jda.api.entities.Role role) {
        return of(role.getIdLong(), role.getName());
    }

    public static Role of(long id, String name) {
        return new Role(id, name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return id == role.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
