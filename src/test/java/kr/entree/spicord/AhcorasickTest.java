package kr.entree.spicord;

import kr.entree.spicord.config.Parameter;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by JunHyung Lim on 2019-11-27
 */
public class AhcorasickTest {
    @Test
    public void replace() {
        String contents = "&7(%test%){%test%} %test%%test%";
        Parameter parameter = Parameter.of().put("%test%", "replaced");
        Assert.assertEquals(
                "&7(replaced){replaced} replacedreplaced",
                parameter.format(contents)
        );
    }
}
