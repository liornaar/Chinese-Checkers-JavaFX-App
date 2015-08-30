package logic;

import java.awt.Point;
import java.util.ArrayList;

public class CheckersBoard {

    public static final int ROWS = 17;
    public static final int COLS = 25;
    public static final char EMPTY = 'E';
    public static final char WALL = ' ';

    private final BoardCell[][] board = new BoardCell[ROWS][COLS];

    public CheckersBoard() {
        initFullBoard();
    }

    private void initFullBoard() {
        ArrayList<String> lines = FilesManager.readBoardFromFile();
        initBoardFromStrings(lines);
    }

    private void initBoardFromStrings(ArrayList<String> lines) {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                char currentChar = lines.get(i).charAt(j);
                boolean isWall = false;
                Color currentColor = getColorFromCharAndAddToColorSet(currentChar, i, j, false);
                if (currentChar == WALL) {
                    isWall = true;
                }
                board[i][j] = new BoardCell(currentChar, isWall, currentColor);
            }
        }
    }

    public Color getColorFromCharAndAddToColorSet(char currentChar, int row, int col, boolean fromXML) {
        Color currentColor = null;
        switch (currentChar) {
            case 'G':
                currentColor = Color.GREEN;
                if (!fromXML) {
                    ColorSets.greenSetTop.add(new Point(row, col));
                }
                break;
            case 'W':
                currentColor = Color.WHITE;
                if (!fromXML) {
                    ColorSets.whiteSetTopRight.add(new Point(row, col));
                }
                break;
            case 'N':
                currentColor = Color.BLUE;
                if (!fromXML) {
                    ColorSets.blueSetBottomRight.add(new Point(row, col));
                }
                break;
            case 'Y':
                currentColor = Color.YELLOW;
                if (!fromXML) {
                    ColorSets.yellowSetBottom.add(new Point(row, col));
                }
                break;
            case 'B':
                currentColor = Color.BLACK;
                if (!fromXML) {
                    ColorSets.blackSetBottomLeft.add(new Point(row, col));
                }
                break;
            case 'R':
                currentColor = Color.RED;
                if (!fromXML) {
                    ColorSets.redSetTopLeft.add(new Point(row, col));
                }
                break;
            case EMPTY:
                currentColor = Color.EMPTY;
            case WALL:
                currentColor = Color.EMPTY;
                break;
            default:
                throw new AssertionError();
        }

        return currentColor;
    }

    public char colorToChar(Color currentColor) {
        char res;
        switch (currentColor) {
            case GREEN:
                res = 'G';
                break;
            case WHITE:
                res = 'W';
                break;
            case BLUE:
                res = 'N';
                break;
            case YELLOW:
                res = 'Y';
                break;
            case BLACK:
                res = 'B';
                break;
            case RED:
                res = 'R';
                break;
            case EMPTY:
                res = 'E';
                break;
            default:
                res = ' ';
        }
        return res;
    }

    public class BoardCell {//nested class

        private final boolean isWall;
        private char currentChar;
        private Color charColor;

        public BoardCell(char initChar, boolean isWall, Color initColor) {
            currentChar = initChar;
            this.isWall = isWall;
            charColor = initColor;
        }

        public boolean isWall() {
            return isWall;
        }

        public char getChar() {
            return currentChar;
        }

        public Color getColor() {
            return charColor;
        }

        public void setChar(char charToSet, Color charColor) {
            currentChar = charToSet;
            this.charColor = charColor;
        }

    }

    public BoardCell getCellByCoord(int row, int col) {
        return board[row][col];
    }

    public char getCharByCord(int row, int col) {
        return board[row][col].getChar();
    }

    public Color getColorByCord(int row, int col) {
        return board[row][col].getColor();
    }

    public boolean isWall(int row, int col) {
        return board[row][col].isWall();
    }

    public void setCharByCord(char charToSet, Color charColor, int row, int col) {
        board[row][col].setChar(charToSet, charColor);
    }

    public BoardCell[][] getBoard() {
        return board;
    }

}
