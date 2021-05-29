package fr.ubx.poo.model.decor.collectable.bonus;

import fr.ubx.poo.model.go.character.Player;

public class BonusBombNbDec extends Bonus {
    @Override
    public String toString() {
        return "BonusBombNbDec";
    }

    @Override
    public void take(Player player) {
        player.takeBonusBombDec();
    }
}
