package websocket.messages;

import chess.ChessGame;

public class GameMessages extends ServerMessage{
    private final ChessGame game;
    private String color;

    public GameMessages(ChessGame game, String color){
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
        this.color = color;
    }

    public ChessGame getGame(){
        return game;
    }
    public String getColor(){
        if(color.equalsIgnoreCase("an observer")){
            return "WHITE";
        }
        return color;
    }

}
