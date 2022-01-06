package pepse.world;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.AnimationRenderable;
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
    public static final float TIME_BETWEEN_CLIPS = 1f / 60;
    private UserInputListener inputListener;
    private boolean isRight;
    private Renderable[] animation;
    private Renderable renderable;
    private ImageReader imageReader;
    private float energy;

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        float xVelocity = 0;
        String[] files = {"assets/Male-Mage1.png", "assets/Male-Mage2.png", "assets/Male-Mage2.png", "assets/Male-Mage1.png"};
        Renderable newRenderable = new  AnimationRenderable(files, imageReader, true, TIME_BETWEEN_CLIPS);
        if(inputListener.isKeyPressed(KeyEvent.VK_RIGHT)){

            //renderer().setRenderable(new AnimationRenderable(animation, TIME_BETWEEN_CLIPS));
            renderer().setRenderable(newRenderable);
            isRight = true;
            xVelocity += SPEED;
        }
        if(inputListener.isKeyPressed(KeyEvent.VK_LEFT)){
            renderer().setRenderable(newRenderable);
            isRight = false;
            xVelocity -= SPEED;
        }
        //renderer().setIsFlippedHorizontally(isRight);
        transform().setVelocityX(xVelocity);

        if(inputListener.isKeyPressed(KeyEvent.VK_SPACE)){
            if(inputListener.isKeyPressed(KeyEvent.VK_SHIFT) && energy > 0){
                renderer().setRenderable(newRenderable);
                transform().setVelocityY(-SPEED);
                energy -= 0.5;
            }
            if(getVelocity().y() == 0){
                renderer().setRenderable(newRenderable);
                transform().setVelocityY(-SPEED);
            }
        }
        else{
            energy += 0.5;
        }
        if(getVelocity().equals(Vector2.ZERO) && xVelocity == 0){
            renderer().setRenderable(renderable);
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
            if(collision.getPenetrationArea().magnitude() >  40){
                transform().setCenterY(getCenter().y() -Block .SIZE/2);
            }
        }
        super.onCollisionEnter(other, collision);
    }

    public Avatar(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable,
                  UserInputListener inputListener, ImageReader imageReader) {
        super(topLeftCorner, dimensions, renderable);
        this.inputListener = inputListener;
        this.imageReader = imageReader;
        transform().setAccelerationY(500);
        physics().setMass(5);
        transform().setVelocityY(-100);
        energy = 100;
        this.renderable = renderable;
        animation = new Renderable[2];
        animation[0] = imageReader.readImage("assets/Male-Mage1.png", true);
        animation[1] = imageReader.readImage("assets/Male-Mage2.png", true);
        this.physics().preventIntersectionsFromDirection(Vector2.ZERO);
    }

    public static Avatar create(GameObjectCollection gameObjects,
                                int layer, Vector2 topLeftCorner,
                                UserInputListener inputListener,
                                ImageReader imageReader){
        Avatar avatar = new Avatar(topLeftCorner, Vector2.ONES.mult(SIZE),
                imageReader.readImage("assets/Male-Mage.png", true), inputListener,
                imageReader);
        gameObjects.addGameObject(avatar, layer);
        return avatar;

    }
}
