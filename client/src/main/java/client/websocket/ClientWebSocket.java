package client.websocket;

import com.google.gson.Gson;

import model.ResponseException;

import jakarta.websocket.*;
import websocket.messages.NotiMessages;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

public class ClientWebSocket extends Endpoint{
    Session session;

    public void setColor(String color) {
        this.color = color;
    }

    String color;

    public ClientWebSocket(String url) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler((MessageHandler.Whole<String>) this::messageHandle);
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private void messageHandle(String message) {
        ServerMessage stuff = new Gson().fromJson(message, ServerMessage.class);
        //now we have a json object of our info
        if(Objects.equals(stuff.getServerMessageType(), ServerMessage.ServerMessageType.NOTIFICATION)){
            NotiMessages gameMessage = new Gson().fromJson(message, NotiMessages.class);
            //for this one we just need to output the Notification
            System.out.println("\n" + gameMessage.getMessage() + "\n" + ">> ");
        }else if(Objects.equals(stuff.getServerMessageType(), ServerMessage.ServerMessageType.LOAD_GAME)) {
            //now we print the board
            System.out.println("\n >>");

        }else if(Objects.equals(stuff.getServerMessageType(), ServerMessage.ServerMessageType.ERROR)){
            //we just out the error
            System.out.println(message);
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
