package logic;

import java.awt.Point;
import java.util.ArrayList;

public class Player {

    private final String name;
    private boolean isActive = true;
    private int numberOfMoves = 0;
    private final boolean isHuman;
    private final ArrayList<Color> colors = new ArrayList<>();

    // index 0 in this array, is the color's in index 0(in the colors array) ending edge cord.
    private final ArrayList<Point> endingEdgeCords = new ArrayList<>();
    private final ArrayList<Point> marbles = new ArrayList<>();

    public Player(String PlayerName, boolean isHuman) {
        name = PlayerName;
        this.isHuman = isHuman;
        isActive = true;
    }

    public int getNumberOfMoves() {
        return numberOfMoves;
    }

    public void clearMoves() {
        numberOfMoves = 0;
    }

    public void incNumberOfMoves() {
        numberOfMoves++;
    }

    public String getName() {
        return name;
    }

    public void addColor(Color color) {
        colors.add(color);
    }

    public void addMarbles(ArrayList<Point> marblesToAdd) {
        marbles.addAll(marblesToAdd);
    }

    public boolean isHuman() {
        return isHuman;
    }

    public ArrayList<Color> getColors() {
        return colors;
    }

    public ArrayList<Point> getMarbles() {
        return marbles;
    }

    public ArrayList<Point> getEndingEdgePoints() {
        return endingEdgeCords;
    }

    public void addEndingEdgeCord(Point endingEdgePoint) {
        endingEdgeCords.add(endingEdgePoint);
    }

    public boolean isActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public String toString() {
        return name;
    }

}
