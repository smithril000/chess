package dataaccess;

import chess.ChessGame;
import datamodel.AuthData;
import datamodel.GameData;
import datamodel.UserData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class MemoryDataAccess implements DataAccess{
    private static final Logger log = LoggerFactory.getLogger(MemoryDataAccess.class);
    private final HashMap<String, UserData> users = new HashMap<>();
    private final HashMap<String, AuthData> loggedInUsers = new HashMap<>();
    private final HashMap<Integer, GameData> games = new HashMap<>();
    @Override
    public void clear() {
        loggedInUsers.clear();
        users.clear();
    }

    @Override
    public void createUser(UserData user) {
        users.put(user.username(), user);
    }

    @Override
    public UserData getUser(String username) {
        return users.get(username);
    }

    @Override
    public void createLoginUser(AuthData authData){
        loggedInUsers.put(authData.authToken(), authData);
    }
    @Override
    public AuthData getLoggedInData(String authToken){
        return loggedInUsers.get(authToken);
    }
    @Override
    public void removeLoggedInUser(String authToken){
        loggedInUsers.remove(authToken);
    }
    @Override
    public HashMap<Integer, GameData> listGames(){
        return games;
    }
    @Override
    public void createGame(GameData gameData){
        games.put(gameData.gameID(), gameData);
    }
}
