package service;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DatabaseManager;
import dataaccess.MemoryDataAccess;
import dataaccess.ResponseException;
import dataaccess.DataBaseAccess;
import model.*;
import org.mindrot.jbcrypt.BCrypt;

import java.util.*;

public class UserService {
    private final DataBaseAccess dataAccess;
    public UserService(DataBaseAccess dataAccess){
        this.dataAccess = dataAccess;
    }

    public AuthData register(UserData user) throws ResponseException {
        //first do some checks
        if(user.username() == null || user.password() == null){
            throw new ResponseException(400, "Error: bad request");
        }
        else if(dataAccess.getUser(user.username())!= null){
            throw new ResponseException(403, "Error: already exists");
        }
        //we need to creat both the userdata and the authdata
        dataAccess.createUserDate(user);
        //create the auth data
        String auth = generateToken();
        //we can create the authData here
        AuthData authData = new AuthData(user.username(), auth);
        dataAccess.pushAuthData(authData);
        return authData;
    }

    public String generateToken() {
        return UUID.randomUUID().toString();
    }

    public void clear() throws ResponseException{
        dataAccess.clear();
    }

    private Boolean checkPass(String clear, String hash){
        return BCrypt.checkpw(clear, hash);
    }

    public AuthData login(UserData userData) throws ResponseException{
        //do checks
        if(userData.username() == null || userData.password() == null){ // no user or pass entered
            throw new ResponseException(400, "Error, bad request(no login data)");
        }
        //get the user for code clarity
        UserData user = dataAccess.getUser(userData.username());
        if(dataAccess.getUser(userData.username())== null){
            throw new ResponseException(401, "Error, bad request(doesn't exist)");
        }else {
            assert user != null;
            if(!checkPass(userData.password(), user.password())){
                throw new ResponseException(401, "Error, unauthorized");
            }
        }
        //create new auth
        var auth = generateToken();
        var authData = new AuthData(userData.username(), auth);
        //we need to add
        dataAccess.pushAuthData(authData);
        return authData;
    }

    public void logout(String auth) throws ResponseException {
        //make sure we have auth in the db
        checkAuth(auth);
        //now that we found the auth we can remove
        dataAccess.removeFromAuths(auth);
    }
    public void checkAuth(String auth) throws ResponseException{
        //make sure we have auth in the db
        String username =  dataAccess.getUsernameByAuth(auth);
        if(username == null){
            throw new ResponseException(401, "Error, something went wrong(auth check gone bad)");
        }
    }

    public GameID createGame(GameName gameNameData) throws ResponseException {
        String name = gameNameData.gameName();
        if(name == null){
            throw new ResponseException(400, "Error, bad request");
        }
        //lets create the actual chess game
        ChessGame newGame = new ChessGame();
        String jsonString = new Gson().toJson(newGame);
        return dataAccess.createGame(name, jsonString);
    }

    public void joinGame(JoinGameRequest join, String auth) throws ResponseException {
        String color = join.playerColor();
        int id = join.gameID();
        if(id == 0){
            throw new ResponseException(400, "Error, bad request");
        }else if(!Objects.equals(color, "WHITE")){
            if(!Objects.equals(color, "BLACK")){
                throw new ResponseException(400, "Error, bad request");
            }
        }
        if(!dataAccess.checkColor(color, id)){
            throw new ResponseException(403, "Error, color taken");
        }
        String username = dataAccess.getUsernameByAuth(auth);
        System.out.println("Got through service fine");
        dataAccess.joinGame(color, username, id);
    }

    public GamesReturned getGames() throws ResponseException {
        HashMap<Integer, Game> games = dataAccess.getGames();
        //we need to nly get the values, not the keys
        List<Game> gamesToReturn = new ArrayList<>();
        for(HashMap.Entry<Integer, Game> entry : games.entrySet()){
            gamesToReturn.add(entry.getValue());
        }
        return new GamesReturned(gamesToReturn);
    }
}
