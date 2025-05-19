package it.unipd.nonsense.utils;

import com.google.cloud.language.v1.Token;
import com.google.cloud.language.v1.DependencyEdge;
import java.util.*;

public class DependencyTree {
    public static class TreeNode {
        Token token;
        List<TreeNode> children;
        int depth;

        TreeNode(Token token, int depth) {
            this.token = token;
            this.children = new ArrayList<>();
            this.depth = depth;
        }
    }

    private TreeNode root;
    private List<Token> tokens;

    public DependencyTree(List<Token> tokens) {
        this.tokens = tokens;
        this.root = buildDependencyTree();
    }

    private TreeNode buildDependencyTree() {
        if (tokens.isEmpty()) return null;

        // Create nodes for all tokens
        Map<Integer, TreeNode> nodeMap = new HashMap<>();
        for (int i = 0; i < tokens.size(); i++) {
            nodeMap.put(i, new TreeNode(tokens.get(i), 0));
        }

        // Build tree structure
        TreeNode root = null;
        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            DependencyEdge edge = token.getDependencyEdge();
            int headIndex = edge.getHeadTokenIndex();

            if (headIndex == i || headIndex == -1) {
                // This is the root node
                root = nodeMap.get(i);
            } else {
                // Add as child to its head
                TreeNode headNode = nodeMap.get(headIndex);
                TreeNode currentNode = nodeMap.get(i);
                currentNode.depth = headNode.depth + 1;
                headNode.children.add(currentNode);
            }
        }

        // Sort children by their position in the sentence
        for (TreeNode node : nodeMap.values()) {
            node.children.sort(Comparator.comparingInt(
                    child -> tokens.indexOf(child.token)));
        }

        return root;
    }

    public TreeNode getRoot() {
        return root;
    }

    public List<Token> getFlattenedTree() {
        List<Token> result = new ArrayList<>();
        if (root != null) {
            flattenTree(root, result);
        }
        return result;
    }

    private void flattenTree(TreeNode node, List<Token> result) {
        result.add(node.token);
        for (TreeNode child : node.children) {
            flattenTree(child, result);
        }
    }
}