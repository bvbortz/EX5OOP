package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.util.Vector2;
import pepse.world.Avatar;
import pepse.world.Block;
import pepse.world.Sky;
import pepse.world.Terrain;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;
import pepse.world.daynight.SunHalo;
import pepse.world.trees.Tree;

import java.awt.*;

public class PepseGameManager extends GameManager {
    private GameObject sun;
    private float max_x=-1;
    private float min_x;
    private float min_y;
    private float max_y;
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        Vector2 pos = sun.getCenter();
        if(max_x == -1){
            max_x = pos.x();
            min_x = pos.x();
            max_y = pos.y();
            min_y = pos.y();
        }
        else if (max_x < pos.x())
            max_x = pos.x();
        else if (min_x > pos.x())
            min_x = pos.x();
        else if (max_y < pos.y())
            max_y = pos.y();
        else if (min_y > pos.y())
            min_y = pos.y();
        System.out.printf("x: %f-%f, y: %f-%f\n", min_x, max_x, min_y, max_y);
    }

    public static void main(String[] args) {
        new PepseGameManager().run();
    }

    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader, UserInputListener inputListener, WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        windowController.setTargetFramerate(95);
        System.out.printf("screen: x-%f, y-%f", windowController.getWindowDimensions().x(), windowController.getWindowDimensions().y());
        Sky.create(gameObjects(), windowController.getWindowDimensions(), Layer.BACKGROUND);
        Night.create(gameObjects(), windowController.getWindowDimensions(), 10f, Layer.FOREGROUND);
        sun = Sun.create(windowController.getWindowDimensions(), 20, gameObjects(), Layer.BACKGROUND+1);
        SunHalo.create(gameObjects(), sun, new Color(255, 255, 0, 20), Layer.BACKGROUND+10);
        Terrain terrain = new Terrain(gameObjects(), Layer.STATIC_OBJECTS,
                windowController.getWindowDimensions(), 60);
        terrain.createInRange(0, (int) windowController.getWindowDimensions().x());
        Tree tree = new Tree(gameObjects(), terrain::groundHeightAt, Layer.STATIC_OBJECTS);
        tree.createInRange(0, (int)windowController.getWindowDimensions().x());
        Avatar avatar = Avatar.create(gameObjects(), Layer.DEFAULT,
                new Vector2(300, 100), inputListener, imageReader);
        gameObjects().layers().shouldLayersCollide(Layer.STATIC_OBJECTS , Layer.DEFAULT, true);

    }
}
