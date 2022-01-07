package pepse.world;

import danogl.GameObject;
import danogl.components.GameObjectPhysics;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

public class Block extends GameObject {

    public static final int SIZE = 30;
    public Block(Vector2 topLeftCorner, Renderable renderable) {
        super(topLeftCorner, Vector2.ONES.mult(SIZE), renderable);
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
    }

    public static int round(float coordinate){
        return (int)(coordinate/SIZE) * SIZE;
    }

    @Override
    public boolean shouldCollideWith(GameObject other) {
        if(this.getTag().equals("groundBlockLow")){
            return false;
        }
        return super.shouldCollideWith(other);
    }
}
