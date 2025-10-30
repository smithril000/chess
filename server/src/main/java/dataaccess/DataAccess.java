package dataaccess;

import chess.ChessGame;
import datamodel.*;
import server.ResponseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public interface DataAccess {
    void clear() throws ResponseException;
    void createUser(UserData user) throws ResponseException;
    UserData getUser(String username) throws ResponseException;
    //stuff for login data
    void createLoginUser(AuthData authData) throws ResponseException;
    AuthData getLoggedInData(String username) throws ResponseException;
    void removeLoggedInUser(String authToken) throws ResponseException;
    //stuff for game data
    HashMap<Integer, GameData> listGames();
    void createGame(GameData gameData) throws ResponseException;
    void setWhiteName(String name, int gameID) throws ResponseException;
    void setBlackName(String name, int gameID) throws ResponseException;
    ArrayList<GameData> getGames() throws ResponseException;
    GameData getGame(int gameID) throws ResponseException;
    int getID(String gameName) throws ResponseException;

    boolean verifyUser(String username, String password) throws ResponseException;
    //boolean verifyUser(String username, String password);
}
