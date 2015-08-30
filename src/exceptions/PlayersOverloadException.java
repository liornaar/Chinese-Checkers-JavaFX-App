
package exceptions;

public class PlayersOverloadException extends Exception {
    
    public PlayersOverloadException() {
        super("Error: can only insert amount of human players");
    }
}
