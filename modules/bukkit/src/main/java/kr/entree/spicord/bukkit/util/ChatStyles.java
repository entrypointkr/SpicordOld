package kr.entree.spicord.bukkit.util;

import lombok.val;
import lombok.var;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * Created by JunHyung Im on 2020-07-26
 */
public class ChatStyles {
    @Nullable
    public static ChatColor getFirstColor(String from) {
        if (from == null || from.isEmpty()) return null;
        val chars = from.toCharArray();
        var prev = chars[0];
        for (int i = 1; i < chars.length; i++) {
            if (prev != ChatColor.COLOR_CHAR) {
                prev = chars[i];
                continue;
            }
            val style = ChatColor.getByChar(chars[i]);
            if (style != null && style != ChatColor.WHITE && style.isColor()) {
                return style;
            }
        }
        return null;
    }

    @Nullable
    public static Color bukkitToAwt(ChatColor style) {
        if (style == null || style.isFormat()) return null;
        switch (style) {
            case BLACK:
                return Color.BLACK;
            case DARK_BLUE:
                return new Color(0, 0, 170);
            case DARK_GREEN:
                return new Color(0, 170, 0);
            case DARK_AQUA:
                return new Color(0, 170, 170);
            case DARK_RED:
                return new Color(170, 0, 0);
            case DARK_PURPLE:
                return new Color(170, 0, 170);
            case GOLD:
                return new Color(255, 170, 0);
            case GRAY:
                return new Color(170, 170, 170);
            case DARK_GRAY:
                return new Color(85, 85, 85);
            case BLUE:
                return new Color(85, 85, 255);
            case GREEN:
                return new Color(85, 255, 85);
            case AQUA:
                return new Color(85, 255, 255);
            case RED:
                return new Color(255, 85, 85);
            case LIGHT_PURPLE:
                return new Color(255, 85, 255);
            case YELLOW:
                return new Color(255, 255, 85);
            case WHITE:
                return Color.WHITE;
            default:
                return null;
        }
    }
}
