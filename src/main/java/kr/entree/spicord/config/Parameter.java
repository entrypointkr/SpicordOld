package kr.entree.spicord.config;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
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

    public Parameter put(Player player) {
        put("%name%", player.getName());
        put("%player%", player.getName());
        put("%uuid%", player.getUniqueId());
        return this;
    }

    public Parameter put(User user) {
        put("%discord%", user.getName());
        return this;
    }

    public Parameter put(Member member) {
        put("%discord%", member.getEffectiveName());
        return this;
    }

    public Parameter put(String key, Object value) {
        map.put(key, value);
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
