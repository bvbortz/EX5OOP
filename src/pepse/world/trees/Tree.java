package pepse.world.trees;

import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.world.Block;
import pepse.world.RemovableObjects;

import java.awt.*;
import java.util.Objects;
import java.util.Random;
import java.util.function.IntFunction;

/**
 * creates trees for the game - inherits from removableObject as the terrain is removed and
 * added again based on location of avatar
 */
public class Tree extends RemovableObjects {
    private static final Color TREE_COLOR = new Color(100, 50, 20);
    private static final int MIN_TREE_BLOCKS = 4;
    private static final int MAX_TREE_ADD = 6;
    private static final int LEAF_GAP = 2;
    public static final int RANDOM_BOUND = 100;
    public static final int TREE_CHANCE = 5;
    public static final int LEAF_ROWS = 4;
    public static final int LEAF_COLS = 5;
    public static final String LEAF_BLOCK = "leafBlock";
    public static final String TRUNK_BLOCK = "trunkBlock";
    private Random rand;
    private final int seed;
    private IntFunction<Float> groundHeightFunc;
    private int treeLayer;

    /**
     * Constructor
     * @param gameObjects The collection of all participating game objects.
     * @param groundHeightFunc a function to calculate the ground height
     * @param treeLayer the layer of the trees
     * @param seed seed for random
     */
    public Tree(GameObjectCollection gameObjects, IntFunction<Float> groundHeightFunc, int treeLayer,
                int seed) {
        this.seed = seed;
        this.gameObjects = gameObjects;
        this.groundHeightFunc = groundHeightFunc;
        this.treeLayer = treeLayer;
    }

    /**
     * creates trees in a certain range
     * @param minX the minimum x coordinate
     * @param maxX the maximum x coordinate
     */
    public void createInRange(int minX, int maxX) {
        // to match terrain blocks
        minX = Block.round(minX);
        maxX = Block.round(maxX);
        removeFromMap(minX, maxX);  // removes what's not in range
        // create trees in range
        for (int x = minX; x <= maxX; x += Block.SIZE) {
            if (rand.nextInt(RANDOM_BOUND) < TREE_CHANCE) {  // chance for tree
                createTree(x, MIN_TREE_BLOCKS + rand.nextInt(MAX_TREE_ADD));
            }
        }
    }

    /**
     * creates a tree with 20 leaves
     * @param x the x coordinate for the tree
     * @param treeBlocks how many blocks high the tree is
     */
    private void createTree(int x, int treeBlocks) {
        this.rand = new Random(Objects.hash(x, seed));
        float groundHeight = Block.round(groundHeightFunc.apply(x));  // ground height at x of tree
        createTrunk(x, treeBlocks, groundHeight);
        createLeaves(x, treeBlocks, (int) groundHeight);
    }

    /**
     * creates the leaves of the tree
     * @param x  x coordinate of tree
     * @param treeBlocks amount of blocks in trunk
     * @param groundHeight height of ground at x
     */
    private void createLeaves(int x, int treeBlocks, int groundHeight) {
        int leafYStart =   // leaves start from 2 blocks on top of trunk
                groundHeight - (Block.SIZE * treeBlocks) - (2 * Block.SIZE) - (2 * LEAF_GAP);
        int leafXStart = x - (2 * Block.SIZE) - (2 * LEAF_GAP);  // from 2 blocks left of trunk
        for (int j = 0; j < LEAF_ROWS; j++) {  // adds leaves
            for (int k = 0; k < LEAF_COLS; k++) {
                Leaf leaf = new Leaf(new Vector2(leafXStart + (k * (Block.SIZE + LEAF_GAP)),
                        leafYStart + (j * (Block.SIZE + LEAF_GAP))), gameObjects, seed);
                leaf.setTag(LEAF_BLOCK);
                addToMap(leafXStart + (k * (Block.SIZE + LEAF_GAP)), Layer.STATIC_OBJECTS, leaf);
                gameObjects.addGameObject(leaf);
            }
        }
    }

    /**
     * creates tree trunk
     * @param x  x coordinate of tree
     * @param treeBlocks amount of blocks in trunk
     * @param groundHeight height of ground at x
     */
    private void createTrunk(int x, int treeBlocks, float groundHeight) {
        for (int i = 0; i < treeBlocks; i++) {  // builds tree trunk
            Block trunk = new Block(new Vector2(x,
                    Block.round(groundHeight - (treeBlocks * Block.SIZE) +
                            (i * Block.SIZE))),
                    new RectangleRenderable(ColorSupplier.approximateColor(TREE_COLOR)));
            trunk.setTag(TRUNK_BLOCK);
            addToMap(x, treeLayer, trunk);
            gameObjects.addGameObject(trunk, treeLayer);
        }
    }
}
