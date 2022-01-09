package pepse.world;

import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;

import java.util.HashMap;

/**
 * a class to handle objects that are removed throughout the game
 */
public abstract class RemovableObjects {
    protected GameObjectCollection gameObjects;
    // maps an x coordinate to blocks in that coordinate, and the blocks are mapped to their layer
    protected HashMap<Integer, HashMap<Block, Integer>> mapXtoBlocksToLayer = new HashMap<>();

    /**
     * adds a block to the map
     *
     * @param x     the x coordinate
     * @param layer the layer
     * @param block the block
     */
    protected void addToMap(int x, int layer, Block block) {
        // if x coordinate is already in map and block is not then add
        if (mapXtoBlocksToLayer.containsKey(x)) {
            if (!mapXtoBlocksToLayer.get(x).containsKey(block)) {
                mapXtoBlocksToLayer.get(x).put(block, layer);
            }
        } else { // else create new map with block and add to original map
            HashMap<Block, Integer> blocksToLayers = new HashMap<>();
            blocksToLayers.put(block, layer);
            mapXtoBlocksToLayer.put(x, blocksToLayers);
        }
    }

    /**
     * removes all blocks that are not in range
     * @param minX the min x coordinate of the range
     * @param maxX the max x coordinate of the range
     */
    protected void removeFromMap(int minX, int maxX) {
        // loops over all x coordinates and checks if in range
        for (var xToMapEntry : mapXtoBlocksToLayer.entrySet()) {
            if (xToMapEntry.getKey() < minX || xToMapEntry.getKey() > maxX) {
                // if not in range loops over all blocks in x coordinate and removes them
                for (var blockToLayerEntry : xToMapEntry.getValue().entrySet()) {
                    Block block = blockToLayerEntry.getKey();
                    int layer = blockToLayerEntry.getValue();
                    if (!gameObjects.removeGameObject(block, layer)) {
                        gameObjects.removeGameObject(block, Layer.DEFAULT);
                    }
                }
            }
        }
        // removes blocks that were removed from game from map
        mapXtoBlocksToLayer.entrySet().removeIf(entry -> entry.getKey() < minX || entry.getKey() > maxX);
    }
}
