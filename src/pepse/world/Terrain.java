package pepse.world;

import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.util.NoiseGenerator;

import java.awt.*;

public class Terrain {

    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    private static final int TERRAIN_DEPTH = 20;

    private final NoiseGenerator noiseGenerator;
    private final double noiseAtX0;
    private float groundHeightAtX0;
    private GameObjectCollection gameObjects;
    private int groundLayer;

    public Terrain(GameObjectCollection gameObjects, int groundLayer, Vector2 windowDimensions, int seed) {
        this.gameObjects = gameObjects;
        this.groundLayer = groundLayer;
        this.groundHeightAtX0 = windowDimensions.y() * (2f / 3);
        this.noiseGenerator = new NoiseGenerator(seed, (int)groundHeightAtX0);
        this.noiseAtX0 = noiseGenerator.noise(0);

    }

    public float groundHeightAt(float x) {
        return (float) (groundHeightAtX0 + (5 * Block.SIZE * noiseGenerator.noise(x)));
    }

    public void createInRange(int minX, int maxX) {
        int x = minX;
        while (x <= maxX){
            float y = (float) (Math.floor(groundHeightAt(x) / Block.SIZE) * Block.SIZE);
            createColumn(x, y);
            x += Block.SIZE;
        }
    }

    /**
     * creates a column of ground begining at coordinates
     * @param x x coordinate
     * @param y y coordinate
     */
    private void createColumn(int x, float y) {
        for (int i = 0; i < TERRAIN_DEPTH; i++) {
            Block groundBlock = new Block(new Vector2(x, y + (i * Block.SIZE)),
                    new RectangleRenderable(BASE_GROUND_COLOR));
            gameObjects.addGameObject(groundBlock, groundLayer);
        }
    }

    public static void main(String[] args) {

    }
}



