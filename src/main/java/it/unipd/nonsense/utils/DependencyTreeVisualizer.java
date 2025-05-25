package it.unipd.nonsense.utils;

import com.google.cloud.language.v1.Token;
import com.google.cloud.language.v1.DependencyEdge;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

import java.util.*;

/**
 * A visualizer for dependency parse trees using JavaFX.
 * Transforms linguistic analysis results from Google Cloud Natural Language API
 * into interactive graphical representations of sentence structure.
 */
public class DependencyTreeVisualizer {
    // The JavaFX pane where the tree will be drawn
    private final Pane drawingPane;

    // Current dimensions of the drawing pane
    private double paneWidth;
    private double paneHeight;

    // The tokens currently being visualized
    private List<Token> currentTokens;

    // Layout constants
    private static final double VERTICAL_SPACING = 70;  // Space between tree levels
    private static final double MIN_H_SPACING = 60;     // Minimum horizontal spacing between nodes
    private static final double NODE_WIDTH = 80;        // Width of each node rectangle
    private static final double NODE_HEIGHT = 40;       // Height of each node rectangle

    /**
     * Constructs a new DependencyTreeVisualizer bound to the specified JavaFX Pane.
     * Sets up resize listeners to automatically redraw the tree when the pane size changes.
     *
     * @param drawingPane The JavaFX Pane where the tree visualization will be rendered
     */
    public DependencyTreeVisualizer(Pane drawingPane) {
        this.drawingPane = drawingPane;
        this.paneWidth = drawingPane.getWidth();
        this.paneHeight = drawingPane.getHeight();

        // Add listeners for pane resizing
        drawingPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            this.paneWidth = newVal.doubleValue();
            redrawTree();
        });

        drawingPane.heightProperty().addListener((obs, oldVal, newVal) -> {
            this.paneHeight = newVal.doubleValue();
            redrawTree();
        });
    }

    /**
     * Redraws the dependency tree using the current tokens and pane dimensions.
     */
    private void redrawTree() {
        if (currentTokens != null) {
            drawDependencyTree(currentTokens);
        }
    }

    /**
     * Finds the root token in a list of tokens.
     *
     * @param tokens List of tokens to search
     * @return The root token, or null if not found
     */
    private Token findRootToken(List<Token> tokens) {
        for (Token token : tokens) {
            if (token.getDependencyEdge().getLabel() == DependencyEdge.Label.ROOT) {
                return token;
            }
        }
        return null;
    }

    /**
     * Main method to draw a dependency parse tree from linguistic tokens.
     *
     * @param tokens List of tokens from syntactic analysis
     */
    public void drawDependencyTree(List<Token> tokens) {
        this.currentTokens = tokens;
        drawingPane.getChildren().clear();
        if (tokens.isEmpty()) return;

        // Find root and build layout
        Token rootToken = findRootToken(tokens);
        if (rootToken == null) return;

        TreeNodeLayout rootLayout = buildLayout(rootToken, tokens);

        // Calculate scaling if needed
        double totalTreeWidth = rootLayout.subtreeWidth;
        double scale = Math.min((paneWidth - 40) / totalTreeWidth, 1.0);

        // Assign positions to all nodes
        Map<Token, Position> positions = new HashMap<>();
        double centerX = paneWidth / 2;
        double startY = 20;
        assignPositions(rootLayout, centerX, startY, positions);

        // Center the tree horizontally
        double minX = positions.values().stream().mapToDouble(p -> p.x).min().orElse(0);
        double maxX = positions.values().stream().mapToDouble(p -> p.x).max().orElse(0);
        double actualTreeWidth = maxX - minX;
        double offset = (paneWidth - actualTreeWidth) / 2 - minX;

        positions.values().forEach(pos -> pos.x += offset);

        // Apply scaling if tree is too wide
        if (scale < 1.0) {
            positions.values().forEach(pos -> pos.x = centerX + (pos.x - centerX) * scale);
        }

        // Draw all components
        drawAllConnections(tokens, positions);
        drawAllNodes(tokens, positions);
    }

    /**
     * Gets all child tokens of a given parent token.
     *
     * @param parent The parent token
     * @param allTokens Complete list of tokens
     * @return Sorted list of child tokens
     */
    private List<Token> getChildren(Token parent, List<Token> allTokens) {
        List<Token> children = new ArrayList<>();
        int parentIndex = allTokens.indexOf(parent);

        for (Token token : allTokens) {
            if (token.getDependencyEdge().getHeadTokenIndex() == parentIndex && token != parent) {
                children.add(token);
            }
        }

        children.sort(Comparator.comparingInt(allTokens::indexOf));
        return children;
    }

    /**
     * Draws all dependency connections between nodes.
     *
     * @param tokens List of all tokens
     * @param positions Map of token positions
     */
    private void drawAllConnections(List<Token> tokens, Map<Token, Position> positions) {
        for (Token token : tokens) {
            DependencyEdge edge = token.getDependencyEdge();
            int headIndex = edge.getHeadTokenIndex();

            if (token.getDependencyEdge().getLabel() != DependencyEdge.Label.ROOT && headIndex >= 0 && headIndex < tokens.size()) {
                Token headToken = tokens.get(headIndex);
                Position headPos = positions.get(headToken);
                Position childPos = positions.get(token);

                if (headPos != null && childPos != null) {
                    double startYOffset = NODE_HEIGHT/2 + 10;
                    double endYOffset = 15;
                    drawConnection(headPos.x, headPos.y + startYOffset, childPos.x, childPos.y - endYOffset);
                }
            }
        }
    }

    /**
     * Draws all node elements of the tree.
     *
     * @param tokens List of all tokens
     * @param positions Map of token positions
     */
    private void drawAllNodes(List<Token> tokens, Map<Token, Position> positions) {
        for (Token token : tokens) {
            Position pos = positions.get(token);
            if (pos != null) {
                drawNode(token, pos.x, pos.y);
            }
        }
    }

    /**
     * Draws a single node with token text and dependency label.
     *
     * @param token The token to visualize
     * @param x Center x-coordinate
     * @param y Top y-coordinate
     */
    private void drawNode(Token token, double x, double y) {
        String content = token.getText().getContent();
        String label = token.getDependencyEdge().getLabel() == DependencyEdge.Label.ROOT
                ? token.getPartOfSpeech().getTag().toString()
                : token.getDependencyEdge().getLabel().toString();

        boolean isRoot = token.getDependencyEdge().getLabel() == DependencyEdge.Label.ROOT;

        Text text = new Text(content + "\n(" + label + ")");
        text.setFont(Font.font("Arial", 11));
        text.setTextAlignment(TextAlignment.CENTER);
        text.setWrappingWidth(NODE_WIDTH - 10);
        text.setX(x - (NODE_WIDTH / 2) + 5);
        text.setY(y + (NODE_HEIGHT / 4));

        if (isRoot) {
            text.setFill(Color.DARKRED);
            text.setStyle("-fx-font-weight: bold;");
        } else {
            text.setFill(Color.BLACK);
        }

        drawingPane.getChildren().add(text);
    }

    /**
     * Draws a connection line between two nodes.
     *
     * @param startX Starting x-coordinate
     * @param startY Starting y-coordinate
     * @param endX Ending x-coordinate
     * @param endY Ending y-coordinate
     */
    private void drawConnection(double startX, double startY, double endX, double endY) {
        Line line = new Line(startX, startY, endX, endY);
        line.setStroke(Color.GRAY);
        line.setStrokeWidth(1);
        line.getStrokeDashArray().addAll(2d, 4d);
        drawingPane.getChildren().add(line);

        drawArrowHead(startX, startY, endX, endY);
    }

    /**
     * Draws an arrowhead at the end of a connection.
     *
     * @param startX Line start x-coordinate
     * @param startY Line start y-coordinate
     * @param endX Line end x-coordinate (arrow location)
     * @param endY Line end y-coordinate (arrow location)
     */
    private void drawArrowHead(double startX, double startY, double endX, double endY) {
        double angle = Math.atan2(endY - startY, endX - startX);
        double arrowLength = 8;

        Line arrow1 = new Line();
        arrow1.setStartX(endX);
        arrow1.setStartY(endY);
        arrow1.setEndX(endX - arrowLength * Math.cos(angle - Math.PI/6));
        arrow1.setEndY(endY - arrowLength * Math.sin(angle - Math.PI/6));
        arrow1.setStroke(Color.GRAY);
        arrow1.setStrokeWidth(1);

        Line arrow2 = new Line();
        arrow2.setStartX(endX);
        arrow2.setStartY(endY);
        arrow2.setEndX(endX - arrowLength * Math.cos(angle + Math.PI/6));
        arrow2.setEndY(endY - arrowLength * Math.sin(angle + Math.PI/6));
        arrow2.setStroke(Color.GRAY);
        arrow2.setStrokeWidth(1);

        drawingPane.getChildren().addAll(arrow1, arrow2);
    }

    /**
     * Represents a position in 2D space.
     */
    private static class Position {
        double x, y;
        Position(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    /**
     * Represents the layout structure of a tree node and its subtree.
     */
    private static class TreeNodeLayout {
        Token token;
        List<TreeNodeLayout> children = new ArrayList<>();
        double subtreeWidth;  // Total width required by this subtree
    }

    /**
     * Recursively builds a layout structure for the dependency tree.
     *
     * @param current The current token being processed
     * @param allTokens Complete list of tokens
     * @return TreeNodeLayout structure for this subtree
     */
    private TreeNodeLayout buildLayout(Token current, List<Token> allTokens) {
        TreeNodeLayout node = new TreeNodeLayout();
        node.token = current;
        List<Token> childrenTokens = getChildren(current, allTokens);

        // Build layouts for all children
        for (Token child : childrenTokens) {
            node.children.add(buildLayout(child, allTokens));
        }

        // Calculate required width
        if (node.children.isEmpty()) {
            node.subtreeWidth = NODE_WIDTH;
        } else {
            node.subtreeWidth = 0;
            for (TreeNodeLayout child : node.children) {
                node.subtreeWidth += child.subtreeWidth + MIN_H_SPACING;
            }
            node.subtreeWidth -= MIN_H_SPACING;  // Remove last spacing
        }

        return node;
    }

    /**
     * Recursively assigns screen positions to all nodes in the tree.
     *
     * @param node Current node being positioned
     * @param x Center x-coordinate for this node
     * @param y Top y-coordinate for this node
     * @param positions Map to store token positions
     */
    private void assignPositions(TreeNodeLayout node, double x, double y, Map<Token, Position> positions) {
        positions.put(node.token, new Position(x, y));

        // Position children horizontally centered below parent
        double currentX = x - node.subtreeWidth / 2;

        for (TreeNodeLayout child : node.children) {
            double childCenterX = currentX + child.subtreeWidth / 2;
            assignPositions(child, childCenterX, y + VERTICAL_SPACING, positions);
            currentX += child.subtreeWidth + MIN_H_SPACING;
        }
    }
}