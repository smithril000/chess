package chess;

/**
 * Indicates an invalid move was made in a game
 */
public class InvalidMoveException extends Exception {

    public InvalidMoveException() {

        //throw new InvalidMoveException("Can't do that");
    }

    public InvalidMoveException(String message) {
        super(message);
    }
}
