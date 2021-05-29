/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.view.image;

import fr.ubx.poo.game.Direction;
import javafx.scene.image.Image;

import static fr.ubx.poo.view.image.ImageResource.*;

public final class ImageFactory {
    private final Image[] images;

    private final ImageResource[] directions = new ImageResource[]{
            // Direction { N, E, S, W }
            PLAYER_UP, PLAYER_RIGHT, PLAYER_DOWN, PLAYER_LEFT,
    };

    private final ImageResource[] monsterDirections = new ImageResource[]{
            MONSTER_UP, MONSTER_RIGHT, MONSTER_DOWN, MONSTER_LEFT,
    };

    /**
     * sprite of each state of bomb at second 1, 2, 3, and 4
     */
    private final ImageResource[] bombsState = new ImageResource[]{
            BOMB4, BOMB3, BOMB2, BOMB1,EXPLOSION,
    };

    private final ImageResource[] digits = new ImageResource[]{
            DIGIT_0, DIGIT_1, DIGIT_2, DIGIT_3, DIGIT_4,
            DIGIT_5, DIGIT_6, DIGIT_7, DIGIT_8, DIGIT_9,
    };

    private ImageFactory() {
        images = new Image[ImageResource.values().length];
    }

    /**
     * Point d'accès pour l'instance unique du singleton
     */
    public static ImageFactory getInstance() {
        return Holder.instance;
    }

    private Image loadImage(String file) {
        return new Image(getClass().getResource("/images/" + file).toExternalForm());
    }

    public void load() {
        for (ImageResource img : ImageResource.values()) {
            images[img.ordinal()] = loadImage(img.getFileName());
        }
    }

    public Image get(ImageResource img) {
        return images[img.ordinal()];
    }

    public Image getDigit(int i) {
        if (i < 0 || i > 9)
            throw new IllegalArgumentException();
        return get(digits[i]);
    }


    public Image getPlayer(Direction direction) {
        return get(directions[direction.ordinal()]);
    }

    public Image getMonster(Direction direction){
        return get(monsterDirections[direction.ordinal()]);
    }

    /**
     * @Brief   Return a sprite of a bomb at a given time
     * @param
     * @return
     */
    public Image getBomb(int i){
        if(i >= bombsState.length){
            return get(bombsState[bombsState.length-1]);
        }
        return get(bombsState[i]);}

    /**
     * @return  Return the number of frame for animation explosion of a bomb
     */
    public int nbBombFrames(){
        return bombsState.length;
    }


    /**
     * Holder
     */
    private static class Holder {
        /**
         * Instance unique non préinitialisée
         */
        private final static ImageFactory instance = new ImageFactory();
    }

}
