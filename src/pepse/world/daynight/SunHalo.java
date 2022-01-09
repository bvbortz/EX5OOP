package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;

import java.awt.*;

public class SunHalo {

    public static final String SUN_HALO = "sun halo";
    public static final float SIZE_FACTOR = 2f;

    /**
     * This function creates a halo around a given object that represents the sun. The halo will be tied to
     * the given sun, and will always move with it.
     * @param gameObjects The collection of all participating game objects.
     * @param sun A game object representing the sun (it will be followed by the created game object).
     * @param color The color of the halo.
     * @param layer  The number of the layer to which the created halo should be added.
     * @return A new game object representing the sun's halo.
     */
    public static GameObject create(
            GameObjectCollection gameObjects,
            int layer,
            GameObject sun,
            Color color){
        GameObject sunHalo = new GameObject(Vector2.ZERO, sun.getDimensions().mult(SIZE_FACTOR),
                new OvalRenderable(color));
        sunHalo.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sunHalo.setTag(SUN_HALO);
        sunHalo.addComponent((float deltaTime) -> sunHalo.setCenter(sun.getCenter()));
        gameObjects.addGameObject(sunHalo, layer);
        return sunHalo;
    }
}
