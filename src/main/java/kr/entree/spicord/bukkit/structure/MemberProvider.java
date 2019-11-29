package kr.entree.spicord.bukkit.structure;

/**
 * Created by JunHyung Lim on 2019-11-29
 */
public interface MemberProvider extends UserProvider {
    Member getMember();

    @Override
    default User getUser() {
        return getMember().getUser();
    }
}
