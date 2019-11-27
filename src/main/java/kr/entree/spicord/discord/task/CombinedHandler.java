package kr.entree.spicord.discord.task;

import kr.entree.spicord.discord.JDAHandler;
import net.dv8tion.jda.api.JDA;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by JunHyung Lim on 2019-11-24
 */
public class CombinedHandler implements JDAHandler {
    private final List<JDAHandler> handlers = new ArrayList<>();

    public CombinedHandler add(JDAHandler... handlers) {
        this.handlers.addAll(Arrays.asList(handlers));
        return this;
    }

    @Override
    public void handle(JDA jda) {
        for (JDAHandler handler : handlers) {
            handler.handle(jda);
        }
    }
}
