package server.websocket;

import chess.*;
import com.google.gson.Gson;
import dataaccess.DatabaseManager;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import model.ResponseException;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessages;
import websocket.messages.GameMessages;
import websocket.messages.NotiMessages;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    static Map<Session, Integer> gameSessions = new HashMap<>();

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
                case MAKE_MOVE ->  makeMove(action, ctx.session);
                case RESIGN -> resign(action, ctx.session);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    private void resign(UserGameCommand action, Session session) throws IOException {
        //get game

        //first verify auth
        if(!checkAuth(action.getAuthToken())){
            ErrorMessages mess = new ErrorMessages("Error, unauthorized");
            session.getRemote().sendString(new Gson().toJson(mess));
            return;
        }
        ChessGame game = getGame(action.getGameID(), session);

        //make sure the game isn't marked as done
        assert game != null;

        //make sure we arent an observer
        if(getPlayerData(action, session) == null){
            ErrorMessages mess = new ErrorMessages("Error, unauthorized");
            session.getRemote().sendString(new Gson().toJson(mess));
            return;
        }

        //make sure we haven't already resigned
        if(game.isDone()){
            ErrorMessages mess = new ErrorMessages("Error, already done");
            session.getRemote().sendString(new Gson().toJson(mess));
            return;
        }

        game.setDone(true);
        try {
            setGame(game, action.getGameID());
        }catch(Exception ex){

        }
        //send out the notis
        NotiMessages noti = new NotiMessages(getPlayerData(action, session) + "resigned");
        connections.add(session);
        connections.broadcast(session, noti);
        session.getRemote().sendString(new Gson().toJson(noti));
    }

    private String getPlayerData(UserGameCommand action, Session session){
        String que = "SELECT username FROM authData WHERE authToken =?";
        String username = "";
        //get the username to get the color
        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(que)) {
            preparedStatement.setString(1, action.getAuthToken());

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    username = new Gson().fromJson(rs.getString("username"), String.class);
                }
            }
        }
        catch (SQLException | ResponseException ex) {
            //if we got here we need to throw an error message
            ErrorMessages mess = new ErrorMessages("Error, cant find game");
        }
        //now that we got the username, use it to get the right color
        //get the game
        que = "SELECT whiteUsername, blackUsername FROM games WHERE id=?";
        String white = "";
        String black = "";
        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(que)) {
            preparedStatement.setString(1, String.valueOf(action.getGameID()));

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    white = new Gson().fromJson(rs.getString("whiteUsername"), String.class);
                    black = new Gson().fromJson(rs.getString("blackUsername"), String.class);
                }
            }


        }
        catch (SQLException | ResponseException ex) {
            //if we got here we need to throw an error message
            ErrorMessages mess = new ErrorMessages("Error, cant find game");
        }
        if(Objects.equals(white, username)){
            return "WHITE";
        }else if(Objects.equals(black, username)){
            return "BLACK";
        }
        return null;
    }

    private void makeMove(UserGameCommand action, Session session) throws IOException {
        //first verify auth
        if(!checkAuth(action.getAuthToken())){
            ErrorMessages mess = new ErrorMessages("Error, unauthorized");
            session.getRemote().sendString(new Gson().toJson(mess));
            return;
        }

        //so we want to get the game
        ChessGame game = getGame(action.getGameID(), session);
        //make sure the game isn't marked as done
        assert game != null;
        if(game.isDone()){
            ErrorMessages er = new ErrorMessages("Game is over");
            session.getRemote().sendString(new Gson().toJson(er));
            return;
        }
        //check if game is over
        assert game != null;
        if(game.isInCheckmate(ChessGame.TeamColor.WHITE) || game.isInCheckmate(ChessGame.TeamColor.BLACK)){
            ErrorMessages er = new ErrorMessages("Game is over");
            session.getRemote().sendString(new Gson().toJson(er));
            return;
        }
        //make sure the piece we got is our players color
        if(!game.getBoard().getPiece(action.getMove().getStartPosition()).getTeamColor().toString().equalsIgnoreCase(getPlayerData(action, session))){
            //add error handlers
            ErrorMessages er = new ErrorMessages("Invalid move attempted");
            session.getRemote().sendString(new Gson().toJson(er));
            return;
        }
        assert game != null;
        //make the move - add piece at move, delete piece at start
        try{
            game.makeMove(action.getMove());

            setGame(game, action.getGameID());

            //send ws to re-draw everones board
            GameMessages gameMessage = new GameMessages(game);
            broadcastGame(session, action.getGameID(), game, true);
            broadcastGame(session, action.getGameID(), game, false);
            //send noti message
            NotiMessages noti = new NotiMessages(action.getName() + " made a move");
            broadcastMessage(session, action.getGameID(), action.getName() + " made a move", false);
            //update root too
//            session.getRemote().sendString(new Gson().toJson(gameMessage));
            //check if game is over
            if(game.isInCheckmate(ChessGame.TeamColor.WHITE) || game.isInCheckmate(ChessGame.TeamColor.BLACK)){
                noti = new NotiMessages("Game is over");
                session.getRemote().sendString(new Gson().toJson(noti));
                connections.broadcast(session, noti);
                return;
            }
        }catch(Exception ex){
            //add error handlers
            System.out.println("Found an error");
            ErrorMessages er = new ErrorMessages("Invalid move attempted");
            session.getRemote().sendString(new Gson().toJson(er));
        }
        //now we need to check if game is over

    }

    private void setGame(ChessGame game, int id) throws ResponseException {
        String upd = "UPDATE games SET game=? WHERE id=?";
        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(upd)) {
            preparedStatement.setString(1, new Gson().toJson(game));
            preparedStatement.setInt(2, id);
            preparedStatement.executeUpdate();
        } catch (SQLException | ResponseException ex) {
            //if we got here we need to throw an error message
            ErrorMessages mess = new ErrorMessages("Error, cant find game");
        }
    }

    private void exit(UserGameCommand action, Session session) throws IOException {
        //we need to broadcast a message that they are leaving and remove the player in db
        //first get the player out of the game - maybe just connect db here?
        //get the player in game from db
        ChessGame game;

        String stuff = "";
        if(getPlayerData(action, session) == null){
            //we are just an observer
            
        }else {
            if (getPlayerData(action, session).equalsIgnoreCase("WHITE")) {
                stuff = "UPDATE games SET whiteUsername=? WHERE id=?";
            } else {
                System.out.println(action.getPlayerColor() + ", " + action.getGameID());
                stuff = "UPDATE games SET blackUsername=? WHERE id=?";
            }
            try (var conn = DatabaseManager.getConnection();
                 var statement = conn.prepareStatement(stuff)) {
                statement.setNull(1, Types.VARCHAR);
                statement.setInt(2, action.getGameID());
                statement.executeUpdate();
            } catch (SQLException | ResponseException e) {
                throw new RuntimeException(e);
            }
        }

        //now we need to broadcast that people left
        var message = getPlayerData(action, session) + " left the game";
        NotiMessages noti = new NotiMessages(message);
        broadcastMessage(session, action.getGameID(), message, false);
        gameSessions.remove(session);
        connections.remove(session);

    }

    private void enter(UserGameCommand action, Session session) throws IOException, ResponseException {
        //we send a loadgame just to client, notification to everyone

        //before anything check auth
        if(!checkAuth(action.getAuthToken())){
            //end - go no further
            ErrorMessages mess = new ErrorMessages("Error, unauthorized");
            session.getRemote().sendString(new Gson().toJson(mess));
            return;
        }
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

            if(game == null){
                ErrorMessages mess = new ErrorMessages("Error, cant find game");
                session.getRemote().sendString(new Gson().toJson(mess));
            }else {
                GameMessages gameMessage = new GameMessages(game);
                //sending just the game
                //session.getRemote().sendString(new Gson().toJson(gameMessage));
                connections.add(session);
                gameSessions.put(session, action.getGameID());
                broadcastGame(session, action.getGameID(), game, true);
                String message = (getPlayerData(action, session) + " joined the game as " + action.getPlayerColor());
                var notification = new NotiMessages(message);
                broadcastMessage(session, action.getGameID(), message, false);
            }

        } catch (SQLException | ResponseException ex) {
            //if we got here we need to throw an error message
            ErrorMessages mess = new ErrorMessages("Error, cant find game");
            connections.add(session);
            connections.broadcast(session, mess);
        }
        

    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private ChessGame getGame(int gameId, Session session) throws IOException {
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

            if(game == null){
                ErrorMessages mess = new ErrorMessages("Error, cant find game");
                session.getRemote().sendString(new Gson().toJson(mess));
            }else{
                return game;
            }

        } catch (SQLException | ResponseException | IOException ex) {
            //if we got here we need to throw an error message
            ErrorMessages mess = new ErrorMessages("Error, cant find game");
            connections.add(session);
            connections.broadcast(session, mess);
        }
        return null;
    }

    private Boolean checkAuth(String auth){
        //use this function to see if we can connect to auth
        String sql = "SELECT username FROM authData WHERE authToken=?";
        String username = null;
        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setString(1, auth);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    username = rs.getString("username");
                }
                System.out.println(username);
                return username != null;
            }
        }catch(ResponseException | SQLException _){

        }
        return true;
    }
    public void broadcastGame(Session session, Integer gameID, ChessGame game, boolean self) throws IOException {
        System.out.println("BROADCASTING GAME");
        if(self){
            session.getRemote().sendString(new Gson().toJson(new GameMessages(game)));
        }
        else{
            for (Session gameSession : gameSessions.keySet()) {
                if(Objects.equals(gameSessions.get(gameSession), gameID)){
                    if(session != gameSession && gameSession.isOpen()){
                        gameSession.getRemote().sendString(new Gson().toJson(new GameMessages(game)));
                    }
                }
            }
        }
    }
    public void broadcastMessage(Session session, Integer gameID, String message, boolean self) throws IOException {
        System.out.println("BROADCASTING MESSAGE: " + message);
        if(self){
            session.getRemote().sendString(new Gson().toJson(new NotiMessages(message)));
        }
        else{
            for (Session gameSession : gameSessions.keySet()) {
                if(Objects.equals(gameSessions.get(gameSession), gameID)){
                    if(session != gameSession && gameSession.isOpen()){
                        gameSession.getRemote().sendString(new Gson().toJson(new NotiMessages(message)));
                    }
                }
            }
        }
    }
}