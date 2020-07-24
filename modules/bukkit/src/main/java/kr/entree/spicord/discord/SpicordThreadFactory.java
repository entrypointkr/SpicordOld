package kr.entree.spicord.discord;

import kr.entree.spicord.Spicord;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadFactory;

/**
 * Created by JunHyung Lim on 2019-12-29
 */
public class SpicordThreadFactory implements ThreadFactory {
    private final ThreadFactory factory;
    private final Spicord spicord;

    public SpicordThreadFactory(ThreadFactory factory, Spicord spicord) {
        this.factory = factory;
        this.spicord = spicord;
    }

    @Override
    public Thread newThread(@NotNull Runnable r) {
        Thread thread = factory.newThread(r);
        thread.setContextClassLoader(spicord.getClass().getClassLoader());
        thread.setName("Spicord-" + thread.getName());
        return thread;
    }
}