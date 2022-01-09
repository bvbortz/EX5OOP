package pepse.world;

import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;

import java.util.HashMap;

public abstract class RemovableObjects {
    protected GameObjectCollection gameObjects;
    protected HashMap<Integer, HashMap<Block, Integer>> mapXtoBlocksToLayer = new HashMap<>();
    protected void addToMap(int x, int layer, Block block){
        if(mapXtoBlocksToLayer.containsKey(x)){
            if(!mapXtoBlocksToLayer.get(x).containsKey(block)){
                mapXtoBlocksToLayer.get(x).put(block, layer);
            }
        }
        else{
            HashMap<Block, Integer> blocksToLayers = new HashMap<>();
            blocksToLayers.put(block, layer);
            mapXtoBlocksToLayer.put(x, blocksToLayers);
        }
    }
    protected void removeFromMap(int minX, int maxX){
        for(var xToMapEntry : mapXtoBlocksToLayer.entrySet()){
            if(xToMapEntry.getKey() < minX || xToMapEntry.getKey() > maxX){
                for(var blockToLayerEntry : xToMapEntry.getValue().entrySet()){
                    Block block = blockToLayerEntry.getKey();
                    int layer = blockToLayerEntry.getValue();
                    if(!gameObjects.removeGameObject(block, layer)){
                        gameObjects.removeGameObject(block, Layer.DEFAULT);
                    }
                }
            }
        }
        mapXtoBlocksToLayer.entrySet().removeIf(entry -> entry.getKey() < minX || entry.getKey() > maxX);
    }
}
