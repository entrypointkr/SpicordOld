package kr.entree.spicord.discord;

import net.dv8tion.jda.api.JDA;

/**
 * Created by JunHyung Lim on 2019-11-18
 */
public class EmptyHandler implements JDAHandler {
    public static final EmptyHandler INSTANCE = new EmptyHandler();

    private EmptyHandler() {
    }

    @Override
    public void handle(JDA jda) {
        // Ignore
    }
}
