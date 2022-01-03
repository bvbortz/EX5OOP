package pepse.world.trees;

import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.world.Block;

import java.awt.*;
import java.util.Random;

public class Leaf extends Block {

    public static final Color LEAF_COLOR = new Color(50, 200, 30);

    public Leaf(Vector2 topLeftCorner) {
        super(topLeftCorner, new RectangleRenderable(LEAF_COLOR));
        Random rand = new Random();
        // transitions leaf angles
        new ScheduledTask(this, rand.nextFloat(2), false,
                this::changeAngle);
        // transitions leaf size
        new ScheduledTask(this, rand.nextFloat(2), false,
                this::changeDimensions);
        // drops leaves

        //this.renderer().fadeOut(3);

    }

    /**
     * initializes a transformation
     */
    private void changeDimensions() {
        new Transition<>(this,
                this::setDimensions, Vector2.ONES.mult(Block.SIZE).mult(1.05f),
                new Vector2(Block.SIZE, Block.SIZE).mult(0.95f), Transition.CUBIC_INTERPOLATOR_VECTOR,
                1, Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);
    }

    /**
     * initializes a transformation
     */
    private void changeAngle() {
        new Transition<>(this,
                renderer()::setRenderableAngle, -10f, 10f, Transition.CUBIC_INTERPOLATOR_FLOAT,
                1, Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);
    }

    private void dropLeaf(){
    }
}
