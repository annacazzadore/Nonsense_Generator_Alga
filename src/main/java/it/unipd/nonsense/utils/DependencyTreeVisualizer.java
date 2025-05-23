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

public class DependencyTreeVisualizer {
    private final Pane drawingPane;
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
        return null;
    }

    public void drawDependencyTree(List<Token> tokens) {
        this.currentTokens = tokens;
        drawingPane.getChildren().clear();
        if (tokens.isEmpty()) return;

        Token rootToken = findRootToken(tokens);
        if (rootToken == null) return;

        TreeNodeLayout rootLayout = buildLayout(rootToken, tokens);

        double totalTreeWidth = rootLayout.subtreeWidth;
        double scale = Math.min((paneWidth - 40) / totalTreeWidth, 1.0); // scaling solo se serve

        Map<Token, Position> positions = new HashMap<>();
        double centerX = paneWidth / 2;
        double startY = 20;
        assignPositions(rootLayout, centerX, startY, positions);
        double minX = positions.values().stream().mapToDouble(p -> p.x).min().orElse(0);
        double maxX = positions.values().stream().mapToDouble(p -> p.x).max().orElse(0);
        double actualTreeWidth = maxX - minX;
        double offset = (paneWidth - actualTreeWidth) / 2 - minX;

        for (Position pos : positions.values()) {
            pos.x += offset;
        }


        if (scale < 1.0) {
            for (Position pos : positions.values()) {
                pos.x = centerX + (pos.x - centerX) * scale;
            }
        }

        drawAllConnections(tokens, positions);
        drawAllNodes(tokens, positions);
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
                            childPos.x, childPos.y - endYOffset
                    );
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


    private void drawConnection(double startX, double startY, double endX, double endY) {
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