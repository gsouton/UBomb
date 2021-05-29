/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.view.sprite;

import static fr.ubx.poo.view.image.ImageResource.*;


import fr.ubx.poo.game.Position;
import fr.ubx.poo.model.decor.*;
import fr.ubx.poo.model.decor.collectable.Key;
import fr.ubx.poo.model.decor.collectable.Princess;
import fr.ubx.poo.model.decor.collectable.bonus.*;
import fr.ubx.poo.model.go.Bomb;
import fr.ubx.poo.model.go.GameObject;
import fr.ubx.poo.model.go.character.Monster;
import fr.ubx.poo.model.go.character.Player;
import fr.ubx.poo.model.go.Box;
import fr.ubx.poo.view.image.ImageFactory;
import javafx.scene.layout.Pane;


public final class SpriteFactory {

    public static Sprite createDecor(Pane layer, Position position, Decor decor) {
        ImageFactory factory = ImageFactory.getInstance();
        if (decor instanceof Stone)
            return new SpriteDecor(layer, factory.get(STONE), position);
        if (decor instanceof Tree)
            return new SpriteDecor(layer, factory.get(TREE), position);
        if(decor instanceof Key)
            return new SpriteDecor(layer, factory.get(KEY), position);
        if(decor instanceof Heart)
            return new SpriteDecor(layer, factory.get(HEART), position);
        if(decor instanceof Princess)
            return new SpriteDecor(layer, factory.get(PRINCESS), position);
        if(decor instanceof BonusBombNbDec)
            return new SpriteDecor(layer, factory.get(BONUS_BOMB_NB_DEC), position);
        if(decor instanceof BonusBombNbInc)
            return new SpriteDecor(layer, factory.get(BONUS_BOMB_NB_INC), position);
        if(decor instanceof BonusBombRangeDec)
            return new SpriteDecor(layer, factory.get(BONUS_BOMB_RG_DEC), position);
        if(decor instanceof BonusBombRangeInc)
            return new SpriteDecor(layer, factory.get(BONUS_BOMB_RG_INC), position);
        if (decor instanceof Door) {
        	if (((Door)decor).isOpen())
        		return new SpriteDecor(layer, factory.get(DOOR_OPENED), position);
        	return new SpriteDecor(layer, factory.get(DOOR_CLOSED), position);
        }
        throw new RuntimeException("Unsupported sprite for decor " + decor);
    }

    public static Sprite createPlayer(Pane layer, Player player) {
        return new SpritePlayer(layer, player);
    }

    //public static Sprite createMonster(Pane layer, Monster monster)

    public static Sprite createBomb(Pane layer, Bomb bomb){ return new SpriteBomb(layer, bomb); }

    //public static Sprite createBox(Pane layer, Box box){ return new SpriteBox(layer, box);}

    public static Sprite createGameObject(Pane layer, GameObject go){
        if(go instanceof Monster){
            return new SpriteMonster(layer, (Monster) go);
        }
        if(go instanceof Box){
            return  new SpriteBox(layer, (Box) go);
        }
        throw new RuntimeException("Unsupported sprite for GameObject " + go);
    }

    public static Sprite createExplosion(Pane layer, Position position){
        ImageFactory factory = ImageFactory.getInstance();
        return new SpriteDecor(layer, factory.get(EXPLOSION), position);
    }

}
