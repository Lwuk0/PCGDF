package dev.pcgdf.fase;

import com.badlogic.gdx.utils.Array;
import dev.pcgdf.entities.Car;
import dev.pcgdf.entities.Player;

public class GerenciadorColisao {
    public boolean verificar(Player player, Array<Rua> ruas) {
        if (player == null || !player.isAtiva()) return false;
        for (Rua rua : ruas) {
            for (Car carro : rua.getVeiculos()) {
                if (player.isCollidingWith(carro)) return true;
            }
        }
        return false;
    }
}
