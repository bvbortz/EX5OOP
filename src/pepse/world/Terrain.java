package pepse.world;

import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.util.NoiseGenerator;

import java.awt.*;

/**
 * creates the ground for the game - inherits from removableObject as the terrain is removed and
 * added again based on location of avatar
 */
public class Terrain extends RemovableObjects{

    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    private static final int TERRAIN_DEPTH = 20;
    public static final float INITIAL_HEIGHT_RATIO = 3f / 4;
    public static final int NOISE_AMPLITUDE = 5;
    public static final String GROUND_BLOCK = "groundBlock";
    public static final String GROUND_BLOCK_LOW = "groundBlockLow";
    public static final int TOP_LAYERS = 2;
    private final NoiseGenerator noiseGenerator;
    private float groundHeightAtX0;
    private int groundLayer;

    /**
     * constructor
     * @param gameObjects the objects in the game
     * @param windowDimensions dimensions of the window of the game
     * @param groundLayer layer of the ground
     * @param seed seed for random
     */
    public Terrain(GameObjectCollection gameObjects, int groundLayer, Vector2 windowDimensions, int seed) {
        this.gameObjects = gameObjects;
        this.groundLayer = groundLayer;
        this.groundHeightAtX0 = windowDimensions.y() * INITIAL_HEIGHT_RATIO; // height of ground at 0
        this.noiseGenerator = new NoiseGenerator(seed, (int)groundHeightAtX0); // noise function for ground

    }

    /**
     * receives an x coordinate and returns what the height should be, based on the noise function
     * @param x the coordinate
     * @return what height should be
     */
    public float groundHeightAt(float x) {
        // multiply to make more noticeable
        return (float) (groundHeightAtX0 + (NOISE_AMPLITUDE * Block.SIZE * noiseGenerator.noise(x)));
    }

    /**
     * creates ground in a certain range
     * @param minX the minimum x coordinate
     * @param maxX the maximum x coordinate
     */
    public void createInRange(int minX, int maxX) {
        // so whole game can know where columns start and end
        minX = Block.round(minX);
        maxX = Block.round(maxX);
        removeFromMap(minX, maxX); // removes what's not in range
        for (int x = minX; x <= maxX; x+= Block.SIZE) {  // creates each column of ground
            float y = Block.round(groundHeightAt(x));
            createColumn(x, y);
        }
    }

    /**
     * creates a column of ground beginning at coordinates
     * @param x x coordinate
     * @param y y coordinate
     */
    private void createColumn(int x, float y) {
        for (int i = 0; i < TERRAIN_DEPTH; i++) {
            Block groundBlock = new Block(new Vector2(x, y + (i * Block.SIZE)),
                    new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR)));
            addToMap(x, groundLayer, groundBlock);
            // sets tags for top and lower layers of ground
            if(i <= TOP_LAYERS){
                groundBlock.setTag(GROUND_BLOCK);
            }
            else{
                groundBlock.setTag(GROUND_BLOCK_LOW);
            }
            gameObjects.addGameObject(groundBlock, groundLayer);

        }
    }

}



