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
    private static final double VERTICAL_SPACING = 70;
    private static final double MIN_H_SPACING = 60;
    private static final double NODE_WIDTH = 80;
    private static final double NODE_HEIGHT = 40;

    public DependencyTreeVisualizer(Pane drawingPane) {
        this.drawingPane = drawingPane;
        this.paneWidth = drawingPane.getPrefWidth();
        this.paneHeight = drawingPane.getPrefHeight();
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
        drawingPane.getChildren().clear();
        if (tokens.isEmpty()) return;

        // Find root token
        Token rootToken = findRootToken(tokens);
        if (rootToken == null) return;

        // Calculate tree dimensions
        TreeDimensions dimensions = calculateTreeDimensions(rootToken, tokens);

        // Calculate scale factors to fit the tree in the pane
        double xScale = paneWidth / dimensions.totalWidth;
        double yScale = paneHeight / dimensions.totalHeight;
        double scale = Math.min(xScale, yScale) * 0.9; // 90% of available space

        // Calculate positions with scaling
        Map<Token, Position> positions = new HashMap<>();
        calculatePositions(rootToken, tokens,
                paneWidth / 2, 30,
                positions, 1,
                scale, dimensions.maxDepth);

        // Draw all connections first
        drawAllConnections(tokens, positions);

        // Then draw all nodes
        drawAllNodes(tokens, positions);
    }

    private TreeDimensions calculateTreeDimensions(Token root, List<Token> allTokens) {
        TreeDimensions dimensions = new TreeDimensions();
        calculateDimensions(root, allTokens, 0, 0, dimensions);
        return dimensions;
    }

    private void calculateDimensions(Token current, List<Token> allTokens,
                                     double x, int depth,
                                     TreeDimensions dimensions) {
        dimensions.maxDepth = Math.max(dimensions.maxDepth, depth);
        dimensions.totalWidth = Math.max(dimensions.totalWidth, x);
        dimensions.totalHeight = depth * VERTICAL_SPACING + NODE_HEIGHT;

        List<Token> children = getChildren(current, allTokens);
        double childSpacing = calculateChildSpacing(children.size());
        double startX = x - (childSpacing * (children.size() - 1)) / 2;

        for (int i = 0; i < children.size(); i++) {
            calculateDimensions(children.get(i), allTokens,
                    startX + i * childSpacing,
                    depth + 1, dimensions);
        }
    }

    private double calculateChildSpacing(int childCount) {
        if (childCount <= 1) return 0;
        return Math.max(NODE_WIDTH * 1.5, MIN_H_SPACING);
    }

    private void calculatePositions(Token current, List<Token> allTokens,
                                    double x, double y,
                                    Map<Token, Position> positions, int depth,
                                    double scale, int maxDepth) {
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

            if (token.getDependencyEdge().getLabel() != DependencyEdge.Label.ROOT && headIndex >= 0 && headIndex < tokens.size()) {
                Token headToken = tokens.get(headIndex);
                Position headPos = positions.get(headToken);
                Position childPos = positions.get(token);

                if (headPos != null && childPos != null) {
                    drawConnection(headPos.x, headPos.y + 15,
                            childPos.x, childPos.y - 15,
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
        String label = token.getDependencyEdge().getLabel().toString();

        boolean isRoot = token.getDependencyEdge().getLabel() == DependencyEdge.Label.ROOT;
//        boolean isNounVerbAdj = switch (token.getPartOfSpeech().getTag()) {
//            case NOUN, VERB, ADJ -> true;
//            default -> false;
//        };
//
//        // Solo se NON è NOUN, VERB o ADJ disegna il rettangolo
//        if (!isNounVerbAdj) {
//            Rectangle bg = new Rectangle();
//            bg.setWidth(NODE_WIDTH);
//            bg.setHeight(NODE_HEIGHT);
//            bg.setX(x - NODE_WIDTH / 2);
//            bg.setY(y - NODE_HEIGHT / 2);
//            bg.setArcWidth(10);
//            bg.setArcHeight(10);
//            bg.setFill(Color.WHITE);
//            bg.setStroke(isRoot ? Color.DARKRED : Color.LIGHTGRAY);
//            bg.setStrokeWidth(1.5);
//            drawingPane.getChildren().add(bg);
//        }

        // Crea il testo
        Text text = new Text(content + "\n(" + label + ")");
        text.setFont(Font.font("Arial", 11));
        text.setTextAlignment(TextAlignment.CENTER);
        text.setWrappingWidth(NODE_WIDTH - 10);
        text.setX(x - text.getLayoutBounds().getWidth() / 2);
        text.setY(y + text.getLayoutBounds().getHeight() / 4);

        // Stile speciale per la radice
        if (isRoot) {
            text.setFill(Color.DARKRED);
            text.setStyle("-fx-font-weight: bold;");
        } else {
            text.setFill(Color.DARKSLATEBLUE);
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
        double totalWidth;
        double totalHeight;
        int maxDepth;
    }
}