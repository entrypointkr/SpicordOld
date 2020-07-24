package kr.entree.spicord;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.mockito.stubbing.Answer;

import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by JunHyung Lim on 2019-11-29
 */
public class JDAFactory {
    public static MessageAction createMessageAction() {
        return mock(MessageAction.class);
    }

    public static TextChannel createTextChannel(Consumer<Object> receiver) {
        TextChannel channel = mock(TextChannel.class);
        Answer<MessageAction> answer = invocation -> {
            receiver.accept(invocation.getArguments()[0]);
            return createMessageAction();
        };
        when(channel.sendMessage(any(CharSequence.class))).thenAnswer(answer);
        when(channel.sendMessage(any(MessageEmbed.class))).thenAnswer(answer);
        when(channel.sendMessage(any(Message.class))).thenAnswer(answer);
        return channel;
    }
}
