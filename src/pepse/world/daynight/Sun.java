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

    public static GameObject create(
            Vector2 windowDimensions,
            float cycleLength,
            GameObjectCollection gameObjects,
            int layer){
        GameObject sun = new GameObject(
                Vector2.ZERO, new Vector2(SUN_SIZE, SUN_SIZE),
                new OvalRenderable(SUN_COLOR));
        sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        Vector2 initialSunCenter = windowDimensions.multX(0.5f).multY(INITIAL_HEIGHT_RATIO);
        sun.setCenter(initialSunCenter);
        gameObjects.addGameObject(sun, layer);
        sun.setTag("sun");
        Transition<Float> nightTransition = new Transition<>(
                sun, //the game object being changed
                (Float angle) -> sun.setCenter(initialSunCenter.subtract(windowDimensions.multX(0.5f).multY(HORIZONTAL_AXIS_RATIO)).rotated(angle).multX(X_RADIUS_RATIO).add(windowDimensions.multX(0.5f).multY(HORIZONTAL_AXIS_RATIO))),  //the method to call
                0f,    //initial transition value
                360f,   //final transition value
                Transition.LINEAR_INTERPOLATOR_FLOAT,  //use a cubic interpolator
                cycleLength/2,   //transtion fully over half a day
                Transition.TransitionType.TRANSITION_LOOP,
                null);  //nothing further to execute upon reaching final value
        return sun;
    }
}
