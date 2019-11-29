package kr.entree.spicord.config;

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

    private Parameter() {
    }

    public static Parameter of() {
        return new Parameter();
    }

    public static Parameter ofPlayer(Player player) {
        return of().put("%player%", player.getName())
                .put("%name%", player.getName())
                .put("%uuid%", player.getUniqueId());
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
