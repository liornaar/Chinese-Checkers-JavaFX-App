
package scenes.Game;

import chinesecheckersjavafx.ChineseCheckersJavaFX;
import chinesecheckersjavafx.GameController;
import game_manager.GameManager;
import java.awt.Point;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import logic.CheckersBoard;
import logic.Color;
import logic.Player;

public class CheckersGameController extends GameController implements Initializable {
    
    
    @FXML
    private Button finishTurnButton;
    
    @FXML
    private Button saveGameButton;
    
    @FXML
    private Button retireButton;
    
    @FXML
    private Button restartGameButton;
    
    @FXML
    private Button startNewGameButton;
    
    @FXML
    private Label errorLabel;
    
    @FXML
    private Label msgLabel;

    public Label getErrorLabel() {
        return errorLabel;
    }
    
    @FXML
    private GridPane checkersBoard;
    
    @FXML
    private VBox playersListView;
    
    @FXML
    private VBox colorsListView;
    
    private Button[][] buttonsBoard = new Button[CheckersBoard.ROWS][CheckersBoard.COLS]; 
    
    private Point currentPickedMarble; 
    private GameManager gameManager;
    private boolean isOnSkipRoll = false;
    
    private SimpleBooleanProperty isUserWantsNewGame = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty saveGame = new SimpleBooleanProperty(false);

    

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        ChineseCheckersJavaFX.createFadeTransition(saveGameButton, true);
        ChineseCheckersJavaFX.createFadeTransition(finishTurnButton, true);
        ChineseCheckersJavaFX.createFadeTransition(retireButton, true);

    }
    
    private void run() {
       initGridFromBoard();
       initPlayerList();
       displayPlayerNameInTurn();
       setCurrentPlayerMarblesDisabled(false);
    }
    
    private void initPlayerList(){
        ArrayList<Player> Players = gameManager.getPlayers();
        for (Player playerInGame : Players) {
            Label label = createLabel(playerInGame.getName());
            playersListView.getChildren().add(label);
            String colorsString = "";
            for (Color color : playerInGame.getColors()) {
                colorsString += color.toString().toLowerCase() + ", ";
            }          
            label = createLabel(colorsString.substring(0, colorsString.length()-2));
            colorsListView.getChildren().add(label);
        }
    }
      
    private void setCurrentPlayerMarblesDisabled(boolean state){
        for (Point marble : gameManager.getCurrentPlayer().getMarbles()) {
             buttonsBoard[marble.x][marble.y].setDisable(state);
        }
    }
    
    public void initGridFromBoard() {
        for (int i = 0; i < CheckersBoard.ROWS; i++) {
            for (int j = 0; j < CheckersBoard.COLS; j++) {
                if (!gameManager.getBoard().isWall(i, j)) {
                    buttonsBoard[i][j] = new Button();
                    buttonsBoard[i][j].setDisable(true);
                    buttonsBoard[i][j].setOnAction((ActionEvent e) -> {
                        onMarblePicked(e.getSource());
                    });
                    buttonsBoard[i][j].setId(gameManager.getBoard().getColorByCord(i, j).toString());
                    //todo change constants
                    checkersBoard.add(buttonsBoard[i][j], j + 10, i + 100);
                }
            }
        }
    }

    private void onMarblePicked(Object buttonClicked) {
            //TODO
       Button pickedMarble = (Button) buttonClicked;
       Point pickedMarblePoint = convertButtonToPoint(pickedMarble);
       if(!pickedMarble.getId().equals("POSSIBLEMOVE")){ //picked one of his marbles
           disablePossibleMoves();
           gameManager.clearPossibleMoves();
           fillPossibleMovesButtons(false);          
           currentPickedMarble = pickedMarblePoint;
           if(isOnSkipRoll){
               gameManager.calculateSkipMovesOnly(pickedMarblePoint);
           }else{
               gameManager.calculatePossibleMoves(pickedMarblePoint);
           }
           
           fillPossibleMovesButtons(true);
           
       }else{//pressed on a possible move
           isOnSkipRoll = gameManager.isSkipMove(currentPickedMarble, pickedMarblePoint);
           setSaveOptionCauseSkipped();
           disablePossibleMoves();
           gameManager.clearPossibleMoves();
           moveMarble(pickedMarblePoint);
       }
    }
    
    private void setSaveOptionCauseSkipped(){
        gameManager.setIsUserCanSave(!isOnSkipRoll);
        saveGameButton.setDisable(isOnSkipRoll);
    }

    @FXML
    private void finishTurn(ActionEvent event) {
        disablePossibleMoves();
        endTurn();
    }

    @FXML
    private void saveGame(ActionEvent event) {
        saveGame.set(true);
    }
    
    public SimpleBooleanProperty getSaveGame() {
        return saveGame;
    }

    @FXML
    private void retire(ActionEvent event) {
        removePlayerFromList();
        gameManager.removeCurrentPlayerFromGame();
        removeRetiredPlayerMarbles();
    }
    
    @FXML
    private void restartGame(ActionEvent event) {
        clearData();
        gameManager.initGameWithSameSettings();
        run();
    }
    
    @FXML
    private void startNewGame(ActionEvent event) {
        isUserWantsNewGame.set(true);
    }

    @Override
    public void setGameManager(GameManager manager) {
        gameManager = manager;
        run();
    }
    
    public SimpleBooleanProperty getIsUserWantsNewGame() {
        return isUserWantsNewGame;
    }

    private Point convertButtonToPoint(Button buttonClicked) {
        Point pointInBoard = new Point();
        
        for (int i = 0; i < CheckersBoard.ROWS; i++) {
            for (int j = 0; j < CheckersBoard.COLS; j++) {
                if (buttonsBoard[i][j] != null && buttonsBoard[i][j].equals(buttonClicked)) {
                    pointInBoard.setLocation(i, j);
                    break;
                }
            }
        }
        return pointInBoard;
    }

    private void fillPossibleMovesButtons(boolean toFeel) {//false for clearing
        String id = getIdForPossibleMoveMarble(toFeel);
        for (Point possibleMoveMarble : gameManager.getPossibleMoves()) {
            buttonsBoard[possibleMoveMarble.x][possibleMoveMarble.y].setDisable(!toFeel);
            buttonsBoard[possibleMoveMarble.x][possibleMoveMarble.y].setId(id);
        }
    }

    private void moveMarble(Point movingTo) {
        updateBoardAndPlayerMarbles(movingTo);
        if(!isOnSkipRoll){
            endTurn();
        }else{
            disableAllExpectSkippingMarble(movingTo);           
        }
    }
    
    private void updateBoardAndPlayerMarbles(Point movingTo){
        String idOfCurrentMarble = buttonsBoard[currentPickedMarble.x][currentPickedMarble.y].getId();
        buttonsBoard[currentPickedMarble.x][currentPickedMarble.y].setId("EMPTY");
        buttonsBoard[currentPickedMarble.x][currentPickedMarble.y].setDisable(true);
        buttonsBoard[movingTo.x][movingTo.y].setId(idOfCurrentMarble);
        buttonsBoard[movingTo.x][movingTo.y].setDisable(false);
        gameManager.moveMarble(currentPickedMarble, movingTo);
        currentPickedMarble = movingTo;
    }

    private String getIdForPossibleMoveMarble(boolean toFeel) {
        String id;
        if(toFeel){
            id = "POSSIBLEMOVE";
        }else{
            id = "EMPTY";
        }
        return id;
    }

    private void endTurn() {
        gameManager.getCurrentPlayer().incNumberOfMoves();
        if (gameManager.checkForWinner() != null) {
            printMsgAndGetChoiceForNextGame("And the winner is " + gameManager.getCurrentPlayer().getName() + " with " + gameManager.getCurrentPlayer().getNumberOfMoves() + " moves!");
        } else if (gameManager.isOnlyComputersLeft()) {
            printMsgAndGetChoiceForNextGame("There is no human players left. The game is now over.");
        } else if (gameManager.isOnly1PlayerRemaining()) {
            gameManager.switchTurns();
            printMsgAndGetChoiceForNextGame(gameManager.getCurrentPlayer().getName() + " is the winner, since he is left alone!!");
        } else {
            isOnSkipRoll = false;
            setSaveOptionCauseSkipped();
            setCurrentPlayerMarblesDisabled(true);
            gameManager.switchTurns();
            gameManager.clearPossibleMoves();
            setCurrentPlayerMarblesDisabled(false);
            if (!gameManager.isHumanTurn()) {
                doComputerMove();
            }else{
                displayPlayerNameInTurn();
            }
        }
    }

    private void disablePossibleMoves() {
         for (Point marble : gameManager.getPossibleMoves()) {
             buttonsBoard[marble.x][marble.y].setDisable(true);
             buttonsBoard[marble.x][marble.y].setId("EMPTY");
        }
    }

    private void disableAllExpectSkippingMarble(Point movingTo) {
        setCurrentPlayerMarblesDisabled(true);
        buttonsBoard[movingTo.x][movingTo.y].setDisable(false);
    }

    private void doComputerMove() {
        setHumanTurnButtonsDisabled(true);
        ArrayList<Point> computerMove = gameManager.getAiMove();
        currentPickedMarble = computerMove.get(0);
        for (int i = 1; i < computerMove.size(); i++) {
            updateBoardAndPlayerMarbles(computerMove.get(i));
            currentPickedMarble = computerMove.get(i);
        }
        setHumanTurnButtonsDisabled(false);
        endTurn();

    }

//    private void doComputerMove() {
//        Thread t = new Thread(() -> {
//                //makeComputerMovePath();
//                setHumanTurnButtonsDisabled(true);
//                ArrayList<Point> computerMove = gameManager.getAiMove();
//                currentPickedMarble = computerMove.get(0);
//                for (int i = 1; i < computerMove.size(); i++) {
//                    updateBoardAndPlayerMarbles(computerMove.get(i));
//                    currentPickedMarble = computerMove.get(i);
//                    try {
//                        Thread.sleep(800);
//                    } catch (InterruptedException ex) {
//                        Thread.currentThread().interrupt();
//                    }
//                }
//                setHumanTurnButtonsDisabled(false);
//                endTurn();
//        });
//        t.start();
//        t.interrupt();
//    }
//
//    private void makeComputerMovePath() {
//        setHumanTurnButtonsDisabled(true);
//        ArrayList<Point> computerMove = gameManager.getAiMove();
//                 
//        Iterator<Point> iter = computerMove.iterator();
//        currentPickedMarble = iter.next();
//        
//        while (iter.hasNext()) {
//            Point movingTo = iter.next();
//            updateBoardAndPlayerMarbles(movingTo);
//            currentPickedMarble = movingTo;
//            try {
//                Thread.sleep(0);
//            } catch (InterruptedException ex) {
//                System.out.println("interrupted");
//            }
//        }
//        
//        for (int i = 1; i < computerMove.size(); i++) {
//            updateBoardAndPlayerMarbles(computerMove.get(i));
//            currentPickedMarble = computerMove.get(i);
//            try {
//                Thread.sleep(0);
//            } catch (InterruptedException ex) {
//                System.out.println("interrupted");
//            }
//        }   
//        setHumanTurnButtonsDisabled(false);
//        Platform.runLater(() -> endTurn());
//    }

    private void printMsgAndGetChoiceForNextGame(String msg) {
        //todo css fot this label!
       gameManager.setIsUserCanSave(false);
       msgLabel.setText(msg);
       setTurnButtonsVisible(false);
       setCurrentPlayerMarblesDisabled(true);
    }

    private void removeRetiredPlayerMarbles() {
        for (Point marble : gameManager.getCurrentPlayer().getMarbles()) {
            buttonsBoard[marble.x][marble.y].setId("EMPTY");
            buttonsBoard[marble.x][marble.y].setDisable(true);
        }
        endTurn();
    }

    private void setHumanTurnButtonsDisabled(boolean toDisable) {
        finishTurnButton.setDisable(toDisable);
        saveGameButton.setDisable(toDisable);
        retireButton.setDisable(toDisable);
    }

    private Label createLabel(String name) {
        Label nameLabel = new Label();
        nameLabel.setText(name);
     //   nameLabel.setId("playerLabel");
      //  nameLabel.setPrefWidth(playersListView.getWidth());
        nameLabel.setPrefHeight(15);
        return nameLabel;
    }

    private void displayPlayerNameInTurn() {
        msgLabel.setText("It's " + gameManager.getCurrentPlayer().getName() + " turn");
    }

    private void removePlayerFromList() {
        int index = gameManager.getPlayers().indexOf(gameManager.getCurrentPlayer());
        playersListView.getChildren().remove(index);
        colorsListView.getChildren().remove(index);
    }

    private void clearData() {
        buttonsBoard = new Button[CheckersBoard.ROWS][CheckersBoard.COLS];        
        setTurnButtonsVisible(true);
        gameManager.setIsUserCanSave(true);
        checkersBoard.getChildren().clear();
        playersListView.getChildren().clear();
        colorsListView.getChildren().clear();
    }

    private void setTurnButtonsVisible(boolean visible) {
        finishTurnButton.setVisible(visible);
        saveGameButton.setVisible(visible);
        retireButton.setVisible(visible);
        restartGameButton.setVisible(!visible);
        startNewGameButton.setVisible(!visible);
    }


}
