package pepse.world;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.event.KeyEvent;
import java.security.Key;

public class Avatar extends GameObject {
    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param renderable    The renderable representing the object. Can be null, in which case
     */
    private static final int SIZE = 50;
    private static final float SPEED = 300;
    private UserInputListener inputListener;

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        float xVelocity = 0;
        if(inputListener.isKeyPressed(KeyEvent.VK_RIGHT)){
            xVelocity += SPEED;
        }
        if(inputListener.isKeyPressed(KeyEvent.VK_LEFT)){
            xVelocity -= SPEED;
        }
        transform().setVelocityX(xVelocity);
        if(inputListener.isKeyPressed(KeyEvent.VK_SPACE)){
            if(getVelocity().y() == 0){
                transform().setVelocityY(-SPEED);
            }
        }

    }

    @Override
    public void onCollisionStay(GameObject other, Collision collision) {
        super.onCollisionStay(other, collision);
        if(other.getTag().equals("groundBlock") && collision.getPenetrationArea().magnitude() >  40){
            transform().setCenterY(getCenter().y() - Block.SIZE/2);
        }
    }

    @Override
    public boolean shouldCollideWith(GameObject other) {
        return super.shouldCollideWith(other) || other.getTag().equals("groundBlock");
    }

    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        if(other.getTag().equals("groundBlock")){
            transform().setVelocityY(0);
        }
        super.onCollisionEnter(other, collision);
    }

    public Avatar(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable,
                  UserInputListener inputListener) {
        super(topLeftCorner, dimensions, renderable);
        this.inputListener = inputListener;
        transform().setAccelerationY(500);
        physics().setMass(5);
        this.physics().preventIntersectionsFromDirection(Vector2.ZERO);
    }

    public static Avatar create(GameObjectCollection gameObjects,
                                int layer, Vector2 topLeftCorner,
                                UserInputListener inputListener,
                                ImageReader imageReader){
        Avatar avatar = new Avatar(topLeftCorner, Vector2.ONES.mult(SIZE),
                imageReader.readImage("assets/Male-Mage.png", true), inputListener);
        gameObjects.addGameObject(avatar);
        return avatar;

    }
}
