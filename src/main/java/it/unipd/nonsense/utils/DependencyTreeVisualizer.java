package it.unipd.nonsense.utils;

import com.google.cloud.language.v1.Token;
import com.google.cloud.language.v1.DependencyEdge;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

import java.util.*;

public class DependencyTreeVisualizer {
    private Pane drawingPane;
    private double paneWidth;
    private double paneHeight;
    private List<Token> currentTokens;
    private static final double VERTICAL_SPACING = 70;
    private static final double MIN_H_SPACING = 60;
    private static final double NODE_WIDTH = 80;
    private static final double NODE_HEIGHT = 40;

    public DependencyTreeVisualizer(Pane drawingPane) {
        this.drawingPane = drawingPane;
        this.paneWidth = drawingPane.getWidth();
        this.paneHeight = drawingPane.getHeight();

        drawingPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            this.paneWidth = newVal.doubleValue();
            redrawTree();
        });

        drawingPane.heightProperty().addListener((obs, oldVal, newVal) -> {
            this.paneHeight = newVal.doubleValue();
            redrawTree();
        });

    }

    private void redrawTree() {
        if (currentTokens != null) {
            drawDependencyTree(currentTokens);
        }
    }

    private Token findRootToken(List<Token> tokens) {
        for (Token token : tokens) {
            if (token.getDependencyEdge().getLabel() == DependencyEdge.Label.ROOT) {
                return token;
            }
        }
        return null; // Nessuna radice trovata
    }

    public void drawDependencyTree(List<Token> tokens) {
        this.currentTokens = tokens; // Per ridisegnare in caso di resize
        drawingPane.getChildren().clear();
        if (tokens.isEmpty()) return;

        Token rootToken = findRootToken(tokens);
        if (rootToken == null) return;

        // Costruisce struttura ricorsiva
        TreeNodeLayout rootLayout = buildLayout(rootToken, tokens);

        // Calcola la larghezza totale
        double totalTreeWidth = rootLayout.subtreeWidth;
        double scale = Math.min((paneWidth - 40) / totalTreeWidth, 1.0); // scaling solo se serve

        // Posizionamento centrato
        Map<Token, Position> positions = new HashMap<>();
        double centerX = paneWidth / 2;
        double treeHalfWidth = rootLayout.subtreeWidth / 2;
        double rootX = centerX;
        double startY = 20;
        assignPositions(rootLayout, rootX, startY, positions);
        // Calcola l'offset medio per centrare meglio tutto l’albero
        double minX = positions.values().stream().mapToDouble(p -> p.x).min().orElse(0);
        double maxX = positions.values().stream().mapToDouble(p -> p.x).max().orElse(0);
        double actualTreeWidth = maxX - minX;
        double offset = (paneWidth - actualTreeWidth) / 2 - minX;

// Applica l’offset a tutti i nodi
        for (Position pos : positions.values()) {
            pos.x += offset;
        }


        // Applica scaling se necessario
        if (scale < 1.0) {
            for (Position pos : positions.values()) {
                pos.x = centerX + (pos.x - centerX) * scale;
            }
        }

        drawAllConnections(tokens, positions);
        drawAllNodes(tokens, positions);
    }

    // Nuovo metodo per calcolare le dimensioni senza scaling
    private TreeDimensions calculateRawTreeDimensions(Token root, List<Token> allTokens) {
        TreeDimensions dimensions = new TreeDimensions();
        calculateRawDimensions(root, allTokens, 0, 0, dimensions);
        dimensions.totalWidth = dimensions.maxX - dimensions.minX;
        return dimensions;
    }

    private void calculateRawDimensions(Token current, List<Token> allTokens,
                                        double x, int depth,
                                        TreeDimensions dimensions) {
        dimensions.maxDepth = Math.max(dimensions.maxDepth, depth);
        dimensions.minX = Math.min(dimensions.minX, x);
        dimensions.maxX = Math.max(dimensions.maxX, x);
        dimensions.totalHeight = (depth + 1) * VERTICAL_SPACING + NODE_HEIGHT;

        List<Token> children = getChildren(current, allTokens);
        double childSpacing = calculateChildSpacing(children.size());
        double startX = x - (childSpacing * (children.size() - 1)) / 2;

        for (int i = 0; i < children.size(); i++) {
            calculateRawDimensions(children.get(i), allTokens,
                    startX + i * childSpacing,
                    depth + 1, dimensions);
        }
    }


    // Modifica il metodo calculatePositions per usare lo scaling
    private void calculatePositions(Token current, List<Token> allTokens, double x, double y, Map<Token, Position> positions, int depth, double scale, int maxDepth) {
        positions.put(current, new Position(x, y));

        List<Token> children = getChildren(current, allTokens);
        if (children.isEmpty()) return;

        double childSpacing = calculateChildSpacing(children.size()) * scale;
        double startX = x - (childSpacing * (children.size() - 1)) / 2;
        double childY = y + (VERTICAL_SPACING * scale);

        for (int i = 0; i < children.size(); i++) {
            calculatePositions(children.get(i), allTokens,
                    startX + i * childSpacing, childY,
                    positions, depth + 1, scale, maxDepth);
        }
    }

    private double calculateChildSpacing(int childCount) {
        if (childCount <= 1) return 0;
        return Math.max(NODE_WIDTH * 1.5, MIN_H_SPACING);
    }

    private List<Token> getChildren(Token parent, List<Token> allTokens) {
        List<Token> children = new ArrayList<>();
        int parentIndex = allTokens.indexOf(parent);

        for (Token token : allTokens) {
            if (token.getDependencyEdge().getHeadTokenIndex() == parentIndex &&
                    token != parent) {
                children.add(token);
            }
        }

        // Sort children by their position in the sentence
        children.sort(Comparator.comparingInt(allTokens::indexOf));
        return children;
    }

    private void drawAllConnections(List<Token> tokens, Map<Token, Position> positions) {
        for (Token token : tokens) {
            DependencyEdge edge = token.getDependencyEdge();
            int headIndex = edge.getHeadTokenIndex();

            if (token.getDependencyEdge().getLabel() != DependencyEdge.Label.ROOT &&
                    headIndex >= 0 && headIndex < tokens.size()) {

                Token headToken = tokens.get(headIndex);
                Position headPos = positions.get(headToken);
                Position childPos = positions.get(token);

                if (headPos != null && childPos != null) {
                    // Aggiunto 10px all'offset verticale per far partire le frecce più in basso
                    double startYOffset = NODE_HEIGHT/2 + 10;
                    double endYOffset = 15; // Puoi regolare anche questo se vuoi cambiare il punto di arrivo

                    drawConnection(headPos.x, headPos.y + startYOffset,
                            childPos.x, childPos.y - endYOffset,
                            edge.getLabel().toString());
                }
            }
        }
    }

    private void drawAllNodes(List<Token> tokens, Map<Token, Position> positions) {
        for (Token token : tokens) {
            Position pos = positions.get(token);
            if (pos != null) {
                drawNode(token, pos.x, pos.y);
            }
        }
    }

    private void drawNode(Token token, double x, double y) {
        String content = token.getText().getContent();
        String label;
        if (token.getDependencyEdge().getLabel() == DependencyEdge.Label.ROOT) {
            label = token.getPartOfSpeech().getTag().toString(); // E.g. NOUN, VERB, ADJ...
        } else {
            label = token.getDependencyEdge().getLabel().toString();
        }


        boolean isRoot = token.getDependencyEdge().getLabel() == DependencyEdge.Label.ROOT;

        // Crea il testo
        Text text = new Text(content + "\n(" + label + ")");
        text.setFont(Font.font("Arial", 11));
        text.setTextAlignment(TextAlignment.CENTER);
        text.setWrappingWidth(NODE_WIDTH - 10);
        text.setX(x - (NODE_WIDTH / 2) + 5);
        text.setY(y + (NODE_HEIGHT / 4));

        // Stile speciale per la radice
        if (isRoot) {
            text.setFill(Color.DARKRED);
            text.setStyle("-fx-font-weight: bold;");
        } else {
            text.setFill(Color.BLACK);
        }

        drawingPane.getChildren().add(text);
    }


    private void drawConnection(double startX, double startY, double endX, double endY, String label) {
        Line line = new Line(startX, startY, endX, endY);
        line.setStroke(Color.GRAY);
        line.setStrokeWidth(1);
        line.getStrokeDashArray().addAll(2d, 4d);
        drawingPane.getChildren().add(line);

        // Add arrow head
        drawArrowHead(startX, startY, endX, endY);
    }

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

    private static class Position {
        double x, y;
        Position(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    private static class TreeDimensions {
        double minX = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double totalWidth;
        double totalHeight;
        int maxDepth;
    }

    private static class TreeNodeLayout {
        Token token;
        List<TreeNodeLayout> children = new ArrayList<>();
        double subtreeWidth;
    }


    private TreeNodeLayout buildLayout(Token current, List<Token> allTokens) {
        TreeNodeLayout node = new TreeNodeLayout();
        node.token = current;

        List<Token> childrenTokens = getChildren(current, allTokens);
        for (Token child : childrenTokens) {
            node.children.add(buildLayout(child, allTokens));
        }

        if (node.children.isEmpty()) {
            node.subtreeWidth = NODE_WIDTH;
        } else {
            node.subtreeWidth = 0;
            for (TreeNodeLayout child : node.children) {
                node.subtreeWidth += child.subtreeWidth + MIN_H_SPACING;
            }
            node.subtreeWidth -= MIN_H_SPACING; // rimuovi l’ultimo spazio extra
        }

        return node;
    }

    private void assignPositions(TreeNodeLayout node, double x, double y, Map<Token, Position> positions) {
        positions.put(node.token, new Position(x, y));

        double currentX = x - node.subtreeWidth / 2;
        for (TreeNodeLayout child : node.children) {
            double childCenterX = currentX + child.subtreeWidth / 2;
            assignPositions(child, childCenterX, y + VERTICAL_SPACING, positions);
            currentX += child.subtreeWidth + MIN_H_SPACING;
        }
    }

}