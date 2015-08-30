package chinesecheckersjavafx;

import FXMLUtils.FXMLGameLoader;
import java.io.IOException;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import game_manager.*;
import java.io.File;
import javafx.animation.FadeTransition;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import logic.XMLGameData;
import logic.XMLHandling;
import scenes.Game.CheckersGameController;
import scenes.NewGame.NewGameController;
import scenes.InitGame.PlayersMenuController;

public class ChineseCheckersJavaFX extends Application {

    private static final String MAIN_MENU_SCENE_FXML = "newGameScreen.fxml";
    private static final String PLAYERS_SCENE_FXML = "InitGame.fxml";
    private static final String GAME_SCENE_FXML = "game.fxml";

    private Stage gameStage;
    private GameManager gameManager;
    private GameScene mainMenu;
    private GameScene newGameScene;
    private GameScene loadGameScene;

    private GameScene boardScene;
    private FXMLGameLoader fxmlLoader = new FXMLGameLoader();
    private XMLGameData dataFromXML;
    private SimpleBooleanProperty gameWasLoaded = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty gameWasSaved = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty loadFaild = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty saveFileFromQuit = new SimpleBooleanProperty(false);
    private String nameOfLoadedFile;

    @Override
    public void start(Stage primaryStage) throws IOException {

        gameStage = primaryStage;
        gameManager = new GameManager();

        createMainMenu();

        setOnCloseButtonEvent();
        primaryStage.setTitle("Chinese Checkers");
        primaryStage.setResizable(false);
        primaryStage.setScene(mainMenu);
        primaryStage.show();
    }

    public static void main(String[] args) {
        try {
            launch(args);
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    private void setNextScenesForMainMenu(NewGameController newGameController) {

        newGameController.getNewGameProperty().addListener((source, oldValue, newValue) -> {
            if (newValue) {
                newGameScene = fxmlLoader.createScene(PLAYERS_SCENE_FXML);
                PlayersMenuController playersMenuController = (PlayersMenuController) fxmlLoader.setController(gameManager);
                setNextSceneForPlayersSettings(playersMenuController);
                gameStage.setScene(newGameScene);
            }
        });

        newGameController.getLoadGameProperty().addListener((source, oldValue, newValue) -> {
            if (newValue) {
                loadGame(newGameController);
                newGameController.getLoadGameProperty().set(false);

            }
        });

    }

    private void loadGame(NewGameController newGameController) {
        loadedStateListeners(newGameController);
        XMLFilesHandlingFX fileChooser = new XMLFilesHandlingFX();
        File xmlFile = fileChooser.createFileChooser(gameStage, true, nameOfLoadedFile);
        if (xmlFile != null) {
            nameOfLoadedFile = xmlFile.getAbsolutePath();
            loadFromNewThread(xmlFile);
        }
    }

    private void loadedStateListeners(NewGameController newGameController) {
        this.gameWasLoaded.addListener((src, ov, nv) -> {
            if (nv) {
                gameManager.initGameFromXML(dataFromXML);
                createNewGameScene();
                newGameController.getLoadGameProperty().set(false);
            }
        });

        this.loadFaild.addListener((src, ov, nv) -> {
            if (nv) {
                nameOfLoadedFile = "";
                newGameController.getErrorLabel().setText("Error: file is currupted");
                createFadeTransition(newGameController.getErrorLabel(), false);
                newGameController.getLoadGameProperty().set(false);

            }
        });
    }

    private void gameWasSavedSuccesfullyListener(CheckersGameController checkersGameController) {
        this.gameWasSaved.addListener((src, ov, nv) -> {
            if (nv) {
                checkersGameController.getErrorLabel().setText("Saved!");
                createFadeTransition(checkersGameController.getErrorLabel(), false);
            }
        });
    }

    private void setNextSceneForPlayersSettings(PlayersMenuController playersSettingController) {
        playersSettingController.getStartGameProperty().addListener((source, oldValue, newValue) -> {
            if (newValue) {
                createNewGameScene();
            }
        });
    }

    private void createMainMenu() {
        mainMenu = fxmlLoader.createScene(MAIN_MENU_SCENE_FXML);
        NewGameController newGameController = (NewGameController) fxmlLoader.setController(gameManager);
        setNextScenesForMainMenu(newGameController);
    }

    private void setNextSceneForGame(CheckersGameController checkersGameController) {
        checkersGameController.getIsUserWantsNewGame().addListener((source, oldValue, newValue) -> {
            if (newValue) {
                createMainMenu();
                gameStage.setScene(mainMenu);
            }
        });

        checkersGameController.getSaveGame().addListener((source, oldValue, newValue) -> {
            if (newValue) {
                saveGame(checkersGameController);
                checkersGameController.getSaveGame().set(false);
            }
        });

        this.saveFileFromQuit.addListener((source, oldValue, newValue) -> {
            if (newValue) {
                saveGame(checkersGameController);
                this.saveFileFromQuit.set(false);
            }
        });
    }

    private void saveGame(CheckersGameController checkersGameController) {

        gameWasSavedSuccesfullyListener(checkersGameController);
        XMLFilesHandlingFX fileChooser = new XMLFilesHandlingFX();
        File xmlFile = fileChooser.createFileChooser(gameStage, false, nameOfLoadedFile);
        if (xmlFile != null) {
            saveFromNewThread(xmlFile);
            checkersGameController.getSaveGame().set(false);
        }
    }

    private void createNewGameScene() {
        boardScene = fxmlLoader.createScene(GAME_SCENE_FXML);
        CheckersGameController checkersGameController = (CheckersGameController) fxmlLoader.setController(gameManager);
        setNextSceneForGame(checkersGameController);
        gameStage.setScene(boardScene);
    }

    private void loadFromNewThread(File xmlFile) {

        Thread xmlThread = new Thread() {
            @Override
            public void run() {
                try {
                    dataFromXML = new XMLHandling().getGameDataFromXML(xmlFile.getAbsolutePath());
                    Platform.runLater(() -> {
                        gameWasLoaded.set(true);
                        gameWasLoaded.set(false);
                    });
                } catch (Exception ex) {
                    Platform.runLater(() -> {
                        loadFaild.set(true);
                        loadFaild.set(false);

                    });
                }
            }
        };
        xmlThread.start();
    }

    private void saveFromNewThread(File file) {
        dataFromXML = gameManager.getDataFromGame();
        Thread xmlThread = new Thread() {
            @Override
            public void run() {
                try {
                    new XMLHandling().saveGameToXML(dataFromXML, file.getAbsolutePath());
                    Platform.runLater(() -> {
                        gameWasSaved.set(true);
                        gameWasSaved.set(false);
                    });

                } catch (Exception ex) {
                    System.out.println("there was an error saving the file to the disk");
                }
            }
        };
        xmlThread.start();
    }

    public SimpleBooleanProperty getGameWasLoaded() {
        return gameWasLoaded;
    }

    private void setOnCloseButtonEvent() {
        gameStage.setOnCloseRequest((final WindowEvent event) -> {
            event.consume();
            exitStageOnCloseRequest();

        });
    }

    private void exitStageOnCloseRequest() {
        Stage exitStage = new Stage();
        exitStage.initModality(Modality.APPLICATION_MODAL);
        if (gameStage.getScene().equals(boardScene) && gameManager.isUserCanSave()) {
            createExitStageOnBoardScene(exitStage);
        } else {
            createExitStageOnOtherScenes(exitStage);
        }
    }

    private void createExitStageOnBoardScene(Stage exitStage) {
        Label label = new Label("Do you want to save before exit?");
        Button exitSaveButton = new Button("Save");
        Button exitCancelButton = new Button("Cancel");
        Button exitExitButton = new Button("Exit");

        exitExitButton.setOnAction((ActionEvent event) -> {
            exitStage.close();
            gameStage.close();

        });
        exitCancelButton.setOnAction((ActionEvent event) -> {
            exitStage.hide();

        });

        exitSaveButton.setOnAction((ActionEvent event) -> {
            this.saveFileFromQuit.set(true);
            exitStage.hide();
        });
        createExitStage(label, exitStage, exitSaveButton, exitExitButton, exitCancelButton);

    }

    private void createExitStage(Label label, Stage exitStage, Button... buttons) {
        FlowPane pane = new FlowPane(10, 10);
        pane.setAlignment(Pos.CENTER);
        pane.getChildren().addAll(buttons);
        VBox vBox = new VBox(20);
        vBox.setAlignment(Pos.CENTER);
        vBox.getChildren().addAll(label, pane);
        Scene scene = new Scene(vBox);
        exitStage.setScene(scene);
        exitStage.setResizable(false);
        exitStage.show();
    }

    private void createExitStageOnOtherScenes(Stage exitStage) {
        Label label = new Label("Are you sure you want to quit?");
        Button exitQuitButton = new Button("Yes");
        Button exitCancelButton = new Button("Cancel");

        exitQuitButton.setOnAction((ActionEvent event) -> {
            exitStage.close();
            gameStage.close();
        });

        exitCancelButton.setOnAction((ActionEvent event) -> {
            exitStage.hide();
        });

        createExitStage(label, exitStage, exitQuitButton, exitCancelButton);
    }

    public static void createFadeTransition(Parent node, boolean fadeIn) {
        FadeTransition ft = new FadeTransition(Duration.millis(2500), node);
        ft.setFromValue(fadeIn == true ? 0.0 : 1.0);
        ft.setToValue(fadeIn == true ? 1.0 : 0.0);
        ft.play();
    }

}
