package kr.entree.spicord.config;

import io.vavr.control.Either;
import io.vavr.control.Try;
import kr.entree.spicord.Spicord;
import kr.entree.spicord.util.Enums;
import kr.entree.spicord.util.Parameter;
import lombok.val;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.ConfigurationSection;

import java.util.stream.Stream;

/**
 * Created by JunHyung Lim on 2020-03-12
 */
public class SpicordRichPresence {
    private final OnlineStatus status;
    private final Activity activity;
    private final boolean idle;

    public SpicordRichPresence(OnlineStatus status, Activity activity, boolean idle) {
        this.status = status;
        this.activity = activity;
        this.idle = idle;
    }

    public static Either<String, SpicordRichPresence> parse(ConfigurationSection section, Parameter parameter) {
        val status = OnlineStatus.fromKey(section.getString("status", ""));
        if (status == OnlineStatus.UNKNOWN) {
            val availableStatus = Stream.of(OnlineStatus.values()).map(OnlineStatus::getKey).toArray();
            return Either.left("Available online status: " + StringUtils.join(availableStatus, ", "));
        }
        val idle = section.getBoolean("idle", false);
        return parseActivity(section, parameter).toEither()
                .mapLeft(Throwable::getMessage)
                .map(activity -> new SpicordRichPresence(status, activity, idle));
    }

    @SuppressWarnings("ConstantConditions")
    public static Try<Activity> parseActivity(ConfigurationSection section, Parameter parameter) {
        val activity = Enums.valueOf(Activity.ActivityType.class, section.getString("activity", "").toUpperCase());
        val name = parameter.format(section.getString("name", ""));
        val url = parameter.format(section.getString("url", ""));
        return Try.of(() -> Activity.of(activity, name, url));
    }

    public void update() {
        val jda = Spicord.discord().getJda();
        if (jda == null) return;
        jda.getPresence().setPresence(status, activity, idle);
    }
}
