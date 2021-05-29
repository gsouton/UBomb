package fr.ubx.poo.view.sprite;

import fr.ubx.poo.model.go.Box;
import fr.ubx.poo.view.image.ImageFactory;
import fr.ubx.poo.view.image.ImageResource;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.layout.Pane;

public class SpriteBox extends SpriteGameObject{


    public SpriteBox(Pane layer, Box box) {
        super(layer, null, box);
    }

    @Override
    public ColorAdjust updateImage() {
        setImage(ImageFactory.getInstance().get(ImageResource.BOX));
        return null;
    }
}
