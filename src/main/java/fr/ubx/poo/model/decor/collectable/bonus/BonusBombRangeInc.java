package fr.ubx.poo.model.decor.collectable.bonus;

import fr.ubx.poo.model.go.character.Player;

public class BonusBombRangeInc extends Bonus {

    @Override
    public String toString() {
        return "BonusBombRangeInc";
    }

    @Override
    public void take(Player player) {
        player.takeBonusRangeInc();
    }
}
