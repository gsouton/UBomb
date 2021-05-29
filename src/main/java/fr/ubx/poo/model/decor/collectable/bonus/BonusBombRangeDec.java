package fr.ubx.poo.model.decor.collectable.bonus;

import fr.ubx.poo.model.go.character.Player;

public class BonusBombRangeDec extends Bonus {

    @Override
    public String toString() {
        return "BonusBombRangeDec";
    }

    @Override
    public void take(Player player) {
        player.takeBonusRangeDec();
    }
}
