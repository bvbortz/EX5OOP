package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.util.Vector2;
import pepse.world.Sky;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;

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
        System.out.printf("screen: x-%f, y-%f", windowController.getWindowDimensions().x(), windowController.getWindowDimensions().y());
        Sky.create(gameObjects(), windowController.getWindowDimensions(), Layer.BACKGROUND);
        Night.create(gameObjects(), windowController.getWindowDimensions(), 10f, Layer.FOREGROUND);
        sun = Sun.create(windowController.getWindowDimensions(), 20, gameObjects(), Layer.BACKGROUND+1);
    }
}
