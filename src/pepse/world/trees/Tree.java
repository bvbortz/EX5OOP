package pepse.world.trees;

import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.world.Block;
import pepse.world.RemovableObjects;

import java.awt.*;
import java.util.Random;
import java.util.function.IntFunction;

public class Tree extends RemovableObjects {
    private static final Color TREE_COLOR = new Color(100, 50, 20);
    private static final int MIN_TREE_BLOCKS = 4;
    private static final int MAX_TREE_ADD = 6;
    private static final int LEAF_GAP = 2;
    private final Random rand;
    private IntFunction<Float> groundHeightFunc;
    private int treeLayer;

    public Tree(GameObjectCollection gameObjects, IntFunction<Float> groundHeightFunc, int treeLayer) {
        this.gameObjects = gameObjects;
        this.groundHeightFunc = groundHeightFunc;
        this.treeLayer = treeLayer;
        this.rand = new Random();
    }

    public void createInRange(int minX, int maxX) {
        // to match terrain blocks
        minX = Block.round(minX);
        maxX = Block.round(maxX);
        removeFromMap(minX, maxX);
        for (int x = minX; x <= maxX; x += Block.SIZE) {
            if (rand.nextInt(100) < 5) {  // chance for tree
                createTree(x, MIN_TREE_BLOCKS + rand.nextInt(MAX_TREE_ADD));
            }
        }
    }

    /**
     * creates a tree with 20 leaves
     *
     * @param x          the x coordinate for the tree
     * @param treeBlocks how many blocks high the tree is
     */
    public void createTree(int x, int treeBlocks) {
        float groundHeight = Block.round(groundHeightFunc.apply(x));
        for (int i = 0; i < treeBlocks; i++) {  // builds tree
            Block trunk = new Block(new Vector2(x,
                    Block.round(groundHeight - (treeBlocks * Block.SIZE) +
                            (i * Block.SIZE))),
                    new RectangleRenderable(ColorSupplier.approximateColor(TREE_COLOR)));
            trunk.setTag("trunkBlock");
            addToMap(x, treeLayer, trunk);
            gameObjects.addGameObject(trunk, treeLayer);
        }
        int leafYStart =   // leaves start from 2 blocks on top of trunk
                (int) groundHeight - (Block.SIZE * treeBlocks) - (2 * Block.SIZE) - (2 * LEAF_GAP);
        int leafXStart = x - (2 * Block.SIZE) - (2 * LEAF_GAP);  // from 2 blocks left of trunk
        for (int j = 0; j < 4; j++) {  // adds leaves
            for (int k = 0; k < 5; k++) {
                Leaf leaf = new Leaf(new Vector2(leafXStart + (k * (Block.SIZE + LEAF_GAP)),
                        leafYStart + (j * (Block.SIZE + LEAF_GAP))), gameObjects);
                leaf.setTag("leafBlock");
                addToMap(leafXStart + (k * (Block.SIZE + LEAF_GAP)), Layer.STATIC_OBJECTS, leaf);
                gameObjects.addGameObject(leaf);
            }
        }
    }
}
