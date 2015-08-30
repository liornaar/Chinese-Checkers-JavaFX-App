package logic;

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.*;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import logic.CheckersBoard.BoardCell;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLHandling {

    private static final String errorOpeningFile = "Error Opening File";
    private static final String RESOURCES_FOLDER_NAME = "Resources";
    private static final String SCHEMA_NAME = "chinese_checkers.xsd";
    private static final boolean LOAD = true;

    private XMLGameData data;
    private static final ArrayList<String> errors = new ArrayList<>();
    private DocumentBuilderFactory builderFactory;
    private DocumentBuilder builder;
    private Document doc;
    private String pathLoaded;
    
    public void getDataFromFile(String pathXML) throws IOException {

        data = new XMLGameData(LOAD);
        pathLoaded = pathXML;
        builderFactory = DocumentBuilderFactory.newInstance();
        errors.clear();

        try {
            builder = builderFactory.newDocumentBuilder();
            doc = builder.parse(new File(pathXML));
            doc.normalize();
            if (validateXMLSchema(pathXML)) {

                Node rootNode = doc.getElementsByTagName("chinese_checkers").item(0);
                Element rootElement = (Element) rootNode;
                getElementsFromDomTree(rootElement);
                if (!errors.isEmpty()) {
                    throw new IOException(errorOpeningFile);
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            throw new IOException(errorOpeningFile);
        }

    }

    public String getPathLoaded() {
        return pathLoaded;
    }

    public static boolean validateXMLSchema(String xmlPath) throws IOException {

        try {
            SchemaFactory factory
                    = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new File("src/" + RESOURCES_FOLDER_NAME + "/" + SCHEMA_NAME));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new File(xmlPath)));

        } catch (IOException | SAXException e) {
            errors.add("The XML file is not a valid file corresponding to the XSD file");
            throw new IOException();
        }
        return true;
    }

    public XMLGameData getGameDataFromXML(String pathXML) throws IOException {
        try {
            getDataFromFile(pathXML);
        } catch (IOException e) {
            throw e;
        }
        return data;
    }


    private void getElementsFromDomTree(Element rootElement) {

        String currentPlayer = rootElement.getAttribute("current_player");

        Element playersElement = (Element) rootElement.getElementsByTagName("players").item(0);

        getPlayersList(playersElement.getElementsByTagName("player"));
        if (!errorWithName(currentPlayer)) {
            errors.add("there isnt a player named " + currentPlayer + " hence cant be the current player");
        } else {
            setCurrentPlayer(currentPlayer);
        }

        Element boardElement = (Element) rootElement.getElementsByTagName("board").item(0);

        getBoard(boardElement.getElementsByTagName("cell"));

    }

    private void getPlayersList(NodeList playersNodeList) {

        ArrayList<Player> players = data.getPlayers();
        Player newPlayer;

        for (int i = 0; i < playersNodeList.getLength(); i++) {
            Element newPlayerElement = (Element) playersNodeList.item(i);
            newPlayer = getPlayerAttributes(newPlayerElement);
            if (!errorWithName(newPlayer.getName())) {
                players.add(newPlayer);
            } else {
                errors.add("the name" + newPlayer.getName() + " is not unique");
                break;
            }
        }
    }

    private Player getPlayerAttributes(Element playerElement) {
        Player newPlayer;
        String name;
        boolean type;
        ArrayList<Color> colors;
        ArrayList<Point> targetPoints = new ArrayList<>();

        name = playerElement.getAttribute("name");
        type = getPlayerType(playerElement.getAttribute("type"));
        colors = getPlayerColor(playerElement.getElementsByTagName("colorDef"), targetPoints);
        
        setTypeOfPlayer(type);
        data.setNumberOfColorsForPlayer(colors.size());

        newPlayer = new Player(name, type);
        addColorToPlayer(newPlayer, colors);
        addTargetPointsToPlayer(newPlayer, targetPoints);

        return newPlayer;
    }

    private ArrayList<Color> getPlayerColor(NodeList ColorList, ArrayList<Point> targetPoints) {

        Element ColorElement, targetElement;
        ArrayList<Color> playerColors = new ArrayList<>();

        for (int i = 0; i < ColorList.getLength(); i++) {

            ColorElement = (Element) ColorList.item(i);
            targetElement = (Element) ColorElement.getElementsByTagName("target").item(0);
            playerColors.add(Color.valueOf(ColorElement.getAttribute("color")));
            targetPoints.add(new Point(getPointFromTargetAttribute(targetElement.getAttribute("row"), targetElement.getAttribute("col"))));

        }
        return playerColors;
    }

    private Point getPointFromTargetAttribute(String row, String col) {

        Point p = null;

        try {
            p = new Point(Integer.parseInt(row), Integer.parseInt(col));
            if (!data.getTargetPoints().contains(p)) {
                errors.add("target point is not an official game target point");
            }
            if (p.x < 0 || p.x > XMLGameData.BOARD_H || p.y < 0 || p.y > XMLGameData.BOARD_W || data.getMaxColsForEachRow().get(p.x) < p.y) {
                errors.add("target point is out of bounds");
            }

        } catch (NumberFormatException e) {
            errors.add("target points are not valid");
        }

        if (data.getTotalTargetPoints().contains(p)) {
            errors.add("each color has only 1 target point");
            data.getTotalTargetPoints().clear();
        }
        return p;
    }

    private boolean getPlayerType(String type) {
        boolean res;

        if ("HUMAN".equals(type)) {
            res = true;
        } else if ("COMPUTER".equals(type)) {
            res = false;
        } else {
            errors.add("type of player not supported");
            res = false;
        }

        return res;
    }

    private void getBoard(NodeList cellList) {
        int row, col;
        char color;
        char[][] board = new char[XMLGameData.BOARD_H][XMLGameData.BOARD_W + 1];
        for (int i = 0; i < cellList.getLength(); i++) {
            Element cell = (Element) cellList.item(i);
            row = getCoord(cell, "row");
            col = getCoord(cell, "col");
            if (data.getMaxColsForEachRow().get(row) < col || row < 0 || row > XMLGameData.BOARD_H) {
                errors.add("one or more of the input cells is out of bounds");
                return;
            }
            color = getColor(cell);
            board[row - 1][col] = color;
        }

        data.createBoardFromXML(board);
        if (!data.checkIfPlayerHasAllItsPoints()) {
            errors.add("there should be exactly 10 marbles for each color for each player");
        }

    }

    private int getCoord(Element cell, String whatToCheck) {
        String row = cell.getAttribute(whatToCheck);
        int rowInt = 0;
        try {
            rowInt = Integer.parseInt(row);
        } catch (Exception e) {
            errors.add("row is not a number");
        }

        return rowInt;
    }

    private char getColor(Element cell) {
        char color = cell.getAttribute("color").charAt(0);
        if (color == 'B') {
            color = cell.getAttribute("color").equals("BLACK") ? 'B' : 'N';
        }

        return color;
    }

    private void setCurrentPlayer(String currentPlayer) {

        ArrayList<Player> players = data.getPlayers();

        for (Player player : players) {
            if (player.getName().equals(currentPlayer)) {
                data.setCurrentPlayersTurn(player);
                break;
            }
        }
    }

    private void setTypeOfPlayer(boolean type) {
        if (type) {
            data.addHumanPlayer();
        } else {
            data.addComputerPlayer();
        }

    }

    private void addColorToPlayer(Player newPlayer, ArrayList<Color> colors) {

        for (Color color : colors) {
            newPlayer.addColor(color);
        }
    }

    public void saveGameToXML(XMLGameData gameData, String path) throws IOException {

        if (path == null) {
            path = pathLoaded;
        }
        
        File fileToSave = null;

        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        try {
            builderFactory = DocumentBuilderFactory.newInstance();
            builder = builderFactory.newDocumentBuilder();

            DOMImplementation impl = builder.getDOMImplementation();

            doc = impl.createDocument(null, null, null);

            // root elements
            doc = builder.newDocument();
            Element rootElement = doc.createElement("chinese_checkers");
            doc.appendChild(rootElement);
            rootElement.setAttribute("xmlns:xsi", XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
            rootElement.setAttribute("current_player", gameData.getCurrentPlayersTurn().toString());
            rootElement.setAttribute("xsi:noNamespaceSchemaLocation", "chinese_checkers.xsd");
            rootElement.appendChild(getPlayersElements(gameData.getPlayers()));
            rootElement.appendChild(getBoardelements(gameData.getBoard()));
            
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            fileToSave = new File(path);
            StreamResult result = new StreamResult(fileToSave);

            transformer.transform(source, result);

        } catch (ParserConfigurationException | TransformerException ex) {
            throw new IOException("error saving file, path not found");

        }
        
    }

    private Node getPlayersElements(ArrayList<Player> players) {

        Element playerListElement = doc.createElement("players");

        for (Player player : players) {
            if (player.isActive()) {
                playerListElement.appendChild(createPlayerElement(player));
            }
        }

        return playerListElement;

    }

    private Node createPlayerElement(Player player) {

        Integer targetIndex = 0;

        Element playerElement = doc.createElement("player");
        playerElement.setAttribute("type", player.isHuman() ? "HUMAN" : "COMPUTER");
        playerElement.setAttribute("name", player.getName());

        for (Color color : player.getColors()) {
            playerElement.appendChild(createColorElement(color, player.getEndingEdgePoints().get(targetIndex++)));
        }

        return playerElement;
    }

    private Node createColorElement(Color color, Point target) {

        Element colorElement = doc.createElement("colorDef");
        colorElement.setAttribute("color", color.toString());
        colorElement.appendChild(createTargetelement(target));
        return colorElement;

    }

    private Node createTargetelement(Point target) {
        Element targetElement = doc.createElement("target");
        targetElement.setAttribute("row", Integer.toString(target.x));
        targetElement.setAttribute("col", Integer.toString(target.y));

        return targetElement;
    }

    private Node getBoardelements(CheckersBoard board) {

        Element boardElement = doc.createElement("board");
        Color color;

        for (int row = 0; row < CheckersBoard.ROWS; row++) {
            for (int col = 0; col < CheckersBoard.COLS; col++) {
                color = board.getColorByCord(row, col);
                if (color != Color.EMPTY) {
                    boardElement.appendChild(createCellElement(row, col, board, color));
                }

            }
        }

        return boardElement;
    }

    private Node createCellElement(int row, int col, CheckersBoard board, Color color) {

        Element cellElement = doc.createElement("cell");
        BoardCell cell = board.getCellByCoord(row, col);
        col = getColomnForWriting(row, col, cell, board);
        row++;

        cellElement.setAttribute("row", Integer.toString(row));
        cellElement.setAttribute("col", Integer.toString(col));
        cellElement.setAttribute("color", color.toString());

        return cellElement;

    }

    private int getColomnForWriting(int row, int col, BoardCell cell, CheckersBoard board) {

        int count = 0;
        int counterToEmpty = 0;
        while (board.getCharByCord(row, counterToEmpty) == CheckersBoard.WALL) {
            counterToEmpty++;
        }
        while (cell != board.getCellByCoord(row, counterToEmpty)) {
            if (board.getCharByCord(row, counterToEmpty) != CheckersBoard.WALL) {
                count++;
            }

            counterToEmpty++;
        }
        return count + 1;
    }

    public static ArrayList<String> getErrors() {
        return errors;
    }

    private boolean errorWithName(String name) {

        for (Player player : data.getPlayers()) {
            if (player.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    private void addTargetPointsToPlayer(Player newPlayer, ArrayList<Point> targetPoints) {

        ArrayList<Point> points = newPlayer.getEndingEdgePoints();

        for (Point point : targetPoints) {
            points.add(point);
        }
    }

 }
