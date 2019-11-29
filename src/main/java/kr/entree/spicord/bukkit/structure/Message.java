package kr.entree.spicord.bukkit.structure;

import lombok.Getter;

/**
 * Created by JunHyung Lim on 2019-11-29
 */
public class Message {
    @Getter
    private final User author;
    @Getter
    private final String contents;

    private Message(User author, String contents) {
        this.author = author;
        this.contents = contents;
    }

    public static Message of(User author, String contents) {
        return new Message(author, contents);
    }

    public static Message of(net.dv8tion.jda.api.entities.Message message) {
        return of(
                User.of(message.getAuthor()),
                message.getContentDisplay()
        );
    }
}
