package kr.entree.spicord.bukkit.util;

import lombok.experimental.UtilityClass;
import lombok.val;
import org.apache.commons.lang.StringUtils;

@UtilityClass
public class Strings {
    public static String substringAfter(String string, String separator) {
        val ret = StringUtils.substringAfter(string, separator);
        return ret.isEmpty() ? string : ret;
    }
}
