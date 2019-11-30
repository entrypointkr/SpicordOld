package kr.entree.spicord.config;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Created by JunHyung Lim on 2019-11-20
 */
public enum Lang {
    PREFIX("&c[Spicord] &f"),
    VERIFY_MESSAGE("%prefix% 디스코드 &e%discord% &f로부터 인증 요청이 왔습니다. 채팅으로 인증 코드를 입력하세요."),
    VERIFY_NEEDS("%prefix% 디스코드 인증이 필요합니다. 서버 디스코드 인증채널에서 &e'%command% 게임닉네임' &f을 입력하여 인증하세요."),
    VERIFY_SUCCESS("%prefix% 인증되었습니다!"),
    NO_PERMISSION("%prefix% 권한이 없습니다."),
    ;
    @Getter
    private final String def;
    @Getter
    private final String key = normalizedKey();

    Lang(String def) {
        this.def = def;
    }

    public static String colorize(String contents) {
        return ChatColor.translateAlternateColorCodes('&', contents);
    }

    private String normalizedKey() {
        return name().toLowerCase().replace("_", "-");
    }

    public String get(ConfigurationSection config) {
        return colorize(config.getString(key, def));
    }
}
