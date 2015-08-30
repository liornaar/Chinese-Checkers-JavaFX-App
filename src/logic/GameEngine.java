package logic;

import static logic.CheckersBoard.EMPTY;
import java.util.ArrayList;
import java.awt.Point;
import java.util.Random;

public class GameEngine {

    private static final int FALSE_VALUE = -1;

    private CheckersBoard board;
    private ArrayList<Player> players = new ArrayList<>();
    private Player currentPlayersTurn; // the first player to play is randomized
    private final ArrayList<Point> possibleMovesForCurrentPickedMarble = new ArrayList<>();

    private int numberOfPlayers;
    private int numberOfHumanPlayers;
    private int numberOfColorsForEachPlayer;

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void initNewGame(ArrayList<String> names, int totalPlayers, int humanPlayers, int colorsForEach) {

        this.numberOfPlayers = totalPlayers;
        this.numberOfHumanPlayers = humanPlayers;
        this.numberOfColorsForEachPlayer = colorsForEach;
        players.clear();
        ColorSets.clearAll();
        setPlayerNames(names);
        board = new CheckersBoard(); //here the board is initialized to full 6 colors board + the ColorSets are set.
        int totalColors = numberOfPlayers * numberOfColorsForEachPlayer;
        if (totalColors < 6) {
            removeUnusedColorsFromInit(totalColors);
        }
        assignColorsAndMarblesToPlayers(totalColors);
        currentPlayersTurn = randomFirstPlayerToPlay();
    }

    public void initNewGameFromXML(XMLGameData xmlData) {

        this.numberOfPlayers = xmlData.getPlayers().size();
        this.numberOfHumanPlayers = xmlData.getNumberOfHumanPlayers();
        this.numberOfColorsForEachPlayer = xmlData.getNumberOfColorsForPlayer();
        currentPlayersTurn = xmlData.getCurrentPlayersTurn();
        players = xmlData.getPlayers();
        board = xmlData.getBoard();
    }

    //INIT Funcs
    private void setPlayerNames(ArrayList<String> names) {
        for (int i = 0; i < numberOfHumanPlayers; i++) {
            Player newHumanPlayer = new Player(names.get(i), true);
            players.add(newHumanPlayer);
        }

        int count = 1;
        for (int i = numberOfHumanPlayers; i < numberOfPlayers; i++) {
            Player newAIPlayer = new Player("Computer #" + count, false);
            players.add(newAIPlayer);
            count++;
        }
    }

    private void removeUnusedColorsFromInit(int totalColors) {

        switch (totalColors) {
            case 2:
                removeColorFromBoard(ColorSets.whiteSetTopRight);
                removeColorFromBoard(ColorSets.blueSetBottomRight);
                removeColorFromBoard(ColorSets.blackSetBottomLeft);
                removeColorFromBoard(ColorSets.redSetTopLeft);
                break;
            case 3:
                removeColorFromBoard(ColorSets.yellowSetBottom);
                removeColorFromBoard(ColorSets.whiteSetTopRight);
                removeColorFromBoard(ColorSets.redSetTopLeft);
                break;
            case 4:
                removeColorFromBoard(ColorSets.yellowSetBottom);
                removeColorFromBoard(ColorSets.greenSetTop);
                break;
            case 5:
                removeColorFromBoard(ColorSets.whiteSetTopRight);
                break;
            default:
                throw new AssertionError();
        }
    }

    private void assignColorsAndMarblesToPlayers(int totalColors) {
        switch (totalColors) {
            case 2:
                assignColorAndMarblesToSpecipicPlayer(players.get(0), Color.GREEN, ColorSets.greenSetTop, new Point(17, 1));
                assignColorAndMarblesToSpecipicPlayer(players.get(1), Color.YELLOW, ColorSets.yellowSetBottom, new Point(1, 1));
                break;
            case 3:
                assignColorAndMarblesToSpecipicPlayer(players.get(0), Color.GREEN, ColorSets.greenSetTop, new Point(17, 1));
                assignColorAndMarblesToSpecipicPlayer(players.get(1), Color.BLUE, ColorSets.blueSetBottomRight, new Point(5, 1));
                assignColorAndMarblesToSpecipicPlayer(players.get(2), Color.BLACK, ColorSets.blackSetBottomLeft, new Point(5, 13));
                break;
            case 4:
                assign4colors();
                break;
            case 5:
                assignColorAndMarblesToSpecipicPlayer(players.get(0), Color.GREEN, ColorSets.greenSetTop, new Point(17, 1));
                assignColorAndMarblesToSpecipicPlayer(players.get(1), Color.BLUE, ColorSets.blueSetBottomRight, new Point(5, 1));
                assignColorAndMarblesToSpecipicPlayer(players.get(2), Color.YELLOW, ColorSets.yellowSetBottom, new Point(1, 1));
                assignColorAndMarblesToSpecipicPlayer(players.get(3), Color.BLACK, ColorSets.blackSetBottomLeft, new Point(5, 13));
                assignColorAndMarblesToSpecipicPlayer(players.get(4), Color.RED, ColorSets.redSetTopLeft, new Point(13, 13));
                break;
            case 6:
                assign6colors();
                break;
            default:
                throw new AssertionError();
        }
    }

    private void assignColorAndMarblesToSpecipicPlayer(Player player, Color color, ArrayList<Point> marbles, Point endingEdgePoint) {
        player.addColor(color);
        player.addMarbles(marbles);
        player.addEndingEdgeCord(endingEdgePoint);
    }

    private void assign4colors() {
        if (numberOfColorsForEachPlayer == 1) {
            assignColorAndMarblesToSpecipicPlayer(players.get(0), Color.WHITE, ColorSets.whiteSetTopRight, new Point(13, 1));
            assignColorAndMarblesToSpecipicPlayer(players.get(1), Color.BLUE, ColorSets.blueSetBottomRight, new Point(5, 1));
            assignColorAndMarblesToSpecipicPlayer(players.get(2), Color.BLACK, ColorSets.blackSetBottomLeft, new Point(5, 13));
            assignColorAndMarblesToSpecipicPlayer(players.get(3), Color.RED, ColorSets.redSetTopLeft, new Point(13, 13));
        } else {
            assignColorAndMarblesToSpecipicPlayer(players.get(0), Color.WHITE, ColorSets.whiteSetTopRight, new Point(13, 1));
            assignColorAndMarblesToSpecipicPlayer(players.get(0), Color.BLUE, ColorSets.blueSetBottomRight, new Point(5, 1));
            assignColorAndMarblesToSpecipicPlayer(players.get(1), Color.BLACK, ColorSets.blackSetBottomLeft, new Point(5, 13));
            assignColorAndMarblesToSpecipicPlayer(players.get(1), Color.RED, ColorSets.redSetTopLeft, new Point(13, 13));
        }
    }

    private void assign6colors() {
        if (numberOfColorsForEachPlayer == 1) {
            assignColorAndMarblesToSpecipicPlayer(players.get(0), Color.GREEN, ColorSets.greenSetTop, new Point(17, 1));
            assignColorAndMarblesToSpecipicPlayer(players.get(1), Color.WHITE, ColorSets.whiteSetTopRight, new Point(13, 1));
            assignColorAndMarblesToSpecipicPlayer(players.get(2), Color.BLUE, ColorSets.blueSetBottomRight, new Point(5, 1));
            assignColorAndMarblesToSpecipicPlayer(players.get(3), Color.YELLOW, ColorSets.yellowSetBottom, new Point(1, 1));
            assignColorAndMarblesToSpecipicPlayer(players.get(4), Color.BLACK, ColorSets.blackSetBottomLeft, new Point(5, 13));
            assignColorAndMarblesToSpecipicPlayer(players.get(5), Color.RED, ColorSets.redSetTopLeft, new Point(13, 13));
        } else if (numberOfColorsForEachPlayer == 2) {
            assignColorAndMarblesToSpecipicPlayer(players.get(0), Color.GREEN, ColorSets.greenSetTop, new Point(17, 1));
            assignColorAndMarblesToSpecipicPlayer(players.get(0), Color.WHITE, ColorSets.whiteSetTopRight, new Point(13, 1));
            assignColorAndMarblesToSpecipicPlayer(players.get(1), Color.BLUE, ColorSets.blueSetBottomRight, new Point(5, 1));
            assignColorAndMarblesToSpecipicPlayer(players.get(1), Color.YELLOW, ColorSets.yellowSetBottom, new Point(1, 1));
            assignColorAndMarblesToSpecipicPlayer(players.get(2), Color.BLACK, ColorSets.blackSetBottomLeft, new Point(5, 13));
            assignColorAndMarblesToSpecipicPlayer(players.get(2), Color.RED, ColorSets.redSetTopLeft, new Point(13, 13));
        } else {
            assignColorAndMarblesToSpecipicPlayer(players.get(0), Color.GREEN, ColorSets.greenSetTop, new Point(17, 1));
            assignColorAndMarblesToSpecipicPlayer(players.get(0), Color.WHITE, ColorSets.whiteSetTopRight, new Point(13, 1));
            assignColorAndMarblesToSpecipicPlayer(players.get(0), Color.BLUE, ColorSets.blueSetBottomRight, new Point(5, 1));
            assignColorAndMarblesToSpecipicPlayer(players.get(1), Color.YELLOW, ColorSets.yellowSetBottom, new Point(1, 1));
            assignColorAndMarblesToSpecipicPlayer(players.get(1), Color.BLACK, ColorSets.blackSetBottomLeft, new Point(5, 13));
            assignColorAndMarblesToSpecipicPlayer(players.get(1), Color.RED, ColorSets.redSetTopLeft, new Point(13, 13));
        }
    }

    private Player randomFirstPlayerToPlay() {
//        Random rand = new Random();
//        int randomNum = rand.nextInt(numberOfPlayers);
//
//        return players.get(randomNum);
        return players.get(0);
    }

    //End of INIT
    private void removeColorFromBoard(ArrayList<Point> pointsToRemove) {

        for (Point point : pointsToRemove) {
            board.setCharByCord('E', Color.EMPTY, point.x, point.y);
        }
    }

    public CheckersBoard getBoard() {
        return board;
    }

    public Player checkForWinner() {
        Player winner = null;
        boolean foundWinner = true;

        for (int i = 0; i < currentPlayersTurn.getColors().size() && foundWinner; i++) {
            Color currentPlayerColor = currentPlayersTurn.getColors().get(i);
            Point endingEdgePoint = currentPlayersTurn.getEndingEdgePoints().get(i);
            if (!(checkIfAllPointsIsColored(currentPlayerColor, getEndingCords(endingEdgePoint)))) {
                foundWinner = false;
                break;
            }
        }

        if (foundWinner) {
            winner = currentPlayersTurn;
        }

        return winner; //Null returned for none
    }

    private boolean checkIfAllPointsIsColored(Color color, ArrayList<Point> pointsToCheck) {
        boolean allPointsColored = true;

        for (Point endingPoint : pointsToCheck) {
            if (board.getColorByCord(endingPoint.x, endingPoint.y) != color) {
                allPointsColored = false;
                break;
            }
        }

        return allPointsColored;
    }

    public boolean isHumanTurn() {
        return currentPlayersTurn.isHuman();
    }

    //get player's availible moves
    public void calculatePossibleMovesForPlayer(Point pickedMarble) {

        possibleMovesForCurrentPickedMarble.clear();
        possibleMovesForCurrentPickedMarble.addAll(getPossibleMovesForPoint(pickedMarble));
    }

    private ArrayList<Point> getPossibleMovesForPoint(Point point) {

        Point possibleMovePoint;
        ArrayList<Point> possibleMoves = new ArrayList<>();

        for (Direction dir : Direction.values()) {
            possibleMovePoint = getPointInDirection(point, dir);

            if (checkIfValidCell(possibleMovePoint)) {
                if (board.getCharByCord(possibleMovePoint.x, possibleMovePoint.y) == EMPTY) {
                    possibleMoves.add(possibleMovePoint);
                } else {
                    Point canSkipThisDirection = getPointInDirection(possibleMovePoint, dir);
                    if (checkIfValidCell(canSkipThisDirection)) {
                        if (board.getCharByCord(canSkipThisDirection.x, canSkipThisDirection.y) == EMPTY) {
                            possibleMoves.add(canSkipThisDirection);
                        }
                    }
                }
            }
        }
        return possibleMoves;
    }

    private Point getPointInDirection(Point currentPos, Direction direction) {
        Point destination = new Point();

        switch (direction) {
            case TOPRIGHT:
                destination.setLocation(currentPos.getX() - 1, currentPos.getY() + 1);
                break;
            case RIGHT:
                destination.setLocation(currentPos.getX(), currentPos.getY() + 2);
                break;
            case BOTTOMRIGHT:
                destination.setLocation(currentPos.getX() + 1, currentPos.getY() + 1);
                break;
            case BOTTOMLEFT:
                destination.setLocation(currentPos.getX() + 1, currentPos.getY() - 1);
                break;
            case LEFT:
                destination.setLocation(currentPos.getX(), currentPos.getY() - 2);
                break;
            case TOPLEFT:
                destination.setLocation(currentPos.getX() - 1, currentPos.getY() - 1);
                break;
            default:
                throw new AssertionError();
        }
        return destination;
    }

    public void updateBoardWithNewPoint(Point oldMarble, Point newMarble) {

        Color color = board.getColorByCord(oldMarble.x, oldMarble.y);
        char c = board.getCharByCord(oldMarble.x, oldMarble.y);

        board.setCharByCord(CheckersBoard.EMPTY, Color.EMPTY, oldMarble.x, oldMarble.y);
        board.setCharByCord(c, color, newMarble.x, newMarble.y);
    }

    private boolean isPointOutOfRange(Point possibleMovePoint) {
        return possibleMovePoint.x < 0 || possibleMovePoint.y < 0 || possibleMovePoint.y > CheckersBoard.COLS - 1 || possibleMovePoint.x > CheckersBoard.ROWS - 1;
    }

    public Player getCurrentPlayerTurn() {
        return currentPlayersTurn;
    }

    public void switchTurns() {
        boolean nextIsActive = false;
        int currentIndex = players.indexOf(currentPlayersTurn);

        while (!nextIsActive) {
            if (currentIndex == numberOfPlayers - 1) {
                currentIndex = 0;
            } else {
                currentIndex++;
            }

            if (players.get(currentIndex).isActive()) {
                nextIsActive = true;
            }
        }
        currentPlayersTurn = players.get(currentIndex);
    }

    public ArrayList<Point> playAITurn() {
        Point bestSourceMarble = new Point();
        Point bestDestinationMarble = new Point();
        double bestDistance = 999;
        ArrayList<Point> skippingPath = new ArrayList<>();
        ArrayList<Point> maxSkippingPath = new ArrayList<>(); // in case we want to print the path to where we moved.
        Color currentColor;
        boolean isFirstRound = true;

        for (Point computerMarble2 : currentPlayersTurn.getMarbles()) {
            Point computerMarble = new Point(computerMarble2);
            currentColor = board.getColorByCord(computerMarble.x, computerMarble.y);
            calculatePossibleMovesForPlayer(computerMarble);
            if (!possibleMovesForCurrentPickedMarble.isEmpty()) {
                ArrayList<Point> tempPossibleMoves = possibleMovesForCurrentPickedMarble;
                for (int i = 0; i < tempPossibleMoves.size(); i++) {
                    Point possibleMove = new Point(tempPossibleMoves.get(i));
                    if (isFirstRound) {
                        bestSourceMarble = computerMarble;
                        bestDestinationMarble = possibleMove;
                        isFirstRound = false;
                    } else {
                        if (checkIfSkipped(computerMarble, possibleMove)) {
                            possibleMove = calculateAISkipPath(computerMarble, possibleMove, currentColor, skippingPath);
                            double tempDistance = updateBestMovesForSkip(bestSourceMarble, computerMarble, bestDestinationMarble, possibleMove, bestDistance, currentColor);
                            if (tempDistance != FALSE_VALUE) {
                                bestDistance = tempDistance;
                                maxSkippingPath = skippingPath;
                            }
                        } else { //current possible move is not a skip move
                            double tempDistance = updateBestMovesForSkip(bestSourceMarble, computerMarble, bestDestinationMarble, possibleMove, bestDistance, currentColor);
                            if (tempDistance != FALSE_VALUE) {
                                bestDistance = tempDistance;
                                maxSkippingPath.clear();
                            }
                        }
                    }
                }
            }
        }
        boolean gavePiority = false;
        if (maxSkippingPath.size() < 3) {
            //check if we still have marbles in starting base and give piority to move so it wont stuck, do if we dont have a "big skip".
            gavePiority = givePiorityToPointsStillInBase(bestSourceMarble, bestDestinationMarble, maxSkippingPath);
        }
        if(gavePiority || maxSkippingPath.isEmpty()){
            maxSkippingPath.clear();
            maxSkippingPath.add(bestSourceMarble);
            maxSkippingPath.add(bestDestinationMarble);
        }
        return maxSkippingPath;
        
//        ArrayList<Point> sourceAndDestination = new ArrayList<>(2);
//        sourceAndDestination.add(bestSourceMarble);
//        sourceAndDestination.add(bestDestinationMarble);
//        return sourceAndDestination;  
        
//        updateBoardWithNewPoint(bestSourceMarble, bestDestinationMarble);
//        updatePlayerMarblesAfterMove(bestSourceMarble, bestDestinationMarble);
    }

    private double updateBestMovesForSkip(Point bestSourceMarble, Point computerMarble, Point bestDestinationMarble, Point possibleMove, double bestDistance, Color currentColor) {
        double tempDistance;

        tempDistance = checkIfMoveIsGoingToTargetAndGetDistance(computerMarble, possibleMove, currentColor);
        if (tempDistance < bestDistance && tempDistance != FALSE_VALUE && (!reachedEndCords(computerMarble)) && (!isInOtherBase(computerMarble, possibleMove))) {
            bestSourceMarble.setLocation(computerMarble);
            bestDestinationMarble.setLocation(possibleMove);
        } else {
            tempDistance = FALSE_VALUE;
        }

        return tempDistance;
    }

    private boolean givePiorityToPointsStillInBase(Point bestSourceMarble, Point bestDestinationMarble, ArrayList<Point> maxSkippingPath) {
        boolean gavePiority = false;
        Color currentColor = board.getColorByCord(bestSourceMarble.x, bestSourceMarble.y);
        Point endingEdgePoint = getPlayerEndingEdgePointByColor(currentColor);
        ArrayList<Point> startingCords = getStartingCordsByEndingEdge(endingEdgePoint);

        if (!startingCords.contains(bestSourceMarble)) { //the marble we found best is not in base, check if we have marbles there.
            boolean keepSearching = true;
            for (int i = 0; i < currentPlayersTurn.getMarbles().size() && keepSearching; i++) {
                Point computerMarble = currentPlayersTurn.getMarbles().get(i);
                currentColor = board.getColorByCord(computerMarble.x, computerMarble.y);
                endingEdgePoint = getPlayerEndingEdgePointByColor(currentColor);
                startingCords = getStartingCordsByEndingEdge(endingEdgePoint);
                if (startingCords.contains(computerMarble)) {// we still have a marbel in base!
                    calculatePossibleMovesForPlayer(computerMarble);
                    for (int j = 0; j < possibleMovesForCurrentPickedMarble.size(); j++) {
                        if (checkIfMoveIsGoingToTargetAndGetDistance(computerMarble, possibleMovesForCurrentPickedMarble.get(j), currentColor) != FALSE_VALUE && (!isInOtherBase(computerMarble, possibleMovesForCurrentPickedMarble.get(j)))) {
                            bestSourceMarble.setLocation(computerMarble);
                            bestDestinationMarble.setLocation(possibleMovesForCurrentPickedMarble.get(j));
                            maxSkippingPath.clear();
                            keepSearching = false;
                            gavePiority = true;
                        }
                    }
                }
            }
        }
        return gavePiority;
    }

    private Point calculateAISkipPath(Point computerMarble, Point possibleMove, Color currentColor, ArrayList<Point> skippingPath) {
        skippingPath.clear();
        skippingPath.add(computerMarble);
        boolean keepSkipping = true;
        double distanceFromEnd = checkIfMoveIsGoingToTargetAndGetDistance(computerMarble, possibleMove, currentColor);
        if (distanceFromEnd == FALSE_VALUE) {
            keepSkipping = false;
            skippingPath.clear();
        }

        while (keepSkipping) {
            skippingPath.add(possibleMove);
            calculatePossibleSkipPointsOnly(possibleMove); //all the current player moves is cleared here.

            if ((possibleMovesForCurrentPickedMarble.size() == 1) || (possibleMovesForCurrentPickedMarble.isEmpty())) {
                keepSkipping = false;
            } else {
                Point bestSkip = getBestSkipPoint(possibleMove, currentColor);
                if (checkIfMoveIsGoingToTargetAndGetDistance(possibleMove, bestSkip, currentColor) == FALSE_VALUE) {
                    keepSkipping = false;
                } else {
                    possibleMove = bestSkip; //now we check from here if we can keep skipping forward
                }
            }
        }

        return possibleMove;
    }

    public ArrayList<Point> getPossibleMoves() {
        return possibleMovesForCurrentPickedMarble;
    }

    public void updatePlayerMarblesAfterMove(Point pickedUpMarble, Point userChoice) {
        currentPlayersTurn.getMarbles().remove(pickedUpMarble);
        currentPlayersTurn.getMarbles().add(userChoice);
    }

    public boolean checkIfSkipped(Point pickedUpMarble, Point userChoice) {
        int xDiff = pickedUpMarble.x - userChoice.x;
        int yDiff = pickedUpMarble.y - userChoice.y;
        if ((xDiff == 2) || (xDiff == -2) || (yDiff == 4) || (yDiff == -4)) {
            return true;
        } else {
            return false;
        }
    }

    public void calculatePossibleSkipPointsOnly(Point pickedUpMarble) {
        possibleMovesForCurrentPickedMarble.clear();
        Point skipOnThisPoint;

        for (Direction dir : Direction.values()) {
            skipOnThisPoint = getPointInDirection(pickedUpMarble, dir);
            if (checkIfValidCell(skipOnThisPoint)) {
                if (board.getCharByCord(skipOnThisPoint.x, skipOnThisPoint.y) != EMPTY) { //have some marble there
                    Point possibleMovePoint = getPointInDirection(skipOnThisPoint, dir);
                    if (checkIfValidCell(possibleMovePoint)) {
                        if (board.getCharByCord(possibleMovePoint.x, possibleMovePoint.y) == EMPTY) {
                            possibleMovesForCurrentPickedMarble.add(possibleMovePoint);
                        }
                    }
                }
            }
        }
    }

    private boolean checkIfValidCell(Point pointInBoard) { //
        boolean isEmpty = false;
        if (!isPointOutOfRange(pointInBoard)) {
            if (!board.isWall(pointInBoard.x, pointInBoard.y)) {
                isEmpty = true;
            }
        }
        return isEmpty;
    }

    private ArrayList<Point> getEndingCords(Point edgePoint) {
        ArrayList<Point> endingCords = null;
        if ((edgePoint.x == 1) && (edgePoint.y == 1)) {
            endingCords = ColorSets.greenSetTop;
        } else if ((edgePoint.x == 5) && (edgePoint.y == 1)) {
            endingCords = ColorSets.redSetTopLeft;
        } else if ((edgePoint.x == 5) && (edgePoint.y == 13)) {
            endingCords = ColorSets.whiteSetTopRight;
        } else if ((edgePoint.x == 13) && (edgePoint.y == 1)) {
            endingCords = ColorSets.blackSetBottomLeft;
        } else if ((edgePoint.x == 13) && (edgePoint.y == 13)) {
            endingCords = ColorSets.blueSetBottomRight;
        } else if ((edgePoint.x == 17) && (edgePoint.y == 1)) {
            endingCords = ColorSets.yellowSetBottom;
        }
        return endingCords;
    }

    private double checkIfMoveIsGoingToTargetAndGetDistance(Point sourceMarble, Point movingTo, Color marbleColor) {

        Point XMLEndingPoint = getPlayerEndingEdgePointByColor(marbleColor);
        Point endingEdgePoint = endingXMLPointToBigBoard(XMLEndingPoint);

        double distanceSrc = calculateDistance(sourceMarble, endingEdgePoint);
        double distanceSkippedTo = calculateDistance(movingTo, endingEdgePoint);

        if (distanceSrc <= distanceSkippedTo) {
            distanceSkippedTo = FALSE_VALUE;
        }

        return distanceSkippedTo;
    }

    private Point getPlayerEndingEdgePointByColor(Color marbleColor) {
        Point endingEdgePoint = null;

        for (int i = 0; i < currentPlayersTurn.getColors().size(); i++) {
            if (currentPlayersTurn.getColors().get(i) == marbleColor) {
                endingEdgePoint = currentPlayersTurn.getEndingEdgePoints().get(i);
            }
        }

        return endingEdgePoint;
    }

    private double calculateDistance(Point p1, Point p2) {
        double x1 = p1.getX();
        double x2 = p2.getX();
        double y1 = p1.getY();
        double y2 = p2.getY();

        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    private Point endingXMLPointToBigBoard(Point edgePoint) {
        Point converted = new Point();
        if ((edgePoint.x == 1) && (edgePoint.y == 1)) {
            converted.setLocation(0, 12);
        } else if ((edgePoint.x == 5) && (edgePoint.y == 1)) {
            converted.setLocation(4, 0);
        } else if ((edgePoint.x == 5) && (edgePoint.y == 13)) {
            converted.setLocation(4, 25);
        } else if ((edgePoint.x == 13) && (edgePoint.y == 1)) {
            converted.setLocation(12, 0);
        } else if ((edgePoint.x == 13) && (edgePoint.y == 13)) {
            converted.setLocation(12, 25);
        } else if ((edgePoint.x == 17) && (edgePoint.y == 1)) {
            converted.setLocation(16, 12);
        }
        return converted;
    }

    private Point getBestSkipPoint(Point src, Color marbleColor) {
        Point bestSkipPoint = possibleMovesForCurrentPickedMarble.get(0);
        double minDistance = 999;

        for (int i = 0; i < possibleMovesForCurrentPickedMarble.size(); i++) {
            Point possibleSkip = possibleMovesForCurrentPickedMarble.get(i);
            double tempDistance = checkIfMoveIsGoingToTargetAndGetDistance(src, possibleSkip, marbleColor);
            if (tempDistance < minDistance) {
                minDistance = tempDistance;
                bestSkipPoint = possibleSkip;
            }
        }
        return bestSkipPoint;
    }

    private ArrayList<Point> getStartingCordsByEndingEdge(Point edgePoint) {
        ArrayList<Point> startingCords = new ArrayList<>();
        if ((edgePoint.x == 1) && (edgePoint.y == 1)) {
            startingCords = ColorSets.yellowSetBottom;
        } else if ((edgePoint.x == 5) && (edgePoint.y == 1)) {
            startingCords = ColorSets.blueSetBottomRight;
        } else if ((edgePoint.x == 5) && (edgePoint.y == 13)) {
            startingCords = ColorSets.blackSetBottomLeft;
        } else if ((edgePoint.x == 13) && (edgePoint.y == 1)) {
            startingCords = ColorSets.whiteSetTopRight;
        } else if ((edgePoint.x == 13) && (edgePoint.y == 13)) {
            startingCords = ColorSets.redSetTopLeft;
        } else if ((edgePoint.x == 17) && (edgePoint.y == 1)) {
            startingCords = ColorSets.greenSetTop;
        }
        return startingCords;
    }

    private boolean isInOtherBase(Point bestSourceMarble, Point bestDestinationMarble) {
        boolean isInOtherBase = false;
        Color srcColor = board.getColorByCord(bestSourceMarble.x, bestSourceMarble.y);
        Point endingEdgePoint = getPlayerEndingEdgePointByColor(srcColor);
        ArrayList<Point> endingCords = getEndingCords(endingEdgePoint);
        ArrayList<Point> pointsToCheck = addAllSetsExpectEnding(endingCords);
        if (pointsToCheck.contains(bestDestinationMarble)) {
            isInOtherBase = true;
        }
        return isInOtherBase;
    }

    private boolean reachedEndCords(Point bestSourceMarble) {
        boolean reachedEndCords = false;
        Color srcColor = board.getColorByCord(bestSourceMarble.x, bestSourceMarble.y);
        Point endingEdgePoint = getPlayerEndingEdgePointByColor(srcColor);
        ArrayList<Point> endingCords = getEndingCords(endingEdgePoint);
        if (endingCords.contains(bestSourceMarble)) {
            reachedEndCords = true;
        }
        return reachedEndCords;
    }

    private ArrayList<Point> addAllSetsExpectEnding(ArrayList<Point> endingCords) {
        ArrayList<Point> res = new ArrayList<>();
        res.addAll(ColorSets.blackSetBottomLeft);
        res.addAll(ColorSets.blueSetBottomRight);
        res.addAll(ColorSets.greenSetTop);
        res.addAll(ColorSets.redSetTopLeft);
        res.addAll(ColorSets.whiteSetTopRight);
        res.addAll(ColorSets.yellowSetBottom);
        res.removeAll(endingCords);

        return res;
    }

    public boolean isOnly1PlayerRemaining() {
        int counter = 0;
        for (Player player : players) {
            if (player.isActive()) {
                counter++;
            }
        }

        return counter == 1;
    }

    public void removeCurrentPlayerFromGame() {
        removeColorFromBoard(currentPlayersTurn.getMarbles());
        currentPlayersTurn.setIsActive(false);
    }

    public void initWithSameData() {

        for (Player player : players) {
            player.setIsActive(true);
            player.clearMoves();
            removeColorFromBoard(player.getMarbles());
            player.getMarbles().clear();
            //  for (Point endingCord : player.getEndingEdgePoints()) {
        }
        for (Player player : players) {
            for (int i = 0; i < player.getEndingEdgePoints().size(); i++) {
                Point endingCord = player.getEndingEdgePoints().get(i);
                Color currentColor = player.getColors().get(i);
                ArrayList<Point> startingCords = getStartingCordsByEndingEdge(endingCord);
                player.addMarbles(startingCords);
                updateBoardWithPointsAndColor(startingCords, currentColor);
            }
            currentPlayersTurn = randomFirstPlayerToPlay();
        }
    }

    private void updateBoardWithPointsAndColor(ArrayList<Point> startingCords, Color currentColor) {
        char charToSet = board.colorToChar(currentColor);
        for (Point startingCord : startingCords) {
            board.setCharByCord(charToSet, currentColor, startingCord.x, startingCord.y);
        }
    }

    public boolean isOnlyComputersLeft() {
        boolean onlyComputers = true;
        
        for (Player player : players) {
            if(player.isHuman() && player.isActive()){
                onlyComputers = false;
                break;
            }
        }
        
        return onlyComputers;
    }

}
