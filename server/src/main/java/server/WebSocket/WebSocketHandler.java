package server.WebSocket;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import model.GameID;
import model.ResponseException;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;
import websocket.messages.GameMessages;
import websocket.messages.NotiMessages;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

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
                case LEAVE -> exit(action, ctx.session);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    private void exit(UserGameCommand action, Session session) throws IOException {
        //we need to broadcast a message that they are leaving and remove the player in db
        //first get the player out of the game - maybe just connect db here?
        //get the player in game from db
        ChessGame game;

        String stuff;
        if(action.getPlayerColor().equalsIgnoreCase("WHITE")){
            System.out.println("It thisnks its white");
            stuff = "UPDATE games SET whiteUsername=? WHERE id=?";
        }else{
            System.out.println(action.getPlayerColor() + ", " + action.getGameID());
            stuff = "UPDATE games SET blackUsername=? WHERE id=?";
        }
        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(stuff)) {
            statement.setNull(1, Types.VARCHAR);
            statement.setString(2, action.getGameID().toString());
            statement.executeUpdate();
        } catch (SQLException | ResponseException e) {
            throw new RuntimeException(e);
        }

        //now we need to broadcast that people left
        var message = action.getName() + " left the game";
        NotiMessages noti = new NotiMessages(message);
        connections.broadcast(session, noti);

    }

    private void enter(UserGameCommand action, Session session) throws IOException, ResponseException {
        //we send a loadgame just to client, notification to everyone
        //sending the load_game - we need to find the game - not thorugh action
        int gameId = action.getGameID();
        //use db to get the game
        ChessGame game = null;
        String query = "SELECT game FROM games WHERE id=?";

        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setString(1, String.valueOf(gameId));

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    game =  new Gson().fromJson(rs.getString("game"), ChessGame.class);
                }
            }

        } catch (SQLException | ResponseException ex) {
            throw new ResponseException(500, "Error, failed to execute statement " + ex.getMessage());
        }
        
        GameMessages gameMessage = new GameMessages(game, action.getPlayerColor());
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