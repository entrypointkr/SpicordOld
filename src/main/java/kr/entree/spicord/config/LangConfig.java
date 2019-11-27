package kr.entree.spicord.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

/**
 * Created by JunHyung Lim on 2019-11-20
 */
public class LangConfig extends PluginConfiguration {
    public LangConfig(YamlConfiguration config, Plugin plugin) {
        super(config, plugin);
    }

    public LangConfig(Plugin plugin) {
        super(plugin);
    }

    @Override
    protected String getFileName() {
        return "lang.yml";
    }

    @Override
    public void load() {
        for (Lang lang : Lang.values()) {
            String key = lang.getKey();
            String def = lang.getDef();
            if (!contains(key)) {
                set(key, def);
            }
            addDefault(key, def);
        }
        super.load();
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
        return format(lang, Parameter.of());
    }
}
