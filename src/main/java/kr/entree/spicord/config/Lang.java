package kr.entree.spicord.config;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Created by JunHyung Lim on 2019-11-20
 */
public enum Lang {
    PREFIX("&c[Spicord] &f"),
    VERIFY_MESSAGE("%prefix% 디스코드 &e%discord% &f가 본인이 맞으시다면 디스코드에 &e'!인증 &e%code%' &f를 입력해주세요."),
    NO_PLAYER_FOUND("플레이어 %player% 를 찾을 수 없습니다."),
    ALREADY_VERIFIED("이미 인증된 계정입니다."),
    NOT_MATCHES_CODE("인증번호가 잘못되었습니다. `%command% 게임닉네임` 을 다시 입력하세요."),
    VERIFY_USAGE("명령어 사용법: `%command% 게임닉네임`"),
    NEEDS_VERIFY("%prefix% 디스코드 인증이 필요합니다. 서버 디스코드 인증채널에서 &e'%command% 게임닉네임' &f을 입력하여 인증하세요."),
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
