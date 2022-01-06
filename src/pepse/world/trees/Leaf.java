package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.world.Block;

import java.awt.*;
import java.util.Random;

public class Leaf extends Block {

    public static final Color LEAF_COLOR = new Color(50, 200, 30);
    private final Random rand;

    private Vector2 topLeftCorner;
    private Transition<Float> horizontalTransition;
    private Transition<Float> angleTransition;
    private Transition<Vector2> dimensionsTransition;

    public Leaf(Vector2 topLeftCorner) {
        super(topLeftCorner, new RectangleRenderable(LEAF_COLOR));
        physics().setMass(1);
        this.topLeftCorner = topLeftCorner;
        this.rand = new Random();
        // transitions leaf angles
        new ScheduledTask(this, rand.nextFloat(2), false,
                this::changeAngle);
        // tansitions leaf size
        new ScheduledTask(this, rand.nextFloat(2), false,
                this::changeDimensions);
        // drops leaves
        new ScheduledTask(this, rand.nextFloat(100) + 20, false, this::dropLeaf);
    }

    /**
     * initializes a transformation
     */
    private void changeDimensions() {
        this.dimensionsTransition = new Transition<>(this,
                this::setDimensions, Vector2.ONES.mult(Block.SIZE).mult(1.05f),
                new Vector2(Block.SIZE, Block.SIZE).mult(0.95f), Transition.CUBIC_INTERPOLATOR_VECTOR,
                1, Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);
    }

    /**
     * initializes a transformation
     */
    private void changeAngle() {
        this.angleTransition = new Transition<>(this,
                renderer()::setRenderableAngle, -10f, 10f, Transition.CUBIC_INTERPOLATOR_FLOAT,
                1, Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);
    }

    /**
     * makes leaf fall and fade out
     */
    private void dropLeaf(){
        transform().setVelocityY(100);
        this.horizontalTransition = new Transition<>(
                this, // the game object being changed
                transform()::setVelocityX, // the method to call
                 75f, // initial transition value
                - 75f, // final transition value
                Transition.LINEAR_INTERPOLATOR_FLOAT, // use a cubic interpolator
                1, // transition fully over a day
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null); // nothing further to execute upon reaching final value
        renderer().fadeOut(10, this::returnLeaf);
    }


    private void returnLeaf(){
        setTopLeftCorner(this.topLeftCorner);
        renderer().setOpaqueness(1);
        new ScheduledTask(this, rand.nextFloat(2), false,
                this::changeAngle);
        // tansitions leaf size
        new ScheduledTask(this, rand.nextFloat(2), false,
                this::changeDimensions);
        // drops leaves
        new ScheduledTask(this, rand.nextFloat(100) + 20, false, this::dropLeaf);

    }

    @Override
    public boolean shouldCollideWith(GameObject other) {
        if(other.getTag().equals("groundBlock")) {
            return super.shouldCollideWith(other);
        }
        return false;
    }

    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        this.setVelocity(Vector2.ZERO);
        removeComponent(horizontalTransition);
        removeComponent(dimensionsTransition);
        removeComponent(angleTransition);
    }
}
