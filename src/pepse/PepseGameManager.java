package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.gui.rendering.Camera;
import danogl.util.Vector2;
import pepse.world.Avatar;
import pepse.world.Sky;
import pepse.world.Terrain;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;
import pepse.world.daynight.SunHalo;
import pepse.world.trees.Tree;

import java.awt.*;

public class PepseGameManager extends GameManager {
    public static final int seed = 60;
    public static final float NIGHT_CYCLE = 10f;
    public static final int SUN_CYCLE = 20;
    private int rightEdge;
    private int leftEdge;
    private GameObject sun;
    private Terrain terrain;
    private Tree tree;
    private Avatar avatar;
    private int windowWidth;

    /**
     * adds or removes terrain and trees based on movement of avatar
     *
     * @param deltaTime as in super
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        // if avatar moves from center
        if (avatar.getCenter().x() - leftEdge <= windowWidth / 2f ||
                rightEdge - avatar.getCenter().x() <= windowWidth / 2f) {
            // adds/removes the game objects as relevant to avatar location
            leftEdge = (int) avatar.getCenter().x() - windowWidth;
            rightEdge = (int) avatar.getCenter().x() + windowWidth;
            terrain.createInRange(leftEdge, rightEdge);
            tree.createInRange(leftEdge, rightEdge);

        }

    }

    public static void main(String[] args) {
        new PepseGameManager().run();
    }

    /**
     * as in super
     *
     * @param imageReader
     * @param soundReader
     * @param inputListener
     * @param windowController
     */
    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader, UserInputListener inputListener, WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        windowController.setTargetFramerate(20);
        windowWidth = (int) windowController.getWindowDimensions().x();
        Sky.create(gameObjects(), windowController.getWindowDimensions(), Layer.BACKGROUND);
        Night.create(gameObjects(), Layer.FOREGROUND, windowController.getWindowDimensions(), NIGHT_CYCLE);
        sun = Sun.create(gameObjects(), Layer.BACKGROUND + 1, windowController.getWindowDimensions(), SUN_CYCLE);
        SunHalo.create(gameObjects(), Layer.BACKGROUND + 10, sun, new Color(255, 255, 0, 20));
        terrain = new Terrain(gameObjects(), Layer.STATIC_OBJECTS,
                windowController.getWindowDimensions(), seed);
        // creates terrain in rangetwice the size of the window
        terrain.createInRange(-windowWidth / 2, 3 * windowWidth / 2);
        tree = new Tree(gameObjects(), terrain::groundHeightAt, Layer.STATIC_OBJECTS, seed);
        // creates trees in range twice the size of the window
        tree.createInRange(-windowWidth / 2, 3 * windowWidth / 2);
        Vector2 initialAvatarLocation0 = new Vector2(windowController.getWindowDimensions().x() / 2, terrain.groundHeightAt(300) - 50);
        avatar = Avatar.create(gameObjects(), Layer.DEFAULT,
                initialAvatarLocation0, inputListener, imageReader);
        setCamera(new Camera(avatar, windowController.getWindowDimensions().mult(0.5f).subtract(initialAvatarLocation0),
                windowController.getWindowDimensions(),
                windowController.getWindowDimensions()));
        rightEdge = (int) windowController.getWindowDimensions().x();
        leftEdge = 0;
        gameObjects().layers().shouldLayersCollide(Layer.STATIC_OBJECTS, Layer.DEFAULT, true);

    }
}
