package kr.entree.spicord.di.qualifier;

import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by JunHyung Lim on 2020-03-07
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface MessengerCooldown {
}
