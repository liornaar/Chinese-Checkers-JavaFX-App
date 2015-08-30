package scenes.InitGame;

import chinesecheckersjavafx.ChineseCheckersJavaFX;
import chinesecheckersjavafx.GameController;
import exceptions.DuplicateNameException;
import exceptions.EmptyNameException;
import exceptions.PlayersOverloadException;
import game_manager.GameManager;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class PlayersMenuController extends GameController implements Initializable {

    private static final String STRING_EMPTY = "";
    private static final int MIN_PLAYERS = 2;
    private static final int MAX_PLAYERS_FOR_COLORS = 3;
    private static final int MAX_PLAYERS = 6;
    private static final int MIN_COLORS = 1;
    private static final int MIN_HUMANS = 1;

    @FXML
    private ComboBox totalPlayersCombo;

    @FXML
    private ComboBox colorsForEachCombo;

    @FXML
    private ComboBox humanPlayersCombo;

    @FXML
    private TextField nameText;

    @FXML
    private Button setNameButton;

    @FXML
    private VBox playersListView;

    @FXML
    private Button startGameButton;

    @FXML
    private Label errorLabel;

    private GameManager gameManager;

    private ArrayList<String> playersList = new ArrayList<>();

    private SimpleBooleanProperty startGameProperty = new SimpleBooleanProperty(false);

    public SimpleBooleanProperty getStartGameProperty() {
        return startGameProperty;
    }

    @FXML
    protected void setName(ActionEvent event) {
        String name = nameText.getText();
        try {
            validateName(name);
            //maybe move this to another function
            Label nameLabel = createPlayerLabel(name);
            playersListView.getChildren().add(nameLabel);
            playersList.add(name);
            nameText.setText(STRING_EMPTY);

        } catch (DuplicateNameException | EmptyNameException | PlayersOverloadException e) {
            errorLabel.setText(e.getMessage());
        }

    }

    @FXML
    protected void startGame(ActionEvent event) {
        if (validSettings()) {
            setValueToGameManager();
            gameManager.initGame();
            startGameProperty.set(true);

        } else {
            errorLabel.setText("Error: amount of human players must correspond to the players list size");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ChineseCheckersJavaFX.createFadeTransition(startGameButton, true);
        initCombos();
        setDisableComobos();
        addListeners();
    }

    private void initCombos() {
        totalPlayersCombo.setItems(FXCollections.observableArrayList(2, 3, 4, 5, 6));
        totalPlayersCombo.setValue(MIN_PLAYERS);
        colorsForEachCombo.setItems(FXCollections.observableArrayList(1, 2, 3));
        colorsForEachCombo.setValue(MIN_COLORS);
        humanPlayersCombo.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5, 6));
        humanPlayersCombo.setValue(MIN_HUMANS);
    }

    private void setDisableComobos() {
        setNameButton.disableProperty().bind(Bindings.isEmpty(nameText.textProperty()));
        colorsForEachCombo.disableProperty().bind(Bindings.and(totalPlayersCombo.valueProperty().isNotEqualTo(3), totalPlayersCombo.valueProperty().isNotEqualTo(2)));
    }

    @Override
    public void setGameManager(GameManager manager) {
        gameManager = manager;
    }

    private void addListeners() {
        addPlayersToColorsListener();
        addPlayersToHumanPlayersListener();
        addTextFieldToSetPlayerListener();
        addColorsAndHumanToTotalListener();

    }

    private void setMaxColorsForPlayers(int totalPlayersValue, int comboBoxValue) {
        if (totalPlayersValue == MAX_PLAYERS_FOR_COLORS && comboBoxValue == MAX_PLAYERS_FOR_COLORS) {
            errorLabel.setText("Error: cant have 3 players with 3 colors each");
            colorsForEachCombo.setValue(MIN_PLAYERS);
        }
    }

    private void addPlayersToColorsListener() {
        colorsForEachCombo.valueProperty().addListener((observable, ov, nv) -> {
            setMaxColorsForPlayers((Integer) totalPlayersCombo.getValue(), (Integer) nv);
        });
        setEmptyTextOnHover(colorsForEachCombo);
    }

    private void addPlayersToHumanPlayersListener() {
        humanPlayersCombo.valueProperty().addListener((observable, ov, nv) -> {
            playersListView.getChildren().clear();
            playersList.clear();
            setMaxPlayersForTotalPlayers((Integer) totalPlayersCombo.getValue(), (Integer) nv);
        });
        setEmptyTextOnHover(humanPlayersCombo);
    }

    private void setMaxPlayersForTotalPlayers(int totalPlayers, int humanPlayers) {
        if (totalPlayers < humanPlayers) {
            errorLabel.setText("Error: cant have more human players than total players");
            humanPlayersCombo.setValue(totalPlayers);
        }
    }

    private void setEmptyTextOnHover(ComboBox comboBox) {
        comboBox.hoverProperty().addListener((observable, ov, nv) -> {
            errorLabel.setText(STRING_EMPTY);
        });
    }

    private void addTextFieldToSetPlayerListener() {
        nameText.textProperty().addListener((observable, ov, nv) -> {
            errorLabel.setText(STRING_EMPTY);
        });
    }

    private boolean validSettings() {

        boolean validSettings;
        validSettings = totalPlayersToTable();
        return validSettings;
    }

    private boolean totalPlayersToTable() {
        if (!Objects.equals((Integer) humanPlayersCombo.getValue(), playersList.size())) {
            return false;
        }
        return true;
    }

    private void validateName(String name) throws DuplicateNameException, EmptyNameException, PlayersOverloadException {

        if (name.trim().length() <= 0) {
            throw new EmptyNameException();
        } else if (duplicateName(name)) {
            throw new DuplicateNameException();
        } else if (MaxPlayersExceeded()) {
            throw new PlayersOverloadException();
        }
    }

    private boolean duplicateName(String name) {
        if (playersList.contains(name)) {
            return true;
        }
        return false;
    }

    private boolean MaxPlayersExceeded() {
        if (playersList.size() == (Integer) humanPlayersCombo.getValue()) {
            return true;
        }
        return false;
    }

    private void addColorsAndHumanToTotalListener() {
        totalPlayersCombo.valueProperty().addListener((observable, ov, nv) -> {
            setMaxPlayersForTotalPlayers((Integer) nv, (Integer) humanPlayersCombo.getValue());
            setMaxColorsForPlayers((Integer) colorsForEachCombo.getValue(), (Integer) nv);
            if ((Integer) nv > 3) {
                colorsForEachCombo.setValue(MIN_COLORS);
            }
        });
    }

    private void setValueToGameManager() {
        gameManager.setPlayerNames(playersList);
        gameManager.setNumberOfPlayers((Integer) totalPlayersCombo.getValue());
        gameManager.setNumberOfColorsForEachPlayer((Integer) colorsForEachCombo.getValue());
        gameManager.setNumberOfHumanPlayers((Integer) humanPlayersCombo.getValue());
    }

    private Label createPlayerLabel(String name) {
        Label nameLabel = new Label();
        nameLabel.setText(name);
        nameLabel.setId("playerLabel");
        nameLabel.setPrefWidth(playersListView.getWidth());
        nameLabel.setPrefHeight(40);
        return nameLabel;
    }

}
