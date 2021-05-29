package fr.ubx.poo.model.decor.collectable.bonus;

import fr.ubx.poo.model.go.character.Player;

public class BonusBombNbInc extends Bonus {

    @Override
    public String toString() {
        return "BonusBombNbInc";
    }

    @Override
    public void take(Player player) {
        player.takeBonusBombInc();
    }
}
