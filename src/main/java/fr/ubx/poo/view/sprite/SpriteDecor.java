package fr.ubx.poo.view.sprite;

import fr.ubx.poo.game.Position;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;


public class SpriteDecor extends Sprite {
    protected Position position;

    public SpriteDecor(Pane layer, Image image, Position position) {
        super(layer, image);
        this.position = position;
    }

    @Override
    public ColorAdjust updateImage() {

        return null;
    }

    @Override
    public Position getPosition() {
        return position;
    }
}
