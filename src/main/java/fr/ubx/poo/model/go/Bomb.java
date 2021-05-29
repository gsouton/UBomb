package fr.ubx.poo.model.go;

import fr.ubx.poo.game.Direction;
import fr.ubx.poo.game.Game;
import fr.ubx.poo.game.Position;
import fr.ubx.poo.model.go.character.Character;
import fr.ubx.poo.view.image.ImageFactory;
import javafx.animation.AnimationTimer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * GameObject representing a bomb in the Game
 */
public class Bomb extends GameObject {

    /* State Variables */
    private final int nbOfStates = ImageFactory.getInstance().nbBombFrames();
    private int bombState = 0;

    /* Timer */
    private long time = 0; // current time
    private final int explosionTime = nbOfStates -1; // explosion time
    private final long creationTime; // save the time when the bomb was created

    /* Attributes */
    private final int range;
    private final int level;
    private final Collection<Position> blast = new ArrayList<>();

    /***
     *  Instantiate a Bomb object with his own timer and update method.
     *
     * @param game  Reference to the game
     * @param position  Position in the world where the bomb is instantiated
     * @param time  Time when the bomb is instantiated
     * @param range Range of the blast of the bomb
     */
    public Bomb(Game game, Position position, long time, int range) {
        super(game, position);
        this.creationTime =  TimeUnit.NANOSECONDS.toSeconds(time);
        this.range = range;
        timer();
        level = game.getLevel();
        game.getWorld(level).addGameObject(this);
    }

    @Override
    public boolean walkAble() {
        return false;
    }

    @Override
    public boolean isBomb() {
        return true;
    }

    /**
     * Create an Animation Timer and update the bomb state
     */
    private void timer(){
        /* Timer */
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update(now);
            }
        };
         timer.start();
    }

    /**
     *  Positions of the blast of the explosion
     * @return Collection<Position>
     */
    public Collection<Position> getBlast() {
        return blast;
    }

    /**
     * Update the state of the bomb
     * @param now time in Nanoseconds
     */
    private void update(long now){
            int time = (int) (TimeUnit.NANOSECONDS.toSeconds(now) - creationTime); // calculate time passed in seconds
            if (time > this.time) { // if a second passed
                this.time = time;
                incBombState(); // update bombState
            }
            if(isExplosionOver()){
                game.getWorld(level).removeExplosion(blast);
            }


    }
            //if(isExplosionOver()) ---> destroy the collision for the corresponding world


    /**
     * return bombState (represent each second of the bomb since its creation)
     * @return integer
     */
    public int getBombState(){
        return bombState;
    }


    /**
     * Increase the bombState
     */
    public void incBombState(){
        bombState++;
    }

    /**
     * Check if time for explosion
     * @return boolean
     */
    public boolean isExplosionState(){
        return bombState == explosionTime;
    }

    /***
     *  Check if the explosion is over
     * @return boolean
     */
    public boolean isExplosionOver() {
        return bombState > explosionTime+1;
    }


    /**
     *  Calculate the blast of the explosion of the bomb and store the positions in blast
     */
    private void computeBlast(){
        computeBlastInADirection(getPosition(), Direction.N, range);
        computeBlastInADirection(getPosition(), Direction.S, range);
        computeBlastInADirection(getPosition(), Direction.W, range);
        computeBlastInADirection(getPosition(), Direction.E, range);
    }

    /**
     *  Calculate the blast of the explosion in a given direction and range
     *  If a bomb is found when calculating the blast it will
     *  trigger the explosion of bomb that are in range
     * @param position  Position of the bomb
     * @param direction Direction of the blast
     * @param range Range of the blast
     */
    private void computeBlastInADirection(Position position, Direction direction, int range){
        if( range < 0 || (!game.getWorld(level).isEmpty(position) && !game.getWorld(level).isCollectable(position))
                || !game.getWorld(level).isInside(position))
            return;
        if(game.isPlayer(position))
            game.getPlayer().requestDamage();
        if(game.getWorld(level).isGameObject(position)){
            GameObject go = game.getWorld(level).getGameObject(position);
            if(go.isCharacter())
                ((Character)go).requestDamage();
            if(go.isBomb()) {
                //assert go instanceof Bomb;
                ((Bomb)go).triggerExplosion();
            }
            if(go.isBox()){
                //assert go instanceof Box;
                ((Box)go).destroy();
                blast.add(position);
                return;
            }
        }
        if(!blast.contains(position))
            blast.add(position);
        position = direction.nextPosition(position);
        computeBlastInADirection(position, direction, range-1);
    }

    /***
     *  Return the level where the bomb exist
     * @return integer
     */
    public int getLevel() {
        return level;
    }


    /***
     *  Compute all the logic linked to the explosion of the bomb :
     *      - Compute the blast
     *      - Update the state of the bomb
     *      - Retrieve to the player the exploded bomb
     *      - Delete the GameObject from the corresponding world
     */
    public void explode(){
        computeBlast();
        game.getWorld(level).addExplosionToMap(blast);
        incBombState();
        game.getPlayer().incOwnedBombs();
        game.getWorld(level).clearGameObject(this);
    }

    /***
     *  Trigger the bomb and set its state to explosion
     */
    public void triggerExplosion(){
        bombState = explosionTime;
    }
}
