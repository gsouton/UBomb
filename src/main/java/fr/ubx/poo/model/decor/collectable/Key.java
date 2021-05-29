package fr.ubx.poo.model.decor.collectable;

import fr.ubx.poo.model.go.character.Player;

public class Key extends Collectable{


    @Override
    public String toString() {
        return "Key";
    }

    @Override
    public boolean isBonus() {
        return false;
    }

    @Override
    public void take(Player player) {
        player.takeKey();
    }
}
