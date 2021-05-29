/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.engine;

import fr.ubx.poo.game.Direction;
import fr.ubx.poo.game.Position;
import fr.ubx.poo.model.go.Bomb;
import fr.ubx.poo.model.go.GameObject;
import fr.ubx.poo.view.sprite.Sprite;
import fr.ubx.poo.view.sprite.SpriteFactory;
import fr.ubx.poo.game.Game;
import fr.ubx.poo.model.go.character.Player;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.util.*;


public final class GameEngine {

    private static AnimationTimer gameLoop;
    private final String windowTitle;
    private final Game game;
    private final Player player;
    private final List<Sprite> sprites = new ArrayList<>();
    private StatusBar statusBar;
    private final Pane layer = new Pane();
    private Input input;
    private Stage stage;
    private Sprite spritePlayer;

    private final Map<GameObject, Sprite> gameObjectsSprites = new Hashtable<>(); // map of the gameObjects Boxes and monsters
    private final Map<Bomb, ArrayList<Sprite>> bomb_map = new Hashtable<>(); // map of the bomb with their sprites


    public GameEngine(final String windowTitle, Game game, final Stage stage) {
        this.windowTitle = windowTitle;
        this.game = game;
        this.player = game.getPlayer();
        initialize(stage, game, layer);
        buildAndSetGameLoop();
    }


    private void initialize(Stage stage, Game game, Pane layer) {
        this.stage = stage;
        Group root = new Group();
        int height = game.getWorld().dimension.height;
        int width = game.getWorld().dimension.width;
        int sceneWidth = width * Sprite.size;
        int sceneHeight = height * Sprite.size;
        Scene scene = new Scene(root, sceneWidth, sceneHeight + StatusBar.height);
        scene.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());
        stage.setTitle(windowTitle);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        input = new Input(scene);
        root.getChildren().add(layer);
        statusBar = new StatusBar(root, sceneWidth, sceneHeight, game);

        // Create decor sprites
        game.getWorld().forEach( (pos,d) -> sprites.add(SpriteFactory.createDecor(layer, pos, d)));
        //create player
        spritePlayer = SpriteFactory.createPlayer(layer, player);
        //create GameObjects
        createGameObjectSprites();
    }

    /**
     * Create All the GameObject sprites in the world
     */
    private void createGameObjectSprites(){
        for(GameObject go : game.getWorld().getGameObjects())
            if(!go.isBomb())
                gameObjectsSprites.put(go, SpriteFactory.createGameObject(layer, go));
    }

    /**
     * Remove (from screen) and delete all GameObject sprites
     */
    private void deleteGameObjectsSprites(){
        Iterator<Map.Entry<GameObject, Sprite>> iterator = gameObjectsSprites.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<GameObject, Sprite> entry = iterator.next();
            entry.getValue().remove();
            iterator.remove();
        }
    }

    /**
     * Change the stage
     * stage.close() is not called <br> because it's mainly just hiding the stage
     */
    private void changeStage(){
        //delete all sprites on screen
        deleteGameObjectsSprites();
        deleteAllSprites();
        removeSpriteBomb();
        spritePlayer.remove();

        stage.close();
        initialize(stage, game, layer);
    }




    protected final void buildAndSetGameLoop() {
        gameLoop = new AnimationTimer() {
            public void handle(long now) {
                // Check keyboard actions
                processInput(now);

                // Do actions
                update(now);

                // Graphic update
                render();
                statusBar.update(game);
            }
        };
    }

    private void processInput(long now) {
        if (input.isExit()) {
            gameLoop.stop();
            Platform.exit();
            System.exit(0);
        }
        if (input.isMoveDown()) {
            player.requestMove(Direction.S);
        }
        if (input.isMoveLeft()) {
            player.requestMove(Direction.W);
        }
        if (input.isMoveRight()) {
            player.requestMove(Direction.E);
        }
        if (input.isMoveUp()) {
            player.requestMove(Direction.N);
        }
        if(input.isKey()){ // if ENTER is pressed to open a door
            if(player.requestDoor()) {// if the player can open the door
                Position nextPos = player.getNextPos(player.getDirection());
                for(Sprite s : sprites){
                    if(s.getPosition().equals(nextPos))
                        s.remove();
                }
                sprites.removeIf(sprite -> sprite.getPosition().equals(nextPos));
                sprites.add(SpriteFactory.createDecor(layer, nextPos , game.getWorld().get(nextPos))); // add the open door sprite*/
            }

        }
        if(input.isBomb()){ // if user pressed 'SPACE'
            if(player.requestBomb(player.getPosition())){ // check if player can drop a bomb
                createABomb(player.getPosition(), now);
            }
        }
        input.clear();
    }

    private void showMessage(String msg, Color color) {
        Text waitingForKey = new Text(msg);
        waitingForKey.setTextAlignment(TextAlignment.CENTER);
        waitingForKey.setFont(new Font(60));
        waitingForKey.setFill(color);
        StackPane root = new StackPane();
        root.getChildren().add(waitingForKey);
        Scene scene = new Scene(root, 400, 200, Color.WHITE);
        stage.setTitle(windowTitle);
        stage.setScene(scene);
        input = new Input(scene);
        stage.show();
        new AnimationTimer() {
            public void handle(long now) {
                processInput(now);
            }
        }.start();
    }


    private void update(long now) {
        player.update(now);
        updateBombs();
        updateGameObjects();
        if (!player.isAlive()) {
            gameLoop.stop();
            showMessage("Perdu!", Color.RED);
        }
        if (player.isWinner()) {
            gameLoop.stop();
            showMessage("Gagné", Color.BLUE);
        }
        if(player.isEnteringDoor()){ // if the player entered a door (boolean)
            game.loadDoor(player.getPosition()); // load the next level associated with the door and set the player position
            changeStage();
            player.exitDoor(); // reset the boolean to false because the player when completely through the door
        }
        if(player.isCollecting()){
            refreshSprites();
            player.endCollecting();
        }


    }

    private void render() {
        sprites.forEach(Sprite::render);
        renderGameObject();
        renderBombs(); // explosion is over the player
        // last rendering to have player in the foreground
        spritePlayer.render();
        if(player.isInvincible()){
            spritePlayer.getImageView().setOpacity(0.25d);
        }else{
            spritePlayer.getImageView().setOpacity(1);
        }

    }

    /**
     * Render all the gameObjects
     */
    private void renderGameObject(){
        for(Sprite s : gameObjectsSprites.values())
            s.render();
    }

    /**
     * Update the state and sprites of GameObjects
     */
    private void updateGameObjects(){
        Iterator<Map.Entry<GameObject, Sprite>> iterator = gameObjectsSprites.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<GameObject, Sprite> entry = iterator.next();
            if(!entry.getKey().isAlive()){
                entry.getValue().remove();
                iterator.remove();
            }
        }
    }


    public void start() {
        gameLoop.start();
    }


    /**
     * Remove from screen and delete all the sprites from sprites
     */
    private void deleteAllSprites(){
        Iterator<Sprite> iterator = sprites.iterator();
        while(iterator.hasNext()){
            iterator.next().remove();
            iterator.remove();
        }
    }

    /**
     * Instantiate a Bomb object and add the the sprite to the screen
     * @param position Position where instantiated
     * @param now Time in Nanoseconds when instantiated
     */
    private void createABomb(Position position, long now){
        Bomb bomb = new Bomb(game, position, now, player.getBombRange());
        ArrayList<Sprite> bombSprites = new ArrayList<>(); // list for sprites of the bomb
        bombSprites.add(SpriteFactory.createBomb(layer, bomb));
        bomb_map.put(bomb, bombSprites);
    }


    /**
     * Update the Sprite of the bombs
     */
    private void updateBombs(/*long now*/){
        Iterator<Bomb> iterator = bomb_map.keySet().iterator();
        while(iterator.hasNext()){
            Bomb b = iterator.next();
            if(b.isExplosionState()){
                b.explode();
                createExplosionSprites(b);
            }
            if(b.isExplosionOver()){
                deleteSpriteBombs(b);
                iterator.remove();
            }
        }
    }

    /**
     * Render on the screen all the bombs
     */
    private void renderBombs(){
        for (Map.Entry<Bomb, ArrayList<Sprite>> entry : bomb_map.entrySet()) {
            if (entry.getKey().getLevel() == game.getLevel()) {
                entry.getValue().forEach(Sprite::render);
            }
        }
    }

    /**
     * remove the bombs sprite that shouldn't be rendered
     */
    private void removeSpriteBomb(){
        for (Map.Entry<Bomb, ArrayList<Sprite>> entry : bomb_map.entrySet()) {
            if (entry.getKey().getLevel() != game.getLevel()) {
                entry.getValue().forEach(Sprite::remove);
            }
        }
    }

    /**
     * Delete and remove all the sprites for a given bomb
     * @param bomb Bomb
     */
    private void deleteSpriteBombs(Bomb bomb){
       ArrayList<Sprite> bombSprites = bomb_map.get(bomb);
       bombSprites.forEach(Sprite::remove);
       bombSprites.clear();
    }


    /**
     * Create the explosion sprites of the bomb and add it to the list of sprites
     * of bomb_map
     * @param b bomb that you want to add the explosion sprite to
     */
    private void createExplosionSprites(Bomb b){
        for(Position p : b.getBlast()){
           bomb_map.get(b).add(SpriteFactory.createExplosion(layer, p));
        }
    }

    /**
     * Refresh all the sprites of sprites (Decor)
     */
    private void refreshSprites(){
        deleteAllSprites();
        game.getWorld().forEach( (pos,d) -> sprites.add(SpriteFactory.createDecor(layer, pos, d)));
    }








}
