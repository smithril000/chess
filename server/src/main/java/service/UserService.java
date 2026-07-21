package service;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.MemoryDataAccess;
import dataaccess.ResponseException;
import model.*;

import java.util.*;

public class UserService {
    public UserService(){

    }

    public static AuthData register(UserData user) throws ResponseException {
        //first do some checks
        if(user.username() == null || user.password() == null){
            throw new ResponseException(400, "Error: bad request");
        }
        else if(MemoryDataAccess.getUser(user.username())!= null){
            throw new ResponseException(403, "Error: already exists");
        }
        //we need to creat both the userdata and the authdata
        DatabaseManager.createUserDate(user);
        //create the auth data
        String auth = generateToken();
        //we can create the authData here
        AuthData authData = new AuthData(user.username(), auth);
        DatabaseManager.pushAuthData(authData);
        return authData;
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public static void clear() throws ResponseException{
        DatabaseManager.clear();
    }

    public static AuthData login(UserData userData) throws ResponseException{
        //do checks
        if(userData.username() == null || userData.password() == null){ // no user or pass entered
            throw new ResponseException(400, "Error, bad request(no login data)");
        }else if(DatabaseManager.getUser(userData.username())== null){
            throw new ResponseException(401, "Error, bad request(doesn't exist)");
        }else if(!Objects.equals(DatabaseManager.getUser(userData.username()).password(), userData.password())){
            throw new ResponseException(401, "Error, unauthorized");
        }
        //create new auth
        var auth = generateToken();
        var authData = new AuthData(userData.username(), auth);
        //we need to add
        DatabaseManager.pushAuthData(authData);
        return authData;
    }

    public static void logout(String auth) throws ResponseException {
        //make sure we have auth in the db
        checkAuth(auth);
        //now that we found the auth we can remove
        DatabaseManager.removeFromAuths(auth);
    }
    public static void checkAuth(String auth) throws ResponseException{
        //make sure we have auth in the db
        String username =  DatabaseManager.getUsernameByAuth(auth);
        if(username == null){
            throw new ResponseException(401, "Error, something went wrong(auth check gone bad)");
        }
    }

    public static GameID createGame(GameName gameNameData) throws ResponseException {
        String name = gameNameData.gameName();
        if(name == null){
            throw new ResponseException(400, "Error, bad request");
        }
        //lets create the actual chess game
        ChessGame newGame = new ChessGame();
        String jsonString = new Gson().toJson(newGame);
        return DatabaseManager.createGame(name, jsonString);
    }

    public static void joinGame(JoinGameRequest join, String auth) throws ResponseException {
        String color = join.playerColor();
        int id = join.gameID();
        if(id == 0){
            throw new ResponseException(400, "Error, bad request");
        }else if(!Objects.equals(color, "WHITE")){
            if(!Objects.equals(color, "BLACK")){
                throw new ResponseException(400, "Error, bad request");
            }
        }
        if(!DatabaseManager.checkColor(color, id)){
            throw new ResponseException(403, "Error, color taken");
        }
        String username = DatabaseManager.getUsernameByAuth(auth);
        System.out.println("Got through service fine");
        DatabaseManager.joinGame(color, id, username);
    }

    public static GamesReturned getGames() {
        var games = MemoryDataAccess.getGames();
        //we need to nly get the values, not the keys
        List<Game> gamesToReturn = new ArrayList<>();
        for(HashMap.Entry<Integer, Game> entry : games.entrySet()){
            gamesToReturn.add(entry.getValue());
        }
        return new GamesReturned(gamesToReturn);
    }
}
