package pepse.world;

import danogl.GameObject;
import danogl.components.GameObjectPhysics;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * a basic block for building the game
 */
public class Block extends GameObject {

    public static final int SIZE = 30;

    /**
     * constructor
     * @param topLeftCorner as in super
     * @param renderable as in super
     */
    public Block(Vector2 topLeftCorner, Renderable renderable) {
        super(topLeftCorner, Vector2.ONES.mult(SIZE), renderable);
        // as instructed
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
    }

    /**
     * rounds coordinate to nearest block size
     * @param coordinate the coordinate to round
     * @return rounded coordinate
     */
    public static int round(float coordinate){
        return (int)(coordinate/SIZE) * SIZE;
    }

    /**
     * low layers of ground need not collide with any other object
     * @param other other object
     * @return false if low ground block otherwise as super
     */
    @Override
    public boolean shouldCollideWith(GameObject other) {
        if(this.getTag().equals(Terrain.GROUND_BLOCK_LOW)){
            return false;
        }
        return super.shouldCollideWith(other);
    }
}
