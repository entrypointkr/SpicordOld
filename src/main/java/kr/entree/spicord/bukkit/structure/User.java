package kr.entree.spicord.bukkit.structure;

import lombok.Getter;

/**
 * Created by JunHyung Lim on 2019-11-29
 */
public class User {
    @Getter
    private final long id;
    @Getter
    private final boolean bot;
    @Getter
    private final String name;

    private User(long id, boolean bot, String name) {
        this.id = id;
        this.bot = bot;
        this.name = name;
    }

    public static User of(long id, boolean bot, String name) {
        return new User(id, bot, name);
    }

    public static User of(net.dv8tion.jda.api.entities.User user) {
        return of(user.getIdLong(), user.isBot(), user.getName());
    }
}
