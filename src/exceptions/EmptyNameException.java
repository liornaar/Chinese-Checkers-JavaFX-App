
package exceptions;

public class EmptyNameException extends Exception {
    
    public EmptyNameException () {
        super("Error: Cannot insert an empty name");
    }
}
