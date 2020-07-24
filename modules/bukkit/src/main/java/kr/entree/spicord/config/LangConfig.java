package kr.entree.spicord.config;

import dagger.Reusable;
import kr.entree.spicord.util.Parameter;
import org.bukkit.plugin.Plugin;

import javax.inject.Inject;

/**
 * Created by JunHyung Lim on 2019-11-20
 */
@Reusable
public class LangConfig extends PluginConfiguration {
    public LangConfig(Plugin plugin, String fileName) {
        super(plugin, fileName);
    }

    @Inject
    public LangConfig(Plugin plugin) {
        this(plugin, "lang.yml");
    }

    @Override
    public void onLoad() {
        for (Lang lang : Lang.values()) {
            String key = lang.getKey();
            String def = lang.getDef();
            if (!contains(key)) {
                set(key, def);
            }
            addDefault(key, def);
        }
        save();
    }

    public String get(Lang lang) {
        return lang.get(this);
    }

    public String format(Lang lang, Parameter parameter) {
        return parameter.put("%prefix%", Lang.PREFIX.get(this))
                .format(get(lang));
    }

    public String format(Lang lang) {
        return format(lang, new Parameter());
    }
}
