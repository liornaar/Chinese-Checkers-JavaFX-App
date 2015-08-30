
package exceptions;

public class DuplicateNameException extends Exception {
    
    public DuplicateNameException() {
        super("Error: Duplicate name");
    }
}
