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

public class Avatar extends GameObject {
    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     * Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param renderable    The renderable representing the object. Can be null, in which case
     */
    private static final int SIZE = 50;
    private static final float SPEED = 300;
    public static final float TIME_BETWEEN_CLIPS = 0.3f;
    public static final int GRAVITY_ACCELERATION = 500;
    public static final int AVATAR_MASS = 5;
    public static final int INITIAL_VERTICAL_SPEED = 100;
    public static final int INITIAL_ENERGY = 100;
    public static final double ENERGY_LOSS = 0.5;;
    public static final int PENETRATION_THRESHOLD = 40;
    private AnimationRenderable walk;
    private UserInputListener inputListener;
    private boolean isRight;
    private Renderable[] animation;
    private Renderable notMoving;
    private ImageReader imageReader;
    private float energy;

    public Avatar(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable,
                  UserInputListener inputListener, ImageReader imageReader) {
        super(topLeftCorner, dimensions, renderable);
        this.inputListener = inputListener;
        this.imageReader = imageReader;
        transform().setAccelerationY(GRAVITY_ACCELERATION);
        physics().setMass(AVATAR_MASS);
        transform().setVelocityY(-INITIAL_VERTICAL_SPEED);
        energy = INITIAL_ENERGY;
        initiateAnimation(renderable, imageReader);
        this.physics().preventIntersectionsFromDirection(Vector2.ZERO);
    }

    private void initiateAnimation(Renderable renderable, ImageReader imageReader) {
        this.notMoving = renderable;
        animation = new Renderable[2];
        Renderable rightLeg = imageReader.readImage("assets/Male-Mage2.png", true);
        Renderable leftLeg = imageReader.readImage("assets/Male-Mage1.png", true);
        animation[0] = rightLeg;
        animation[1] = leftLeg;
        this.walk = new AnimationRenderable(animation, TIME_BETWEEN_CLIPS);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        float xVelocity = 0;
        xVelocity = moveRightLeft(xVelocity);
        renderer().setIsFlippedHorizontally(!isRight);
        transform().setVelocityX(xVelocity);
        jumpOrFly();
        if (getVelocity().equals(Vector2.ZERO) && xVelocity == 0) {
            renderer().setRenderable(notMoving);
        }

    }

    private void jumpOrFly() {
        if (inputListener.isKeyPressed(KeyEvent.VK_SPACE)) {
            if (inputListener.isKeyPressed(KeyEvent.VK_SHIFT) && energy > 0) {
                renderer().setRenderable(walk);
                transform().setVelocityY(-SPEED);
                energy -= ENERGY_LOSS;
            }
            if (getVelocity().y() == 0) {
                renderer().setRenderable(walk);
                transform().setVelocityY(-SPEED);
            }
        } else if(energy < INITIAL_ENERGY && getVelocity().y() == 0){
            energy += ENERGY_LOSS;
        }
    }

    private float moveRightLeft(float xVelocity) {
        if (inputListener.isKeyPressed(KeyEvent.VK_RIGHT)) {
            renderer().setRenderable(walk);
            isRight = true;
            xVelocity += SPEED;
        }
        if (inputListener.isKeyPressed(KeyEvent.VK_LEFT)) {
            renderer().setRenderable(walk);
            isRight = false;
            xVelocity -= SPEED;
        }
        return xVelocity;
    }


    @Override
    public void onCollisionStay(GameObject other, Collision collision) {
        super.onCollisionStay(other, collision);
        checkPenetration(other, collision);
    }

    private void checkPenetration(GameObject other, Collision collision) {
        if (other.getTag().equals(Terrain.GROUND_BLOCK) && collision.getPenetrationArea().magnitude() > PENETRATION_THRESHOLD) {
            transform().setCenterY(getCenter().y() - Block.SIZE / 2);
        }
    }

    @Override
    public boolean shouldCollideWith(GameObject other) {
        return super.shouldCollideWith(other) || other.getTag().equals(Terrain.GROUND_BLOCK);
    }

    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        if (other.getTag().equals(Terrain.GROUND_BLOCK)) {
            transform().setVelocityY(0);
            checkPenetration(other, collision);
        }
        super.onCollisionEnter(other, collision);
    }


    public static Avatar create(GameObjectCollection gameObjects,
                                int layer, Vector2 topLeftCorner,
                                UserInputListener inputListener,
                                ImageReader imageReader) {
        Renderable notMoving = imageReader.readImage("assets/Male-Mage.png", true);
        Avatar avatar = new Avatar(topLeftCorner, Vector2.ONES.mult(SIZE), notMoving ,inputListener, imageReader);
        gameObjects.addGameObject(avatar, layer);
        return avatar;

    }
}
