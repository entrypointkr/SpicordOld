package kr.entree.spicord.bukkit.structure;

import kr.entree.spicord.util.Option;
import lombok.Data;
import lombok.val;
import org.jetbrains.annotations.Nullable;

/**
 * Created by JunHyung Lim on 2019-11-29
 */
@Data
public class Message {
    private final Long id;
    private final User author;
    private final Member member;
    private final String contents;
    private final String channelId;

    public static Message of(Long id, User author, @Nullable Member member, String contents, String channelId) {
        return new Message(id, author, member, contents, channelId);
    }

    public static Message of(net.dv8tion.jda.api.entities.Message message) {
        val member = message.getMember() != null ? Member.of(message.getMember()) : null;
        return of(
                message.getIdLong(),
                User.of(message.getAuthor()),
                member,
                message.getContentDisplay(),
                message.getChannel().getId()
        );
    }

    public Option<Member> getMember() {
        return Option.of(member);
    }
}
