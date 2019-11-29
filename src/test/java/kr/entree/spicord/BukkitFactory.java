package kr.entree.spicord;

import org.bukkit.entity.Player;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by JunHyung Lim on 2019-11-29
 */
public class BukkitFactory {
    public static Player createPlayer(String name) {
        Player player = mock(Player.class);
        when(player.getName()).thenReturn(name);
        return player;
    }
}
