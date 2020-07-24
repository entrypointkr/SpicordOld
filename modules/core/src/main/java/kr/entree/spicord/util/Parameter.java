package kr.entree.spicord.util;

import kr.entree.spicord.discord.User;
import lombok.val;
import net.dv8tion.jda.api.entities.Member;
import org.ahocorasick.trie.Emit;
import org.ahocorasick.trie.Trie;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Created by JunHyung Im on 2020-07-23
 */
public class Parameter {
    private final Map<String, Object> map = new HashMap<>();

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

    public String getOrFormat(String key) {
        val replacement = map.get(key);
        val object = replacement instanceof Supplier<?>
                ? ((Supplier<?>) replacement).get()
                : replacement;
        return object != null ? object.toString() : "null";
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
                    .append(getOrFormat(emit.getKeyword()));
            prevIndex = emit.getEnd() + 1;
        }
        builder.append(contents.substring(prevIndex));
        return builder.toString();
    }
}
