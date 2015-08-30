package logic;

import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public abstract class FilesManager {

    private static final String RESOURCES_FOLDER_NAME = "Resources";
    private static final String SCHEMA_NAME = "Full6ColorsBoard.txt";

    public static ArrayList<String> readBoardFromFile() { //returns file lines by array of strings.
        Scanner s;
        try {
            s = new Scanner(new File("src/" + RESOURCES_FOLDER_NAME + "/" + SCHEMA_NAME));
        } catch (FileNotFoundException ex) {
            System.out.println("The file " + SCHEMA_NAME + " could not be found in: src/" + RESOURCES_FOLDER_NAME + " folder.");
            return null;
        }
        ArrayList<String> list = new ArrayList<>();
        while (s.hasNextLine()) {
            list.add(s.nextLine());
        }
        s.close();
        return list;
    }
}
