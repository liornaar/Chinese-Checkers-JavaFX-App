
package FXMLUtils;

import chinesecheckersjavafx.GameController;
import chinesecheckersjavafx.GameScene;
import game_manager.GameManager;
import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;

public class FXMLGameLoader {
    
    private static final int MAIN_STAGE_WIDTH = 1070;
    private static final int MAIN_STAGE_HEIGHT = 580;
    
    private FXMLLoader fxmlLoader;
    
       public GameScene createScene(String path) {
        
        try {
            setFXMLLoader(path);
            Parent newGameRoot = getRoot();
            GameScene scene = new GameScene(newGameRoot, MAIN_STAGE_WIDTH, MAIN_STAGE_HEIGHT);
            return scene;
        } catch (Exception ex) {
           //TODO: deal with exception here
        }
       return null; //wont get here usually 
    }
       
       

    public GameController setController(GameManager gameManager) {
        
        GameController controller = (GameController) fxmlLoader.getController();
        controller.setGameManager(gameManager);
        return controller;

        //TODO: add load game listener and property
    }

    private Parent getRoot() throws IOException {
        try{
        return (Parent) fxmlLoader.load(fxmlLoader.getLocation().openStream());
        } catch (Exception e) {
            
        }
        return null;
    }

    private void setFXMLLoader(String filePath) {

        fxmlLoader = new FXMLLoader();
        fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
        URL url = this.getClass().getResource(filePath);
        fxmlLoader.setLocation(url);
        
    }
    
}
