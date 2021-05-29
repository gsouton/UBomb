/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.game;

import fr.ubx.poo.model.decor.Decor;
import fr.ubx.poo.model.decor.Door;
import fr.ubx.poo.model.go.GameObject;

import java.util.*;
import java.util.function.BiConsumer;

public class World {
    private final Map<Position, Decor> grid;
    private final WorldEntity[][] raw;
    public final Dimension dimension;
    private final ArrayList<GameObject> gameObjects;
    private final ArrayList<Position> explosionMap = new ArrayList<>();

    public World(WorldEntity[][] raw) {
        this.raw = raw;
        dimension = new Dimension(raw.length, raw[0].length);
        grid = WorldBuilder.build(raw, dimension);
        this.gameObjects = new ArrayList<>();
    }

    /*public World(WorldEntity[][] raw, ArrayList<GameObject> gameObjects){
        this.raw = raw;
        dimension = new Dimension((raw.length), raw[0].length);
        grid = WorldBuilder.build(raw, dimension);
        this.gameObjects = gameObjects;
    }*/

    //public methods

    public Position findPlayer() throws PositionNotFoundException {
        for (int x = 0; x < dimension.width; x++) {
            for (int y = 0; y < dimension.height; y++) {
                if (raw[y][x] == WorldEntity.Player) {
                    return new Position(x, y);
                }
            }
        }
        throw new PositionNotFoundException("Player");
    }

    public Collection<GameObject> getGameObjects() {
        return gameObjects;
    }

    public Decor get(Position position) {
        return grid.get(position);
    }

    public void set(Position position, Decor decor) {
        grid.put(position, decor);
    }

    public void clear(Position position) {
        grid.remove(position);
    }

    public void forEach(BiConsumer<Position, Decor> fn) {
        grid.forEach(fn);
    }

    public Collection<Decor> values() {
        return grid.values();
    }

    public boolean isInside(Position position) {
        return position.inside(dimension);
    }



    /**
     * Check if the given position is empty
     * Only check for decor elements
     * @param position Position to check from
     * @return boolean
     */
    public boolean isEmpty(Position position) {
        return grid.get(position) == null;
    }


    /**
     * Check if you can walk on the given position
     * Box included (if it's possible to push them)
     * @param position Position to check
     * @return boolean
     */
    public boolean walkAble(Position position){
        if(isEmpty(position)){
            return isWalkableGameObject(position);
        }else{
            return get(position).collectable();
        }
    }

    /**
     * Check if the next position is a collectable
     * @param position position to check from
     * @return boolean
     */
    public boolean isCollectable(Position position){
        if(!isEmpty(position))
            return get(position).collectable();
        return false;

    }

    /**
     * Check if the next position is an explosion
     * @param position position to check from
     * @return boolean
     */
    public boolean isExplosion(Position position){
        return explosionMap.contains(position);
    }

    /**
     * Remove Explosion blast of a bomb
     * @param positions Collection of positions that need to be removed
     */
    public void removeExplosion(Collection<Position> positions){
        for(Position pos : positions){
            explosionMap.remove(pos);
        }

    }

    /**
     * Check at the given position if there is a monster
     * @param position Position to check from
     * @return Boolean
     */
    public boolean isMonster(Position position){
        return gameObjects.stream().anyMatch(gameObject ->
                gameObject.isCharacter() && gameObject.getPosition().equals(position));
    }


    /**
     * Add a game object to the map of Gamecock
     * @param go GameObject to add
     */
    public void addGameObject(GameObject go){ gameObjects.add(go);}





    /***
     * Check if there is a GameObject at the given position
     * @param position Position to check from
     * @return Boolean
     */
    public boolean isGameObject(Position position){
        for(GameObject go : gameObjects)
            if(go.getPosition().equals(position))
                return true;
        return false;
    }

    /**
     * Get the position of a given GameObject (except for player)
     * @param go GameObject
     * @return Position
     */
    public Position getPositionGameObject(GameObject go){
        for(GameObject g : gameObjects){
            if(go.equals(g))
                return g.getPosition();
        }
        return null;
    }

    /***
     * Check if there is a GameObject at the given position and if it's possible to walk over
     * @param position  Position to check from
     * @return Boolean : true if there is no GameObject or possible to walk over else false
     */
    public boolean isWalkableGameObject(Position position){
        for(GameObject go : gameObjects) {
            //System.out.println("go : " + go + " position : " + go.getPosition());
            if (go.getPosition().equals(position)) {
                return go.walkAble();
            }
        }
        return true;
    }

    /***
     * Return the GameObject for the given position
     * @param position Position to retrieve from
     * @return Gamecock, null if not found
     */
    public GameObject getGameObject(Position position){
        for(GameObject go : gameObjects) {
            if (go.getPosition().equals(position)) {
                return go;
            }
        }
        return null;
    }


    /***
     * Delete the GameObject from the map
     * @param go GameObject to delete
     */
    public void clearGameObject(GameObject go){
        gameObjects.remove(go);
    }

    public void addExplosionToMap(Collection<Position> positions){
        explosionMap.addAll(positions);
    }



    /***
     * Check if the given position is a door
     * @param position Position to check from
     * @return Boolean
     */
    public boolean isDoor(Position position){
        return !isEmpty(position) && get(position).isDoor();
    }

    /***
     * Check if the given position is an open door
     * !! Caution if false is returned it doesn't mean that it is not a closed door
     *
     * @param position Position to check from
     * @return Boolean
     */
    public boolean isOpenDoor(Position position) {
        if (isDoor(position))
            return ((Door) get(position)).isOpen();
        return false;
    }

    /**
     * Search for a door in the current world if isNext is true will search for a nextDoor
     * else for a previous Door
     * ! This function doesn't look if the door is open or close
     * ! This also return the first occurrence of corresponding door found
     *
     * @param isNext boolean to indicate which kind of door you're looking for; next or prev
     * @return return a position of a door if find else null
     */
    public Position searchForDoor(boolean isNext){
        Set<Position> positions = grid.keySet();
        Iterator<Position> iterator = positions.iterator();
        Position pos;

        while(iterator.hasNext()){
            pos =  iterator.next();
            Decor decor = get(pos);
            if(decor.isDoor()){
                if(((Door)decor).isNext() == isNext){
                    return pos;
                }
            }
        }
        return null;
    }




}
