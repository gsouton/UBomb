/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.game;


import java.io.*;
import java.util.*;

import fr.ubx.poo.model.decor.Door;
import fr.ubx.poo.model.go.Box;
import fr.ubx.poo.model.go.character.Monster;
import fr.ubx.poo.model.go.character.Player;

import static fr.ubx.poo.game.WorldEntity.*;


public class Game {


    /* Map and world attributes */
    private final World[] world; // array of all worlds of the game
    private final WorldEntity[][][] maps; // maps of the game (level1, level2, level3)
    private int nbLevel; // number of levels in the game
    private int level = 0; // actual level displayed


    /* GameObjects **/
    private final Player player;

    /* File loading attributes */
    private final String worldPath;
    private String levelPrefix; // prefix of file to load maps
    private final String extension = ".txt"; // extension of level files
    public int initPlayerLives;

    public Game(String worldPath) {
        this.worldPath = worldPath;
        loadConfig(worldPath);

        world = new World[nbLevel]; // number of worlds
        maps = new WorldEntity[nbLevel][][]; // same number of maps each world as one

        loadAllMaps(); // load all the maps
        createAllWorlds(); // create all the worlds from the maps

        Position positionPlayer;
        try {
            positionPlayer = world[0].findPlayer();
            player = new Player(this, positionPlayer);
        } catch (PositionNotFoundException e) {
            System.err.println("Position not found : " + e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
    }

    //Public methods

    /***
     * Get a world for a given level
     * @param level level that you want to get
     * @return World object
     */
    public World getWorld(int level){
        return world[level];
    }

    public World getWorld(){
        return world[level];
    }

    /**
     * Return the current level that is loaded and displayed
     * @return integer
     */
    public int getLevel() {
        return level;
    }


    public Player getPlayer() {
        return this.player;
    }

    /**
     * Check if cheat mode is on or not
     * @return Boolean : true if activated else false
     */
    public boolean isCheatMode() {
        return false;
    }


    public int getInitPlayerLives() {
        return initPlayerLives;
    }

    /***
     * Check if for a given position the player is present
     * @param position Position to check from
     * @return Boolean
     */
    public boolean isPlayer(Position position){
        return position.equals(player.getPosition());
    }


    /**
     * Load the level associated with a door with the position of the player
     * It assumes that the player is on a open door:
     *
     * @param playerPos Position of the player
     */
    public void loadDoor(Position playerPos){
        Door door = (Door) getWorld(level).get(playerPos);
        loadLevelDoor(door.isNext());
        Position newPos = world[level].searchForDoor(!door.isNext()); // search for the opposite door, (if you enter a next door --> search for previous)
        player.setPosition(newPos);
        player.setDirection(Direction.S);
    }

    //Private methods

    private void loadConfig(String path) {
        try (InputStream input = new FileInputStream(new File(path, "config.properties"))) {
            Properties prop = new Properties();
            // load the configuration file
            prop.load(input);
            initPlayerLives = Integer.parseInt(prop.getProperty("lives", "3"));
            levelPrefix = prop.getProperty("prefix"); //get prefix (level) of map in sample
            nbLevel = Integer.parseInt(prop.getProperty("levels", "3"));
        } catch (IOException ex) {
            System.err.println("Error loading configuration");
        }
    }


    /**
     *  Load the map from a level.txt using a BufferedReader
     *  Read one line and get the width of the map
     *      * By using the length of the file get height of the map
     *      * Create a 2 dimensional array of height and length to store the map as WorldEntity
     *      * Go through the file line by line and convert char to WorldEntity
     *
     * @param level level that desired to be loaded
     * @return WorldEntity[][]
     */
    private WorldEntity[][] loadMap(int level){
        level++;
        String mapTxt = levelPrefix + level + extension; //name of the file
        File file = new File(worldPath, mapTxt); // open the file
        WorldEntity[][] mapEntities;

        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(file))){
            int size = (int) file.length(); //size of the file level.txt
            String line = bufferedReader.readLine(); // read the first line
            int mapWidth = line.length();
            int mapHeight = size/mapWidth;

            mapEntities = new WorldEntity[mapHeight][mapWidth]; // allocation array
            for(int y = 0; y < mapHeight; y++){
                if(y != 0) // do not read the first line
                    line = bufferedReader.readLine();
                for(int x = 0; x < mapWidth; x++){
                    mapEntities [y][x] = fromCode(line.charAt(x)).get();
                }
            }
            return mapEntities;

        }catch (IOException ex){
            System.err.println("Error loading map from " + mapTxt);
            return null;
        }


    }

    /***
     *  Load all maps (level1.txt, level2.txt ..Etc) in memory
     */
    private void loadAllMaps(){
        for(int i = level; i < nbLevel; i++){
            maps[i] = loadMap(i);
        }
    }

    /***
     *  Create all the worlds with their GameObjects
     */
    private void createAllWorlds(){
        for(int i = level; i < nbLevel; i++){
            //gameObjects.add(createGameObjects(i));
            world[i] = new World(maps[i]/*, gameObjects.get(i)*/);
            createGameObjects(i);
        }
    }

    /**
     * Create all the gameObject associated with a level
     * @param level level chosen
     */
    private void /*ArrayList<GameObject>*/ createGameObjects(int level){
        for(int y = 0; y < maps[level].length; y++)
            for(int x = 0; x < maps[level][y].length; x++){
                WorldEntity entity = maps[level][y][x];
                processEntityToGameObject(entity, x, y, level);
            }
    }

    /**
     * Process a given entity to instantiate the GameObjects
     * @param entity Entity that you want to create
     * @param x position x on the map
     * @param y position y on the map
     * @param level level associated with the entity
     */
    private void processEntityToGameObject(WorldEntity entity, int x, int y, int level){
        switch (entity){
            case Monster:
                new Monster(this, new Position(x, y), level, false);
                break;
            case SmartMonster:
                new Monster(this, new Position(x, y), level, true);
                break;
            case Box:
                new Box(this, new Position(x, y), level);
                break;
            default:
                break;
        }
    }

    /**
     * Load a level when player go through a door,
     * If the door bring to the next level then we load in world the world[level++]
     * else we load the world[level--]
     * @param isNext Boolean indicating if the door bring to the next or previous level
     */
    private void loadLevelDoor(boolean isNext){
        if(isNext)
            level++;
        else
            level--;
    }








}
