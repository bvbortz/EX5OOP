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
import pepse.world.Block;
import pepse.world.Sky;
import pepse.world.Terrain;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;
import pepse.world.daynight.SunHalo;
import pepse.world.trees.Tree;

import java.awt.*;

public class PepseGameManager extends GameManager {
    public static final int seed = 60;
    private int rightEdge;
    private int leftEdge;
    private int lastXPos;
    private GameObject sun;
    private Terrain terrain;
    private Tree tree;
    private Avatar avatar;
    private WindowController windowController;
    private int windowWidth;

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if(avatar.getCenter().x() - leftEdge <= windowWidth / 2f ||
                rightEdge- avatar.getCenter().x() <= windowWidth / 2f){
            terrain.createInRange((int)avatar.getCenter().x()-windowWidth, (int)avatar.getCenter().x()+windowWidth);
            tree.createInRange((int)avatar.getCenter().x()-windowWidth, (int)avatar.getCenter().x()+windowWidth);
            leftEdge = (int)avatar.getCenter().x()-windowWidth;
            rightEdge = (int)avatar.getCenter().x()+windowWidth;
        }

    }

    public static void main(String[] args) {
        new PepseGameManager().run();
    }

    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader, UserInputListener inputListener, WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        windowController.setTargetFramerate(20);
        this.windowController= windowController;
        windowWidth = (int) windowController.getWindowDimensions().x();
        Sky.create(gameObjects(), windowController.getWindowDimensions(), Layer.BACKGROUND);
        Night.create(gameObjects(), Layer.FOREGROUND, windowController.getWindowDimensions(), 10f);
        sun = Sun.create(gameObjects(),Layer.BACKGROUND+1, windowController.getWindowDimensions(), 20);
        SunHalo.create(gameObjects(),Layer.BACKGROUND+10, sun, new Color(255, 255, 0, 20));
        terrain = new Terrain(gameObjects(), Layer.STATIC_OBJECTS,
                windowController.getWindowDimensions(), seed);
        terrain.createInRange(-windowWidth / 2, 3*windowWidth / 2);
        tree = new Tree(gameObjects(), terrain::groundHeightAt, Layer.STATIC_OBJECTS, seed);
        tree.createInRange(-windowWidth / 2, 3*windowWidth / 2);
        Vector2 initialAvatarLocation0 = new Vector2(windowController.getWindowDimensions().x() /2, terrain.groundHeightAt(300)-50);
        avatar = Avatar.create(gameObjects(), Layer.DEFAULT,
                initialAvatarLocation0, inputListener, imageReader);
//        Avatar avatar = Avatar.create(gameObjects(), Layer.DEFAULT,
//                new Vector2(300, 0), inputListener, imageReader);
        setCamera(new Camera(avatar, windowController.getWindowDimensions().mult(0.5f).subtract(initialAvatarLocation0) ,
                windowController.getWindowDimensions(),
                windowController.getWindowDimensions()));
        rightEdge = (int)windowController.getWindowDimensions().x();
        leftEdge = 0;
        lastXPos = (int) avatar.getCenter().x();
        gameObjects().layers().shouldLayersCollide(Layer.STATIC_OBJECTS , Layer.DEFAULT, true);

    }
}
