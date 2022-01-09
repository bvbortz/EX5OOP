package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.world.Block;
import pepse.world.Terrain;

import java.awt.*;
import java.util.Objects;
import java.util.Random;

/**
 * Represents a leaf in the game
 */
public class Leaf extends Block {

    public static final Color LEAF_COLOR = new Color(50, 200, 30);
    public static final int TRANSITION_TIME = 1;
    public static final float BIGGER_DIM = 1.05f;
    public static final float SMALLER_DIM = 0.95f;
    public static final float ANGLE_RANGE = 10f;
    public static final int DROP_SPEED = 100;
    public static final float MOVEMENT_RANGE = 70f;
    public static final int FADE_OUT_TIME = 10;
    public static final int MOVEMENT_CYCLE = 2;
    public static final int FALLEN_LEAVES_CYCLE = 100;
    private final Random rand;

    private Vector2 topLeftCorner;
    private Transition<Float> horizontalTransition;
    private Transition<Float> angleTransition;
    private Transition<Vector2> dimensionsTransition;
    private GameObjectCollection gameObjects;
    private boolean leafFalling;

    /**
     * A constructor for Leaf class
     * @param topLeftCorner top left coordinates of leaf
     * @param gameObjects game objects of game
     * @param seed seed for random
     */
    public Leaf(Vector2 topLeftCorner, GameObjectCollection gameObjects, int seed) {
        super(topLeftCorner, new RectangleRenderable(LEAF_COLOR));
        this.gameObjects = gameObjects;
        physics().setMass(1);
        this.topLeftCorner = topLeftCorner;
        this.rand = new Random(Objects.hash(topLeftCorner.x(), topLeftCorner.y(), seed));
        leafFalling = false; // if leaf is currently falling
        scheduleTransitions();
    }

    /**
     * initializes a transformation to change the leaf's dimensions
     */
    private void changeDimensions() {
        this.dimensionsTransition = new Transition<>(this,
                this::setDimensions, Vector2.ONES.mult(Block.SIZE).mult(BIGGER_DIM),
                new Vector2(Block.SIZE, Block.SIZE).mult(SMALLER_DIM), Transition.CUBIC_INTERPOLATOR_VECTOR,
                TRANSITION_TIME, Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);
    }

    /**
     * initializes a transformation to change the leaf's angle
     */
    private void changeAngle() {
        this.angleTransition = new Transition<>(this,
                renderer()::setRenderableAngle, -ANGLE_RANGE, ANGLE_RANGE, Transition.CUBIC_INTERPOLATOR_FLOAT,
                TRANSITION_TIME, Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);
    }

    /**
     * makes leaf fall and fade out
     */
    private void dropLeaf(){
        leafFalling = true;
        gameObjects.removeGameObject(this, Layer.STATIC_OBJECTS); // falling leaf is no longer static
        gameObjects.addGameObject(this, Layer.DEFAULT); // adds moving leaf
        transform().setVelocityY(DROP_SPEED);
        // makes leaf drift horizontally
        this.horizontalTransition = new Transition<>(
                this, // the game object being changed
                transform()::setVelocityX, // the method to call
                MOVEMENT_RANGE, // initial transition value
                -MOVEMENT_RANGE, // final transition value
                Transition.LINEAR_INTERPOLATOR_FLOAT, // use a cubic interpolator
                TRANSITION_TIME, // transition fully over a day
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null); // nothing further to execute upon reaching final value
        renderer().fadeOut(FADE_OUT_TIME, this::returnLeaf);
    }


    /**
     * returns a fallen leaf to its original position and restarts the drop cycle
     */
    private void returnLeaf(){
        stopLeafMovement();
        leafFalling = false;
        renderer().setOpaqueness(1); // makes leaf visible
        setTopLeftCorner(this.topLeftCorner);
        // sets leaf as static again
        gameObjects.removeGameObject(this, Layer.DEFAULT);
        gameObjects.addGameObject(this, Layer.STATIC_OBJECTS);
        scheduleTransitions();  // restarts cycle

    }

    /**
     * sets the various leaf transitions
     */
    private void scheduleTransitions() {
        // transitions leaf angle
        new ScheduledTask(this, rand.nextFloat() * MOVEMENT_CYCLE, false,
                this::changeAngle);
        // transitions leaf size
        new ScheduledTask(this, rand.nextFloat() * MOVEMENT_CYCLE, false,
                this::changeDimensions);
        // drops leaves
        new ScheduledTask(this, rand.nextFloat() * FALLEN_LEAVES_CYCLE, false, this::dropLeaf);
    }

    /**
     * leaf should collide only with top layers of ground
     * @param other as in super
     * @return True if collides with ground block false otherwise
     */
    @Override
    public boolean shouldCollideWith(GameObject other) {
        if(other.getTag().equals(Terrain.GROUND_BLOCK) && leafFalling) {
            return super.shouldCollideWith(other);
        }
        return false;
    }

    /**
     * stops all leaf movement on collision with ground
     * @param other as in super
     * @param collision as in super
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        stopLeafMovement();
    }

    /**
     * stops all leave movement
     */
    private void stopLeafMovement() {
        this.setVelocity(Vector2.ZERO); // makes velocity zero
        // removes all transitions
        if(horizontalTransition != null){
            removeComponent(horizontalTransition);
            horizontalTransition = null;
        }
        if(dimensionsTransition != null){
            removeComponent(dimensionsTransition);
            dimensionsTransition = null;
        }
        if(angleTransition != null){
            removeComponent(angleTransition);
            angleTransition = null;
        }
    }

    /**
     * ensures leaf remains immobile after hitting ground
     * @param other as in super
     */
    @Override
    public void onCollisionExit(GameObject other) {
        super.onCollisionExit(other);
        // a way to make the leaves maintain their x position without moving horizontally
        transform().setVelocityY(DROP_SPEED / 2f);

    }
}
