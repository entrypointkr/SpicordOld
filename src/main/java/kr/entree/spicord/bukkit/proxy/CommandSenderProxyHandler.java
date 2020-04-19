package kr.entree.spicord.bukkit.proxy;

import kr.entree.spicord.bukkit.util.Defaults;
import kr.entree.spicord.util.Proxies;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.function.Consumer;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class CommandSenderProxyHandler implements InvocationHandler {
    private final Consumer<String> messenger;
    private final Supplier<Boolean> accessible;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        switch (method.getName()) {
            case "sendMessage":
                messenger.accept(ChatColor.stripColor(args[0].toString()));
                return null;
            case "isOp":
            case "hasPermission":
                return accessible.get();
            case "getName":
                return "SpicordSender";
        }
        return Defaults.defaultValue(method.getReturnType());
    }

    public static CommandSender createProxy(Consumer<String> messenger, Supplier<Boolean> accessible) {
        return Proxies.create(CommandSender.class, new CommandSenderProxyHandler(messenger, accessible));
    }
}
