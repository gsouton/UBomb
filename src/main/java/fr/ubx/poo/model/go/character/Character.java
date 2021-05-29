package fr.ubx.poo.model.go.character;

import fr.ubx.poo.game.Direction;
import fr.ubx.poo.game.Game;
import fr.ubx.poo.game.Position;
import fr.ubx.poo.model.Movable;
import fr.ubx.poo.model.go.GameObject;

import java.util.concurrent.TimeUnit;

/**
 * Abstract Class Character with basic attributes of a Character
 */
public abstract class Character extends GameObject implements Movable {

    /* State Boolean */
    protected boolean damageRequested = false;
    protected boolean moveRequested = false;
    protected boolean invincible = false;

    /* Character Stats */
    protected int lives = 1; // number of lives of the character
    protected long timeDamage = 0; // time recording of damages
    protected Direction direction; // direction of the player

    public Character(Game game, Position position) {
        super(game, position);
        this.character = true;
        this.direction = Direction.S;
    }

    abstract public void setDamage();

    abstract public void doMove(Direction direction);

    /**
     * Send a request to inflict damage
     */
    public void requestDamage(){
        if(!invincible){
            damageRequested = true;
        }
    }

    /**
     * Get the direction of character
     * @return Direction
     */
    public Direction getDirection(){
        return direction;
    }

    public Position getNextPos(Direction direction){
        return direction.nextPosition(getPosition());
    }

    public boolean isInvincible() {
        return invincible;
    }

    /**
     * Change the direction of the character to the given direction
     * @param direction Direction
     */
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    /**
     * Updating movement and damages of characters
     * @param now time
     */
    public void update(long now) {
        if (moveRequested) {
            if (canMove(direction)) {
                doMove(direction);
            }
        }
        moveRequested = false;
        if (damageRequested) {
            setDamage();
            setInvincible(now);
            damageRequested = false;
        }
        if (invincible) {
            deactivateInvincible(now);
        }
    }

    /**
     * Make the character invulnerable to any damages
     * @param now time in Nanoseconds
     */
    protected void setInvincible(long now){
        invincible = true;
        timeDamage = (int) TimeUnit.NANOSECONDS.toSeconds(now);
    }

    /**
     * Make the character vulnerable to damages
     * @param now time in Nanoseconds
     */
    protected void deactivateInvincible(long now){
        long time = TimeUnit.NANOSECONDS.toSeconds(now) - timeDamage;
        if(time == 1){
            invincible = false;
        }
    }

    /**
     * Send a request to move the character in the given direction
     * @param direction Direction
     */
    public void requestMove(Direction direction) {
        if (direction != this.direction) {
            this.direction = direction;
        }
        moveRequested = true;
    }


    // use this class to refactor player and monster
}
