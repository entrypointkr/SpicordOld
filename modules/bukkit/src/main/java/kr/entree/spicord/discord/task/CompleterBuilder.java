package kr.entree.spicord.discord.task;

import kr.entree.spicord.discord.JDAHandler;

import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by JunHyung Lim on 2019-11-19
 */
public class CompleterBuilder {
    public static final Runnable EMPTY_RUNNABLE = () -> {
    };
    public static final Consumer<Exception> DEFAULT_FAILURE = Exception::printStackTrace;
    private final JDAHandler handler;
    private Runnable success;
    private Consumer<Exception> failure;
    private Runnable finalizer;

    public CompleterBuilder(JDAHandler handler) {
        this.handler = handler;
    }

    public CompleterBuilder success(Runnable runnable) {
        this.success = runnable;
        return this;
    }

    public CompleterBuilder failure(Consumer<Exception> failure) {
        this.failure = failure;
        return this;
    }

    public CompleterBuilder failure(Logger logger) {
        return failure(ex -> logger.log(Level.WARNING, ex, () -> "Fails while jda handling"));
    }

    public CompleterBuilder finalizer(Runnable finalizer) {
        this.finalizer = finalizer;
        return this;
    }

    public CompleterBuilder latch(CountDownLatch latch) {
        return finalizer(latch::countDown);
    }

    public Completer build() {
        return new Completer(
                handler,
                success != null ? success : EMPTY_RUNNABLE,
                failure != null ? failure : DEFAULT_FAILURE,
                finalizer != null ? finalizer : EMPTY_RUNNABLE
        );
    }
}
