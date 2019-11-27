package kr.entree.spicord.discord.task;

import kr.entree.spicord.discord.JDAHandler;
import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Created by JunHyung Lim on 2019-11-17
 */
public class Completer implements JDAHandler {
    private final JDAHandler handler;
    private final Runnable success;
    private final Consumer<Exception> fail;
    private final Runnable finals;

    public Completer(@NotNull JDAHandler handler, @NotNull Runnable success,
                     @NotNull Consumer<Exception> fail, @NotNull Runnable finals) {
        this.handler = handler;
        this.success = success;
        this.fail = fail;
        this.finals = finals;
    }

    @Override
    public void handle(JDA jda) {
        try {
            handler.handle(jda);
            success.run();
        } catch (Exception ex) {
            fail.accept(ex);
        }
        finals.run();
    }
}
