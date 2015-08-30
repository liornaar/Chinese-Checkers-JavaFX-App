package logic;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

public class XMLGameData {

    public static final int BOARD_W = 13;
    public static final int BOARD_H = 17;
    public static final int MIN_PLAYERS = 2;
    public static final int MAX_PLAYERS = 6;

    private CheckersBoard board = null;
    private ArrayList<Player> players = null;
    private Player currentPlayersTurn;
    private int numberOfHumanPlayers = 0;
    private int numberOfComputerPlayers = 0;
    private int numberOfColorsForPlayer;
    private HashMap<Integer, Integer> maxColsForEachRow = null;
    private ArrayList<Point> targetPoints = null;
    private ArrayList<Point> totalTargetPoints = null;

    public XMLGameData() {
    }

    public XMLGameData(boolean loadGame) {

        board = new CheckersBoard();
        players = new ArrayList<>();
        maxColsForEachRow = new HashMap<>();
        targetPoints = new ArrayList<>();
        totalTargetPoints = new ArrayList<>();
        getMaxColForRow();
        createListOfTargetPoints();
    }

    public ArrayList<Point> getTotalTargetPoints() {
        return totalTargetPoints;
    }

    public void setNumberOfColorsForPlayer(int numberOfColorsForPlayer) {
        this.numberOfColorsForPlayer = numberOfColorsForPlayer;
    }

    public int getNumberOfColorsForPlayer() {
        return numberOfColorsForPlayer;
    }

    public int getNumberOfComputerPlayers() {
        return numberOfComputerPlayers;
    }

    public int getNumberOfHumanPlayers() {
        return numberOfHumanPlayers;
    }

    public void createBoardFromXML(char[][] boardXML) {

        deleteColorFromBoard();
        int placeMarbleCol;

        for (int row = 0; row < BOARD_H; row++) {
            for (int col = 0; col <= BOARD_W; col++) {
                if (boardXML[row][col] != 0) {
                    placeMarbleCol = getColomnForBoard(row, col);
                    Color color = board.getColorFromCharAndAddToColorSet(boardXML[row][col], row, placeMarbleCol, true);
                    board.setCharByCord(boardXML[row][col], color, row, placeMarbleCol);
                }

            }
        }
    }

    private int getColomnForBoard(int row, int col) {

        int countToEmptyCol, countToPlaceMarble;

        countToEmptyCol = 0;
        while (board.getCharByCord(row, countToEmptyCol) == CheckersBoard.WALL) {
            countToEmptyCol++;
        }
        countToPlaceMarble = 0;
        while (countToPlaceMarble != col) {
            countToPlaceMarble++;
        }

        return countToEmptyCol + countToPlaceMarble - 1 + col - 1;

    }

    private void deleteColorFromBoard() {

        for (int row = 0; row < CheckersBoard.ROWS; row++) {
            for (int col = 0; col < CheckersBoard.COLS; col++) {
                if (isColored(board.getCharByCord(row, col))) {
                    board.setCharByCord(CheckersBoard.EMPTY, Color.EMPTY, row, col);
                }
            }
        }
    }

    private boolean isColored(char charByCord) {

        return charByCord == 'N' || charByCord == 'B' || charByCord == 'Y' || charByCord == 'G' || charByCord == 'R' || charByCord == 'W';
    }

    public CheckersBoard getBoard() {
        return board;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public Player getCurrentPlayersTurn() {
        return currentPlayersTurn;
    }

    public void setBoard(CheckersBoard board) {
        this.board = board;
    }

    public void setCurrentPlayersTurn(Player currentPlayersTurn) {
        this.currentPlayersTurn = currentPlayersTurn;
    }

    void addHumanPlayer() {

        this.numberOfHumanPlayers++;
    }

    void addComputerPlayer() {

        this.numberOfComputerPlayers++;
    }

    public void setPlayers(ArrayList<Player> playersList) {
        players = playersList;
    }

    public boolean checkIfPlayerHasAllItsPoints() {

        ArrayList<Point> marbles = new ArrayList<>();

        for (Player player : this.players) {
            for (int row = 0; row < CheckersBoard.ROWS; row++) {
                for (int col = 0; col < CheckersBoard.COLS; col++) {
                    if (player.getColors().contains(board.getColorByCord(row, col))) {
                        marbles.add(new Point(row, col));
                    }
                }
            }
            if (marbles.size() % 10 != 0) {
                return false;
            }
            player.addMarbles(marbles);
            marbles.clear();
        }

        return true;
    }

    private void getMaxColForRow() {

        int col, maxCol;

        for (int row = 0; row < BOARD_H; row++) {
            col = 0;
            maxCol = 0;
            while (board.getCharByCord(row, col) == CheckersBoard.WALL) {
                col++;
            }
            while (col < CheckersBoard.COLS && board.getCharByCord(row, col) != CheckersBoard.WALL) {
                maxCol++;
                col += 2;
            }
            this.maxColsForEachRow.put(row + 1, maxCol);
        }
    }

    public HashMap<Integer, Integer> getMaxColsForEachRow() {
        return maxColsForEachRow;
    }

    private void createListOfTargetPoints() {
        targetPoints.add(new Point(17, 1));
        targetPoints.add(new Point(5, 1));
        targetPoints.add(new Point(5, 13));
        targetPoints.add(new Point(13, 1));
        targetPoints.add(new Point(13, 13));
        targetPoints.add(new Point(1, 1));
    }

    public ArrayList<Point> getTargetPoints() {
        return targetPoints;
    }

}
