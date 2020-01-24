package kr.entree.spicord.bukkit.structure;

import lombok.Getter;

/**
 * Created by JunHyung Lim on 2019-11-29
 */
public class Message {
    @Getter
    private final Long id;
    @Getter
    private final User author;
    @Getter
    private final String contents;
    @Getter
    private final String channelId;

    private Message(Long id, User author, String contents, String channelId) {
        this.id = id;
        this.author = author;
        this.contents = contents;
        this.channelId = channelId;
    }

    public static Message of(Long id, User author, String contents, String channelId) {
        return new Message(id, author, contents, channelId);
    }

    public static Message of(net.dv8tion.jda.api.entities.Message message) {
        return of(
                message.getIdLong(),
                User.of(message.getAuthor()),
                message.getContentDisplay(),
                message.getChannel().getId()
        );
    }
}
