package fr.ubx.poo.model.decor.collectable;

import fr.ubx.poo.model.decor.Decor;
import fr.ubx.poo.model.go.character.Player;

public abstract class Collectable extends Decor {

    protected boolean taken = false;


    public abstract void take(Player player);


    public boolean isBonus(){
        return true;
    }

    @Override
    public boolean collectable() {
        return true;
    }

    public boolean isTaken(){
        return taken;
    }

}
