package websocket.messages;

public class ErrorMessages extends ServerMessage{
    String errorMessage;

    public ErrorMessages(String errorMessage) {
        super(ServerMessageType.ERROR);
        this.errorMessage = errorMessage;
    }
    public String getMessage() {
        return errorMessage;
    }
}
