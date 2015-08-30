
package scenes.NewGame;

import chinesecheckersjavafx.ChineseCheckersJavaFX;
import chinesecheckersjavafx.GameController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import game_manager.*;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;


public class NewGameController extends GameController implements Initializable {
    
    @FXML
    private Button newGameButton;
    
    @FXML
    private Button loadGameButton;
    
    @FXML
    private Label errorLabel;
    
    @FXML
    private BorderPane bigPane;

    
    private SimpleBooleanProperty newGameProperty = new SimpleBooleanProperty(false); 
    private SimpleBooleanProperty loadGameProperty = new SimpleBooleanProperty(false);
    
    private GameManager gameManager;
    
    public void setGameManager (GameManager manager) {
        gameManager = manager;
    }
    
     public Label getErrorLabel() {
        return errorLabel;
    }
    
    @FXML
    protected void newGame(ActionEvent event) {
       newGameProperty.set(true);      
    }
    
    public SimpleBooleanProperty getNewGameProperty() {
        return newGameProperty;
    }
    
    @FXML
    protected void loadGame(ActionEvent event) {
        loadGameProperty.set(true);
    }

    public SimpleBooleanProperty getLoadGameProperty() {
        return loadGameProperty;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ChineseCheckersJavaFX.createFadeTransition(newGameButton, true);
        ChineseCheckersJavaFX.createFadeTransition(loadGameButton, true);
    }

}



    

