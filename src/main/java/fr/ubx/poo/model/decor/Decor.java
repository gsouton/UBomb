/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.model.decor;

import fr.ubx.poo.model.Entity;

/***
 * A decor is an element that does not know its own position in the grid.
 */
public class Decor extends Entity {

    public boolean collectable(){
        return false;
    }
    public boolean isDoor(){
        return false;
    }
    public boolean isExplosion(){ return false;}
    public boolean isPrincess(){
        return false;
    }
}
