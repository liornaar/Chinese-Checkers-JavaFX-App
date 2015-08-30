
package logic;

import java.awt.Point;
import java.util.ArrayList;

public abstract class ColorSets {
    static ArrayList<Point> greenSetTop = new ArrayList<>();   
    static ArrayList<Point> whiteSetTopRight = new ArrayList<>();   
    static ArrayList<Point> blueSetBottomRight = new ArrayList<>();
    static ArrayList<Point> yellowSetBottom = new ArrayList<>();   
    static ArrayList<Point> blackSetBottomLeft = new ArrayList<>();    
    static ArrayList<Point> redSetTopLeft = new ArrayList<>();   
    
    public static void clearAll(){
        greenSetTop.clear();
        whiteSetTopRight.clear();
        blueSetBottomRight.clear();
        yellowSetBottom.clear();
        blackSetBottomLeft.clear();
        redSetTopLeft.clear();
    }
}
 



   