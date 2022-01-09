package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;

import java.awt.*;

public class Sun {
    private static final Color SUN_COLOR = Color.YELLOW;
    private static final int SUN_SIZE = 100;
    private static final float INITIAL_HEIGHT_RATIO = 0.1f;
    private static final float X_RADIUS_RATIO = 1.01f;
    private static final float HORIZONTAL_AXIS_RATIO = 0.95f;
    public static final String SUN = "sun";
    public static final float FULL_CYCLE = 360f;
    public static final float INITIAL_X_RATIO = 0.5f;

    /**
     * This function creates a yellow circle that moves in the sky in an elliptical path (in camera coordinates).
     * @param windowDimensions The dimensions of the windows.
     * @param cycleLength The amount of seconds it should take the created game object to complete a full cycle.
     * @param gameObjects The collection of all participating game objects.
     * @param layer The number of the layer to which the created sun should be added.
     * @return A new game object representing the sun.
     */
    public static GameObject create(
            GameObjectCollection gameObjects,
            int layer,
            Vector2 windowDimensions,
            float cycleLength){
        GameObject sun = new GameObject(
                Vector2.ZERO, new Vector2(SUN_SIZE, SUN_SIZE),
                new OvalRenderable(SUN_COLOR));
        sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        Vector2 initialSunCenter = windowDimensions.multX(INITIAL_X_RATIO).multY(INITIAL_HEIGHT_RATIO);
        sun.setCenter(initialSunCenter);
        gameObjects.addGameObject(sun, layer);
        sun.setTag(SUN);
        Vector2 screenCenter = windowDimensions.multX(INITIAL_X_RATIO).multY(HORIZONTAL_AXIS_RATIO);
        Transition<Float> nightTransition = new Transition<>(
                sun, //the game object being changed
                (Float angle) -> sun.setCenter(initialSunCenter.subtract(screenCenter).rotated(angle).multX(X_RADIUS_RATIO).add(screenCenter)),  //the method to call
                0f,    //initial transition value
                FULL_CYCLE,   //final transition value
                Transition.LINEAR_INTERPOLATOR_FLOAT,  //use a cubic interpolator
                cycleLength/2,   //transtion fully over half a day
                Transition.TransitionType.TRANSITION_LOOP,
                null);  //nothing further to execute upon reaching final value
        return sun;
    }
}
