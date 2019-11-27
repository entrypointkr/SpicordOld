package kr.entree.spicord.config;

import kr.entree.spicord.Spicord;
import kr.entree.spicord.discord.Discord;
import kr.entree.spicord.discord.EmptyHandler;
import kr.entree.spicord.discord.JDAHandler;
import kr.entree.spicord.discord.handler.EmbedMessage;
import kr.entree.spicord.discord.handler.EmptyMessageChannelHandler;
import kr.entree.spicord.discord.handler.MessageChannelHandler;
import kr.entree.spicord.discord.handler.PlainMessage;
import kr.entree.spicord.discord.task.ChannelHandler;
import kr.entree.spicord.discord.task.CombinedHandler;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Created by JunHyung Lim on 2019-11-16
 */
public class SpicordConfig extends PluginConfiguration {
    private VerificationConfig verifyConfig = null;

    public SpicordConfig(YamlConfiguration config, Plugin plugin) {
        super(config, plugin);
    }

    public SpicordConfig(Plugin plugin) {
        super(plugin);
    }

    @Override
    protected String getFileName() {
        return "config.yml";
    }

    public void update(Discord discord) {
        discord.setToken(getString("token", null));
    }

    public boolean isEnabled(String key) {
        return getBoolean(key + ".enabled", true);
    }

    public Set<String> getChannelIds(String key) {
        Object ids = get(key);
        if (ids instanceof Collection) {
            return ((Collection<?>) ids).stream()
                    .map(Object::toString)
                    .collect(Collectors.toSet());
        }
        return ids != null
                ? Collections.singleton(ids.toString())
                : Collections.emptySet();
    }

    @Nullable
    public ConfigurationSection getChannelSection() {
        return getConfigurationSection("channels");
    }

    public String formatChat(Message message) {
        User author = message.getAuthor();
        String contents = message.getContentDisplay();
        String def = "&7[Discord] [%name%]: &f%message%";
        String format = getString("discord-chat", def);
        if (format == null) {
            format = def;
        }
        return format.replace("%name%", author.getName())
                .replace("%message%", contents);
    }

    public JDAHandler getSendMessage(String id, Parameter parameter) {
        return getSendMessage(id, getMessage(id + ".message", parameter));
    }

    public JDAHandler getSendMessage(String id) {
        return getSendMessage(id, Parameter.of());
    }

    public JDAHandler getSendMessage(String id, MessageChannelHandler<TextChannel> handler) {
        if (!isEnabled(id)) {
            return EmptyHandler.INSTANCE;
        }
        Set<String> channels = getChannelIds(id + ".channel");
        CombinedHandler combined = new CombinedHandler();
        for (String channel : channels) {
            combined.add(ChannelHandler.ofText(this, channel, handler));
        }
        return combined;
    }

    public JDAHandler getServerOnMessage() {
        return getSendMessage("messages.server-on");
    }

    public JDAHandler getServerOffMessage(Parameter parameter) {
        return getSendMessage("messages.server-off", parameter);
    }

    public MessageChannelHandler<TextChannel> getMessage(String key, Parameter parameter) {
        Object messageObj = get(key);
        if (messageObj instanceof ConfigurationSection) {
            messageObj = ((ConfigurationSection) messageObj).getValues(false);
        }
        if (messageObj instanceof Map) {
            return new EmbedMessage<>(parseEmbed(((Map) messageObj), parameter));
        } else if (messageObj != null) {
            return new PlainMessage<>(messageObj.toString());
        }
        return new EmptyMessageChannelHandler<>();
    }

    public MessageChannelHandler<TextChannel> getMessage(String key) {
        return getMessage(key, Parameter.of());
    }

    public boolean isSlowModePlayerChat() {
        return getBoolean("messages.player-chat.slow-mode", false);
    }

    public MinecraftChat getDiscordChat() {
        boolean enabled = true;
        String message = "&7[Discord] [%name%]: &f%message%";
        Collection<String> worlds = new ArrayList<>();
        ConfigurationSection section = getConfigurationSection("messages.discord-chat");
        if (section != null) {
            enabled = section.getBoolean("enabled", true);
            message = section.getString("message", message);
            worlds.addAll(section.getStringList("worlds"));
        }
        return new MinecraftChat(enabled, message, worlds);
    }

    public VerificationConfig getVerification() {
        if (verifyConfig == null) {
            verifyConfig = new VerificationConfig(getConfigurationSection("verification"), getLogger());
        }
        return verifyConfig;
    }

    public static MessageEmbed parseEmbed(Map<?, ?> map, Parameter parameter) {
        EmbedBuilder builder = new EmbedBuilder();
        Object title = map.get("title");
        Object desc = map.get("description");
        if (desc == null) {
            desc = map.get("desc");
        }
        Object url = map.get("url");
        Object color = map.get("color");
        Object thumbnail = map.get("thumbnail");
        Object author = map.get("author");
        Object fields = map.get("fields");
        Object footer = map.get("footer");
        if (title != null) {
            if (url != null) {
                builder.setTitle(parameter.format(title.toString()), parameter.format(url.toString()));
            } else {
                builder.setTitle(parameter.format(title.toString()));
            }
        }
        if (desc != null) {
            builder.setDescription(parameter.format(desc.toString()));
        }
        if (color != null) {
            String colorStr = color.toString();
            Color realColor = parseColor(colorStr);
            if (realColor != null) {
                builder.setColor(realColor);
            } else {
                Spicord.logger().log(Level.WARNING, "Unknown color: {0}", colorStr);
            }
        }
        if (thumbnail != null) {
            builder.setThumbnail(parameter.format(thumbnail.toString()));
        }
        if (author != null) {
            String[] authorArr = parseAuthor(author, parameter);
            builder.setAuthor(authorArr[0], authorArr[1], authorArr[2]);
        }
        if (fields != null) {
            parseFields(fields, parameter).forEach(builder::addField);
        }
        if (footer != null) {
            String[] footerArr = parseFooter(footer, parameter);
            builder.setFooter(footerArr[0], footerArr[1]);
        }
        return builder.build();
    }

    private static String[] parseFooter(Object obj, Parameter parameter) {
        String[] ret = new String[]{null, null};
        if (obj instanceof Map) {
            Map<?, ?> map = ((Map) obj);
            Object text = map.get("text");
            Object icon = map.get("icon");
            ret[0] = parameter.format(parseString(text));
            ret[1] = parameter.format(parseString(icon));
        } else {
            String flat = obj.toString();
            String[] pieces = flat.split("\\|");
            ret[0] = parameter.format(pieces[0]);
            ret[1] = pieces.length >= 2 ? parameter.format(pieces[1]) : null;
        }
        return ret;
    }

    private static String parseString(Object obj) {
        return obj != null ? obj.toString() : null;
    }

    private static Collection<MessageEmbed.Field> parseFields(Object fields, Parameter parameter) {
        if (fields instanceof Collection) {
            List<MessageEmbed.Field> ret = new ArrayList<>();
            Collection fieldCollection = (Collection) fields;
            for (Object obj : fieldCollection) {
                if (obj instanceof Map) {
                    Map<?, ?> map = ((Map<?, ?>) obj);
                    Object name = map.get("name");
                    Object value = map.get("value");
                    Object inline = map.get("inline");
                    ret.add(new MessageEmbed.Field(
                            parameter.format(parseString(name)),
                            parameter.format(parseString(value)),
                            parseBoolean(parseString(inline), false)
                    ));
                } else if (obj != null) {
                    String field = obj.toString();
                    String[] values = field.split("\\|");
                    String name = values[0];
                    String value = values.length >= 2 ? values[1] : null;
                    boolean inline = values.length >= 3 && parseBoolean(values[2], false);
                    ret.add(new MessageEmbed.Field(parameter.format(name), parameter.format(value), inline));
                }
            }
            return ret;
        }
        return Collections.emptyList();
    }

    private static boolean parseBoolean(String boolable, boolean def) {
        try {
            return Boolean.parseBoolean(boolable);
        } catch (Exception ex) {
            // Ignore
        }
        return def;
    }

    private static Color parseColor(String color) {
        Color realColor = Color.getColor(color);
        if (realColor == null) {
            try {
                Field field = Color.class.getField(color.toUpperCase());
                realColor = (Color) field.get(null);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                // Ignore
            }
        }
        return realColor;
    }

    private static String[] parseAuthor(Object author, Parameter parameter) {
        String[] ret = new String[]{null, null, null};
        if (author instanceof ConfigurationSection) {
            author = ((ConfigurationSection) author).getValues(false);
        }
        if (author instanceof Map) {
            val map = ((Map<?, ?>) author);
            Object title = map.get("title");
            Object link = map.get("url");
            Object icon = map.get("icon");
            ret[0] = parseString(title);
            ret[1] = parseString(link);
            ret[2] = parseString(icon);
        } else if (author != null) {
            ret[0] = author.toString();
        }
        for (int i = 0; i < ret.length; i++) {
            String str = ret[i];
            if (str != null) {
                ret[i] = parameter.format(str);
            }
        }
        return ret;
    }
}
