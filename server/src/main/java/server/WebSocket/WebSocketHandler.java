package server.WebSocket;

import chess.ChessGame;
import com.google.gson.Gson;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;
import websocket.messages.GameMessages;
import websocket.messages.NotiMessages;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        try {
            UserGameCommand action = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            switch (action.getCommandType()) {
                case CONNECT -> enter(action, ctx.session);
                //case EXIT -> exit(action.visitorName(), ctx.session);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void enter(UserGameCommand action, Session session) throws IOException {
        //we send a loadgame just to client, notification to everyone
        //sending the load_game - we need to find the game
        GameMessages gameMessage = new GameMessages(action.getGame(), action.getPlayerColor());
        //sending jus the game
        session.getRemote().sendString(new Gson().toJson(gameMessage));
        connections.add(session);
        var message = (action.getName() + " joined the game as " + action.getPlayerColor());
        var notification = new NotiMessages(message);
        connections.broadcast(session, notification);
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }
}