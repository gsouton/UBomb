/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.model.go.character;

import fr.ubx.poo.game.Direction;
import fr.ubx.poo.game.Position;
import fr.ubx.poo.model.decor.Door;
import fr.ubx.poo.model.decor.collectable.Collectable;
import fr.ubx.poo.game.Game;


/**
 * GameObject representing a player in the Game
 */
public class Player extends Character {


    /* State Boolean */
    private boolean enteringDoor = false; // bool to know if the player is entering a door
    private boolean winner = false; // bool indicating if the player won or not
    private boolean bombRequested = false; // bool indicating a request to drop a bomb
    private boolean take = false; // bool indicating that the player is collecting an object

    /* Player Stats */
    private int keys = 0; // number of keys owned
    private int nbBombDropped = 0; // number of bombs that the player dropped
    private int maxBombs = 1; // number maximum of bomb that the player can carry
    private int bombRange = 1; // Range of the explosion of the bombs


    public Player(Game game, Position position) {
        super(game, position);
        this.lives = game.getInitPlayerLives();
        level = game.getLevel();
    }

    /**
     * Number of lives of the player
     * @return integer
     */
    public int getLives() {
        return lives;
    }

    /**
     * Number of keys owned by the player
     * @return integer
     */
    public int getKeys() {
        return keys;
    }

    /**
     * Range of the bombs of the player
     * @return integer
     */
    public int getBombRange() {
        return bombRange;
    }

    /**
     * Number of bombs maximum that the player can carry
     * @return integer
     */
    public int getMaxBombs(){ return maxBombs;}

    /**
     * Indicating if the player is winning or not
     * @return boolean
     */
    public boolean isWinner() { return winner; }

    /**
     * Indicating if the player is entering a door
     * @return boolean
     */
    public boolean isEnteringDoor() { return enteringDoor; }

    /**
     * Change the state of enteringDoor to false
     */
    public void exitDoor() {
        enteringDoor = false;
    }

    /**
     * Increase the number of bomb dropped by the player
     * (Decrease the number of bomb owned by the player)
     *
     */
    private void dropABomb(){
        nbBombDropped++;
    }

    /**
     * Increase the number of bomb owned by the player
     */
    public void incOwnedBombs(){
        if(nbBombDropped > 0)
            nbBombDropped--;
    }

    /**
     * Indicate if the player is collecting an object
     * @return boolean
     */
    public boolean isCollecting(){
        return take;
    }

    /**
     * Change the state of take to false
     */
    public void endCollecting(){
        take = false;
    }

    /**
     * Increase the number of keys owned by the player by 1
     */
    public void takeKey(){
        keys++;
    }

    /**
     * Increase the number of lives of the player by 1
     */
    public void takeHeart(){
        lives++;
    }

    /**
     * Increase the number maximum of bombs that the player can carry
     */
    public void takeBonusBombInc(){
        maxBombs++;
    }

    /**
     * Decrease the number maximum of bombs that the player can carry
     */
    public void takeBonusBombDec(){
        if(maxBombs > 1)
            maxBombs--;
    }

    /**
     * Increase the range of the bombs of the player
     */
    public void takeBonusRangeInc(){
        bombRange++;
    }

    /**
     * Decrease the range of the bombs of the player
     */
    public void takeBonusRangeDec(){
        if(bombRange > 1)
            bombRange--;
    }

    /**
     * Save the princess and make the player winner
     */
    public void takePrincess(){
        winner = true;
    }

    @Override
    public String toString() { return "Player"; }

    /**
     * Updating the player position, and managing the bomb request
     * @param now time
     */
    public void update(long now) {
        super.update(now);
        if (bombRequested) { // if bomb want to be dropped
            dropABomb();
            bombRequested = false;
        }
    }


    /**
     * Check if a player can move in a given direction
     * @param direction Direction to move to
     * @return boolean
     */
    @Override
    public boolean canMove(Direction direction) {
        Position n_pos = direction.nextPosition(this.getPosition());
        //System.out.println(n_pos + " : " + this.getPosition() + " " + direction);
        return game.getWorld().isInside(n_pos) &&
                (game.getWorld().walkAble(n_pos) || game.getWorld().isOpenDoor(n_pos));
    }


    /**
     * Set the position of the player for a given direction
     * @param direction Direction to move to
     */
    public void doMove(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());
        setPosition(nextPos);
        //entering a door
        if (game.getWorld().isOpenDoor(nextPos)) {
            enteringDoor = true;
        }
        if(game.getWorld().isCollectable(nextPos)){
            ((Collectable)game.getWorld().get(nextPos)).take(this);
            take = true;
            game.getWorld().clear(nextPos);
        }
        if(game.getWorld().isExplosion(nextPos) || game.getWorld().isMonster(nextPos)){
            requestDamage();
        }
    }

    /**
     * Check if the player canOpen
     *
     * @return Boolean
     */
    private Door canOpenDoor() {
        Position nextPos = getNextPos(direction);
        //if player is facing a door
        if (game.getWorld().isDoor(nextPos)) {
            Door door = (Door) game.getWorld().get(nextPos);
            if (!door.isOpen() && keys > 0) {
                return door;
            }
        }
        return null;

    }

    /**
     * Open a given door and decrease the number of keys
     * @param door Door that you want to open
     */
    private void openDoor(Door door){
        door.openDoor();
        keys--;

    }

    /**
     * Try to open a door
     * @return boolean : true if success else false
     */
    public boolean requestDoor(){
        Door door = canOpenDoor();
        if(door != null) {
            openDoor(door);
            return true;
        }
        return false;
    }

    /**
     * Change the state of bombRequested to true and return true if it's possible to drop a bomb
     * else return false
     *
     * @param position Position to request the bomb from
     * @return Boolean
     */
    public boolean requestBomb(Position position){
        if(canDropABomb(position)) // if can drop
            return bombRequested = true;
        return bombRequested = false;

    }

    /**
     * Check for a given position if it's possible to drop a bomb
     *
     * @param position  Position where the bomb want to be dropped
     * @return  Boolean, True if possible else false
     */
    public boolean canDropABomb(Position position){
        return nbBombDropped < maxBombs
                && game.getWorld().isEmpty(position)
                && !game.getWorld().isGameObject(position);
    }

    /**
     *  If the player is not invincible he gets 1 of damage, : number of lives decrease of 1
     */
    public void setDamage(){
        if(!invincible)
            lives--;
        if(!game.isCheatMode()) {
            if (lives <= 0) {
                winner = false;
                alive = false;
            }
        }
    }





}