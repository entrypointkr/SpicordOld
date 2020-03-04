package kr.entree.spicord.discord;

import kr.entree.spicord.Spicord;

/**
 * Created by JunHyung Lim on 2019-12-30
 */
public abstract class JDATask implements JDAHandler {
    public void queue(Discord discord) {
        discord.addTask(this);
    }

    public void queue() {
        queue(Spicord.get().discord());
    }
}