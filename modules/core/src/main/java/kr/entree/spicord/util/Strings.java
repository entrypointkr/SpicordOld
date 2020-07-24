package kr.entree.spicord.util;

import lombok.experimental.UtilityClass;
import lombok.val;
import org.apache.commons.lang.StringUtils;

@UtilityClass
public class Strings {
    public String substringAfter(String string, String separator) {
        val ret = StringUtils.substringAfter(string, separator);
        return ret.isEmpty() ? string : ret;
    }
}
