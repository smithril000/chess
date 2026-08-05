package websocket.messages;

public class NotiMessages extends ServerMessage{
    String message;

    public NotiMessages(String message) {
        super(ServerMessage.ServerMessageType.NOTIFICATION);
        this.message = message;
    }

    public String getMessage(){
        return this.message;
    }

}
