package dataaccess;

import chess.ChessGame;
import datamodel.AuthData;
import datamodel.BareGameData;
import datamodel.GameData;
import datamodel.UserData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MemoryDataAccess implements DataAccess{
    private static final Logger log = LoggerFactory.getLogger(MemoryDataAccess.class);
    private final HashMap<String, UserData> users = new HashMap<>();
    private final HashMap<String, AuthData> loggedInUsers = new HashMap<>();
    private final HashMap<Integer, GameData> games = new HashMap<>();
    private int gameID_Counter = 1;
    @Override
    public void clear() {
        loggedInUsers.clear();
        users.clear();
        games.clear();
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
        games.put(gameData.getGameID(), gameData);
    }
    @Override
    public void setWhiteName(String name, int gameID){
        games.get(gameID).setWhiteUsername(name);
    }
    public void setBlackName(String name, int gameID){
        games.get(gameID).setBlackUsername(name);
    }
    @Override
    public ArrayList<BareGameData> getGames(){
        ArrayList<BareGameData> bareGames = new ArrayList<>();
        for(int id : games.keySet()){
            //check if any of the names are null
            String blackName = games.get(id).getBlackUsername();
            String whiteName = games.get(id).getWhiteUsername();
//            if(blackName == null){
//                blackName = "null";
//            }
//            if(whiteName == null){
//                whiteName = "null";
//            }

            bareGames.add(new BareGameData(id, whiteName, blackName, games.get(id).getGameName()));
        }
        return bareGames;
    }
    @Override
    public GameData getGame(int gameID){
        return games.get(gameID);
    }
    public int getID(){
        return gameID_Counter++;
    }
}
