package fr.ubx.poo.model.decor;

public class Explosion extends Decor{
    @Override
    public String toString() {
        return "Explosion";
    }

    @Override
    public boolean isExplosion() {
        return true;
    }
}
