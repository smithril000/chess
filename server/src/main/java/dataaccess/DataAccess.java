package dataaccess;

import chess.ChessGame;
import datamodel.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public interface DataAccess {
    void clear();
    void createUser(UserData user);
    UserData getUser(String username);
    //stuff for login data
    void createLoginUser(AuthData authData);
    AuthData getLoggedInData(String username);
    void removeLoggedInUser(String authToken);
    //stuff for game data
    HashMap<Integer, GameData> listGames();
    void createGame(GameData gameData);
    void setWhiteName(String name, int gameID);
    void setBlackName(String name, int gameID);
    ArrayList<BareGameData> getGames();

}
