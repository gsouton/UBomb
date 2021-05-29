package fr.ubx.poo.model.decor.collectable.bonus;

import fr.ubx.poo.model.go.character.Player;

public class Heart extends Bonus {

    @Override
    public String toString() {
        return "Heart";
    }

    @Override
    public void take(Player player) {
        player.takeHeart();
    }
}
