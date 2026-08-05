package client.websocket;

import chess.ChessGame;
import com.google.gson.Gson;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.ResponseException;

import jakarta.websocket.*;
import server.Server;
import ui.DrawBoard;
import websocket.commands.UserGameCommand;
import websocket.messages.GameMessages;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

public class ClientWebSocket extends Endpoint{
    Session session;
    ServerMessage message;

    public ClientWebSocket(String url) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    System.out.println(message);
                    messageHandle(message);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private void messageHandle(String message) {
        ServerMessage stuff = new Gson().fromJson(message, ServerMessage.class);
        System.out.println("Got to the message handler");
        //now we have a json object of our info
        if(Objects.equals(stuff.getServerMessageType(), ServerMessage.ServerMessageType.NOTIFICATION)){
            GameMessages gameMessage = new Gson().fromJson(message, GameMessages.class);

            ChessGame game = gameMessage.getGame();
            DrawBoard board = new DrawBoard();
            board.draw(game, "WHITE");
        }
    }


    public void sendCommand(String command){
        this.session.getAsyncRemote().sendText(command);
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }


}
