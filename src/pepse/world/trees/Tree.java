package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.Block;

import java.awt.*;
import java.util.Random;
import java.util.function.IntFunction;

public class Tree {
    private static final Color TREE_COLOR = new Color(100, 50, 20);
    private static final int MIN_TREE_HEIGHT = 3;
    private final Random rand;
    private GameObjectCollection gameObjects;
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
        for (int x = minX; x <= maxX; x += Block.SIZE) {
            if (rand.nextInt(100) < 5) {  // chance for tree
                createTree(x, MIN_TREE_HEIGHT + rand.nextInt(4));
            }
        }
    }

    /**
     * creates a tree
     * @param x the x coordinate for the tree
     * @param treeHeight how many blocks high the tree is
     */
    public void createTree(int x, int treeHeight) {
        for (int i = 0; i < treeHeight; i++) {  // builds tree
            Block trunk = new Block(new Vector2(x,
                    Block.round(groundHeightFunc.apply(x) - (treeHeight * Block.SIZE) +
                            (i * Block.SIZE))), new RectangleRenderable(TREE_COLOR));
            gameObjects.addGameObject(trunk, treeLayer);

        }
    }


}
