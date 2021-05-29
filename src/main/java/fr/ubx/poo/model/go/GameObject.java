/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.model.go;

import fr.ubx.poo.game.Position;
import fr.ubx.poo.game.Game;
import fr.ubx.poo.model.Entity;

/***
 * A GameObject can access the game and knows its position in the grid.
 */
public abstract class GameObject extends Entity {
    protected final Game game;
    private Position position;
    protected boolean monster = false;
    protected boolean alive = true;
    protected boolean character = false;
    protected int level;


    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public GameObject(Game game, Position position) {
        this.game = game;
        this.position = position;
    }

    /**
     * Check if the GameObject is a character
     * @return boolean
     */
    public boolean isCharacter(){ return character;}

    /**
     * Check if the Gamecock is a bomb
     * @return boolean
     */
    public boolean isBomb(){
        return false;
    }

    /**
     * Check if the GameObject is a Box
     * @return boolean
     */
    public boolean isBox(){
        return false;
    }

    /**
     * Check if the GameObject is still alive
     * @return boolean
     */
    public boolean isAlive(){
        return alive;
    }

    /**
     * Check if it's possible to walk over the gameObject
     * @return boolean
     */
    public boolean walkAble(){
        return true;
    }
}
