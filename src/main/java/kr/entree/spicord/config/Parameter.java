package kr.entree.spicord.config;

import kr.entree.spicord.bukkit.structure.User;
import kr.entree.spicord.bukkit.util.Chat;
import kr.entree.spicord.bukkit.util.Platform;
import kr.entree.spicord.bukkit.util.PlayerData;
import lombok.val;
import net.dv8tion.jda.api.entities.Member;
import org.ahocorasick.trie.Emit;
import org.ahocorasick.trie.Trie;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by JunHyung Lim on 2019-11-24
 */
public class Parameter {
    private final Map<String, Object> map = new HashMap<>();

    public Parameter putServer() {
        return put("%players%", Platform.getOnlinePlayers().size());
    }

    public Parameter put(Chat chat) {
        put(chat.getPlayerData());
        put("%message%", chat.getMessage());
        return this;
    }

    public Parameter put(PlayerData player) {
        put("%name%", player.getName());
        put("%display-name%", player.getDisplayNameOrDefault());
        put("%player%", player.getName());
        put("%uuid%", player.getId());
        return this;
    }

    public Parameter put(Player player) {
        return put(new PlayerData(player));
    }

    public Parameter put(Member member) {
        put("%discord%", member.getEffectiveName());
        return this;
    }

    public Parameter put(String key, Object value) {
        map.put(key, value);
        return this;
    }

    public Parameter put(User user) {
        put("%discord%", user.getNameAsTag());
        return this;
    }

    public Parameter putSerialized(String serialized) {
        val pieces = serialized.split(",");
        val keyBuilder = new StringBuilder();
        for (String piece : pieces) {
            val pair = piece.split("=", 2);
            if (pair.length < 1) {
                continue;
            }
            keyBuilder.append('%').append(pair[0]).append('%');
            put(keyBuilder.toString(), pair[1].replace('_', ' '));
            keyBuilder.setLength(0);
        }
        return this;
    }

    public String format(String contents) {
        StringBuilder builder = new StringBuilder(contents.length() * 2);
        Trie trie = Trie.builder()
                .ignoreOverlaps()
                .ignoreCase()
                .addKeywords(map.keySet())
                .build();
        Collection<Emit> emits = trie.parseText(contents);
        int prevIndex = 0;
        for (Emit emit : emits) {
            int index = emit.getStart();
            builder.append(contents, prevIndex, index)
                    .append(map.get(emit.getKeyword()));
            prevIndex = emit.getEnd() + 1;
        }
        builder.append(contents.substring(prevIndex));
        return builder.toString();
    }
}
