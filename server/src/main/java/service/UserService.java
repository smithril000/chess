package service;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccess;
import datamodel.AuthData;
import datamodel.BareGameData;
import datamodel.GameData;
import datamodel.UserData;
import server.ResponseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class UserService {
    private final DataAccess dataAccess;
    public UserService(DataAccess dataAccess){
        this.dataAccess = dataAccess;
    }

    public AuthData register(UserData user) throws ResponseException {
        if(user.username() == null || user.password() == null){
            throw new ResponseException(400, "Error: bad request");
        }
        else if(dataAccess.getUser(user.username())!= null){
            throw new ResponseException(403, "Error: already exists");
        }
        dataAccess.createUser(user);

        var authData = new AuthData(user.username(), generateAuth());
        dataAccess.createLoginUser(authData);
        return authData;
    }

    public AuthData login(UserData user) throws ResponseException{
        if(user.username() == null || user.password() == null){
            //havent registered yet
            throw new ResponseException(400,"Error: bad request");
        }
        //since the username does exist make sure the password is the same
        else if(dataAccess.getUser(user.username()) == null){
            //wrong password
            throw new ResponseException(401, "Error: Unauthorized");
        }else if(!dataAccess.verifyUser(user.username(), user.password())){//!user.password().equals(dataAccess.getUser(user.username()).password())

            throw new ResponseException(401, "Error: Unauthorized");
        }
        var authData = new AuthData(user.username(), generateAuth());
        dataAccess.createLoginUser(authData);
        return authData;
    }

    public void logout(String authToken) throws ResponseException{
        if(dataAccess.getLoggedInData(authToken) == null){
            //TEST
            var test = dataAccess.getLoggedInData(authToken);
            //auth token not in data base
            throw new ResponseException(401, "Error: unauthorized");
        }else{
            //remove the authtoken fomr the temp memory
            dataAccess.removeLoggedInUser(authToken);
        }
    }

    public void clear(){
        dataAccess.clear();
    }
    //finsih this
    private String generateAuth(){
        return UUID.randomUUID().toString();
    }

    public int createGame(String gameName, String authToken) throws ResponseException{
        //check auth
        if(gameName == null){
            throw new ResponseException(400, "Error: bad request");
        }
        else if(dataAccess.getLoggedInData(authToken) == null){
            throw new ResponseException(401, "Error: unauthorized");
        }
        ChessGame game = new ChessGame();
        //fix this
        int gameID = dataAccess.getID();
        //create the actualy game data
        GameData gameData = new GameData(gameID, null, null, gameName, game);
        dataAccess.createGame(gameData);
        return gameID;
    }

    public void joinGame(int gameID, String authToken, String playerColor) throws ResponseException{
        //do all fo the checks
        //String testAuth = dataAccess.getLoggedInData(authToken).authToken();
        if(gameID == 0){
            throw new ResponseException(400, "Error: bad request");
        }else if(dataAccess.getLoggedInData(authToken) == null){
            throw new ResponseException(401, "Error: unauthorized");
        }else if(Objects.equals(playerColor, "WHITE") && dataAccess.getGame(gameID).getWhiteUsername() != null){
            throw new ResponseException(403, "Error: already taken");
        }else if(Objects.equals(playerColor, "BLACK") && dataAccess.getGame(gameID).getBlackUsername() != null){
            throw new ResponseException(403, "Error: already taken");
        }else if(!Objects.equals(playerColor, "WHITE")){
            if(!Objects.equals(playerColor, "BLACK")){
                throw new ResponseException(400, "Error: bad request");
            }
        }
        //now get the user info to set name and to know what color to do
        //first grab players username
        String username = dataAccess.getLoggedInData(authToken).username();
        if(Objects.equals(playerColor, "BLACK")){
            dataAccess.setBlackName(username, gameID);
        }else{
            dataAccess.setWhiteName(username, gameID);
        }

    }
    public ArrayList<GameData> listGames(String authToken) throws ResponseException{
        if(dataAccess.getLoggedInData(authToken) == null){
            throw new ResponseException(401, "Error: unauthorized");
        }
        //we need to return the map without the actaul game


        return dataAccess.getGames();
    }
    public String checkAuth(String reqJson) throws ResponseException{
        if(reqJson.contains(" ")){
            throw new ResponseException(401, "Error: Unauthorized");
        }else{
            return new Gson().fromJson(reqJson, String.class);
        }
    }
}
