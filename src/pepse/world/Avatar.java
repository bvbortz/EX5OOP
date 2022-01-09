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

    private static final int SIZE = 50;
    private static final float SPEED = 300;
    public static final float TIME_BETWEEN_CLIPS = 0.3f;
    public static final int GRAVITY_ACCELERATION = 500;
    public static final int AVATAR_MASS = 5;
    public static final int INITIAL_VERTICAL_SPEED = 100;
    public static final int INITIAL_ENERGY = 100;
    public static final double ENERGY_LOSS = 0.5;
    ;
    public static final int PENETRATION_THRESHOLD = 40;
    private AnimationRenderable walk;
    private UserInputListener inputListener;
    private boolean isRight;
    private Renderable[] animation;
    private Renderable notMoving;
    private ImageReader imageReader;
    private float energy;

    /**
     * Constructor
     *
     * @param topLeftCorner as in super
     * @param dimensions    as in super
     * @param renderable    as in super
     * @param inputListener to receive input from user
     * @param imageReader   to create renderables
     */
    public Avatar(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable,
                  UserInputListener inputListener, ImageReader imageReader) {
        super(topLeftCorner, dimensions, renderable);
        this.inputListener = inputListener;
        this.imageReader = imageReader;
        transform().setAccelerationY(GRAVITY_ACCELERATION); // creates affect of gravity
        physics().setMass(AVATAR_MASS);
        transform().setVelocityY(-INITIAL_VERTICAL_SPEED);
        energy = INITIAL_ENERGY;
        initiateAnimation(renderable, imageReader); // creates walk animation
        this.physics().preventIntersectionsFromDirection(Vector2.ZERO);
    }

    /**
     * creates walk and static animation
     *
     * @param renderable  static renderable
     * @param imageReader imageReader for creating renderables
     */
    private void initiateAnimation(Renderable renderable, ImageReader imageReader) {
        this.notMoving = renderable;
        animation = new Renderable[2];
        Renderable rightLeg = imageReader.readImage("assets/Male-Mage2.png", true);
        Renderable leftLeg = imageReader.readImage("assets/Male-Mage1.png", true);
        animation[0] = rightLeg;
        animation[1] = leftLeg;
        this.walk = new AnimationRenderable(animation, TIME_BETWEEN_CLIPS);
    }

    /**
     * moves avatar according to user input/energy/gravity
     *
     * @param deltaTime as in super
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        float xVelocity = moveRightLeft();  // horizontal movement
        renderer().setIsFlippedHorizontally(!isRight); // flips avatar according to direction
        transform().setVelocityX(xVelocity);
        jumpOrFly(); // handles flight/jump
        if (getVelocity().equals(Vector2.ZERO) && xVelocity == 0) {  // static
            renderer().setRenderable(notMoving);
        }

    }

    /**
     * handles flight and jumping
     */
    private void jumpOrFly() {
        // if input is flight or jump
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
            // if landed replenish energy
        } else if (energy < INITIAL_ENERGY && getVelocity().y() == 0) {
            energy += ENERGY_LOSS;
        }
    }

    /**
     * handles horizontal movement
     *
     * @return movement velocity
     */
    private float moveRightLeft() {
        float xVelocity = 0;  // movement velocity
        // if input is right
        if (inputListener.isKeyPressed(KeyEvent.VK_RIGHT)) {
            renderer().setRenderable(walk);
            isRight = true;
            xVelocity += SPEED;
        }
        // if input is left
        if (inputListener.isKeyPressed(KeyEvent.VK_LEFT)) {
            renderer().setRenderable(walk);
            isRight = false;
            xVelocity -= SPEED;
        }
        return xVelocity;
    }

    /**
     * ensures that avatar does not fall through objects
     *
     * @param other     as in super
     * @param collision as in super
     */
    @Override
    public void onCollisionStay(GameObject other, Collision collision) {
        super.onCollisionStay(other, collision);
        checkPenetration(other, collision);
    }

    /**
     * checks if avatar has fallen through another object and resets it
     *
     * @param other     the other object
     * @param collision info on the collision
     */
    private void checkPenetration(GameObject other, Collision collision) {
        if (other.getTag().equals(Terrain.GROUND_BLOCK) && collision.getPenetrationArea().magnitude() > PENETRATION_THRESHOLD) {
            transform().setCenterY(getCenter().y() - Block.SIZE / 2);
        }
    }

    /**
     * sets that avatar should collide with top layers of ground blocks
     * @param other as in super
     * @return as in super
     */
    @Override
    public boolean shouldCollideWith(GameObject other) {
        return super.shouldCollideWith(other) || other.getTag().equals(Terrain.GROUND_BLOCK);
    }

    /**
     * on collision with ground block avatar stops falling
     * @param other as in super
     * @param collision as in super
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        if (other.getTag().equals(Terrain.GROUND_BLOCK)) {
            transform().setVelocityY(0);
            checkPenetration(other, collision);
        }
        super.onCollisionEnter(other, collision);
    }


    /**
     * creates an avatar
     * @param gameObjects the objects of the game
     * @param layer the layer of the avatar
     * @param topLeftCorner top left coordinates of avatar
     * @param inputListener for user input
     * @param imageReader for creating renderables
     * @return the avatar
     */
    public static Avatar create(GameObjectCollection gameObjects,
                                int layer, Vector2 topLeftCorner,
                                UserInputListener inputListener,
                                ImageReader imageReader) {
        Renderable notMoving = imageReader.readImage("assets/Male-Mage.png", true);
        Avatar avatar = new Avatar(topLeftCorner, Vector2.ONES.mult(SIZE), notMoving, inputListener, imageReader);
        gameObjects.addGameObject(avatar, layer);
        return avatar;

    }
}
