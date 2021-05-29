package fr.ubx.poo.model.decor.collectable;

import fr.ubx.poo.model.decor.collectable.Collectable;
import fr.ubx.poo.model.go.character.Player;

public class Princess extends Collectable {
    @Override
    public String toString() {
        return "Princess";
    }

    @Override
    public boolean isPrincess() {
        return true;
    }

    @Override
    public void take(Player player) {
        player.takePrincess();
    }
}
