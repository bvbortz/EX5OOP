package pepse.world;

import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.util.NoiseGenerator;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Terrain {

    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    private static final int TERRAIN_DEPTH = 20;
    private HashMap<Integer, HashMap<Block, Integer>> mapXtoBlocksToLayer = new HashMap<>();
    private final NoiseGenerator noiseGenerator;
    private float groundHeightAtX0;
    private GameObjectCollection gameObjects;
    private int groundLayer;

    public Terrain(GameObjectCollection gameObjects, int groundLayer, Vector2 windowDimensions, int seed) {
        this.gameObjects = gameObjects;
        this.groundLayer = groundLayer;
        this.groundHeightAtX0 = windowDimensions.y() * (3f / 4);
        this.noiseGenerator = new NoiseGenerator(seed, (int)groundHeightAtX0);

    }

    public float groundHeightAt(float x) {
        // multiply to make more noticeable
        return (float) (groundHeightAtX0 + (5 * Block.SIZE * noiseGenerator.noise(x)));
    }

    public void createInRange(int minX, int maxX) {
        // so whole game can know where columns start and end
        minX = Block.round(minX);
        maxX = Block.round(maxX);
        for(var elems : mapXtoBlocksToLayer.entrySet()){
            if(elems.getKey() < minX || elems.getKey() > maxX){
                for(var elems2 : elems.getValue().entrySet()){
                    gameObjects.removeGameObject(elems2.getKey(), elems2.getValue());
                }
            }
        }
        for (int x = minX; x <= maxX; x+= Block.SIZE) {  // creates each column of ground
            float y = Block.round(groundHeightAt(x));
            createColumn(x, y);
        }
    }

    /**
     * creates a column of ground begining at coordinates
     * @param x x coordinate
     * @param y y coordinate
     */
    private void createColumn(int x, float y) {
        HashMap<Block, Integer> blockToLayer = new HashMap<>();
        for (int i = 0; i < TERRAIN_DEPTH; i++) {
            Block groundBlock = new Block(new Vector2(x, y + (i * Block.SIZE)),
                    new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR)));
            blockToLayer.put(groundBlock, groundLayer);
            if(i <= 2){
                groundBlock.setTag("groundBlock");
                gameObjects.addGameObject(groundBlock, groundLayer);
            }
            else{
                groundBlock.setTag("groundBlockLow");
                gameObjects.addGameObject(groundBlock, groundLayer);
            }

        }
        mapXtoBlocksToLayer.put(x, blockToLayer);
    }

}



