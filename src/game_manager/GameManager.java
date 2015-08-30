package game_manager;

import logic.*;
import java.awt.Point;
import java.util.ArrayList;


public class GameManager {

    private int numberOfPlayers;
    private int numberOfHumanPlayers;
    private int numberOfColorsForEachPlayer;
    private ArrayList<String> playerNames = new ArrayList<>();
    private boolean userQuited = false;
    private final GameEngine checkersEngine = new GameEngine();
    private boolean wasGameLoaded = false;
    private XMLHandling xmlHandler;
    private boolean isUserCanSave = true;
    
    public ArrayList<Player> getPlayers(){
        return checkersEngine.getPlayers();
    }

    public ArrayList<String> getPlayerNames() {
        return playerNames;
    }
    
    public boolean isHumanTurn(){
        return checkersEngine.isHumanTurn();
    }
    
     public void removeCurrentPlayerFromGame(){
        checkersEngine.removeCurrentPlayerFromGame();
    }
    
    public boolean isOnly1PlayerRemaining(){
        return checkersEngine.isOnly1PlayerRemaining();
    }
    
    public ArrayList<Point> getAiMove(){
        return checkersEngine.playAITurn();
    }
    
    public CheckersBoard getBoard(){
        return checkersEngine.getBoard();
    }
    
    public ArrayList<Point> getPossibleMoves(){
        return checkersEngine.getPossibleMoves();
    }
    
    public Player getCurrentPlayer(){
        return checkersEngine.getCurrentPlayerTurn();
    }
       
    public void playAITurn(){
        checkersEngine.playAITurn();
    }
    
    public void clearPossibleMoves(){
        checkersEngine.getPossibleMoves().clear();
    }
    
    public void calculatePossibleMoves(Point marble){
        checkersEngine.calculatePossibleMovesForPlayer(marble);
    }
    
    public void calculateSkipMovesOnly(Point marble){
        checkersEngine.calculatePossibleSkipPointsOnly(marble);
    }
    
    public boolean isSkipMove(Point Pickedmarble, Point possibleMove){
        return checkersEngine.checkIfSkipped(Pickedmarble, possibleMove);
    }
    
    public void moveMarble(Point currentPickedMarble, Point movingTo){
        checkersEngine.updateBoardWithNewPoint(currentPickedMarble, movingTo);
        checkersEngine.updatePlayerMarblesAfterMove(currentPickedMarble, movingTo);
    }
    
    public Player checkForWinner(){
        return checkersEngine.checkForWinner();
    }
    public void switchTurns(){
        checkersEngine.switchTurns();
    }
    
    public void initGame() {
        checkersEngine.initNewGame(playerNames, numberOfPlayers, numberOfHumanPlayers, numberOfColorsForEachPlayer);
    }
    
    public void initGameFromXML(XMLGameData data) {
        checkersEngine.initNewGameFromXML(data);
    }

    public boolean isDuplicateName(String name) {
        if (playerNames.contains(name)) {
            return true;
        }
        return false;
    }

    public void addPlayerToList(String name) {
        playerNames.add(name);
    }

    public void setNumberOfPlayers(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }

    public void setNumberOfHumanPlayers(int numberOfHumanPlayers) {
        this.numberOfHumanPlayers = numberOfHumanPlayers;
    }

    public void setNumberOfColorsForEachPlayer(int numberOfColorsForEachPlayer) {
        this.numberOfColorsForEachPlayer = numberOfColorsForEachPlayer;
    }

    public void setPlayerNames(ArrayList<String> playerNames) {
        this.playerNames = playerNames;
    }

    public XMLGameData getDataFromGame() {
        XMLGameData data = new XMLGameData();
        data.setBoard(checkersEngine.getBoard());
        data.setCurrentPlayersTurn(checkersEngine.getCurrentPlayerTurn());
        data.setPlayers(checkersEngine.getPlayers());
        return data;
    }

    public boolean isOnlyComputersLeft() {
        return checkersEngine.isOnlyComputersLeft();
    }

    public void initGameWithSameSettings() {
        checkersEngine.initWithSameData();
    }

    public void setIsUserCanSave(boolean b) {
        isUserCanSave = b;
    }
    
    public boolean isUserCanSave(){
        return isUserCanSave;
    }
}
