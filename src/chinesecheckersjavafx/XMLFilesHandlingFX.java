package chinesecheckersjavafx;

import java.io.File;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class XMLFilesHandlingFX {

    private FileChooser fileChooser;

    public File createFileChooser(Stage primaryStage, boolean load, String fileName) {
        getFileChooserXML(load, fileName);

        File xmlFileChosen = loadOrSaveFile(primaryStage, load);

        return xmlFileChosen;
    }

    private void getFileChooserXML(boolean load, String fileName) {
        fileChooser = new FileChooser();
        fileChooser.setTitle(load == true ? "Select an XML file" : "Save XML file");
        if (!load) {
            fileChooser.setInitialFileName(fileName);
        }
        fileChooser.getExtensionFilters().clear();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML files", "*.xml"));

    }

    private File loadOrSaveFile(Stage stage, boolean load) {
        File newFile;
        if (load) {
            newFile = fileChooser.showOpenDialog(stage);
        } else {
            newFile = fileChooser.showSaveDialog(stage);
        }

        return newFile;
    }

}
