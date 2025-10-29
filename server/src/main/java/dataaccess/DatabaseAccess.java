package dataaccess;

import com.google.gson.Gson;
import datamodel.AuthData;
import datamodel.BareGameData;
import datamodel.GameData;
import datamodel.UserData;
import server.ResponseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseAccess implements DataAccess{
    public DatabaseAccess(){
        try {
            configureDatabase();
        }catch(DataAccessException ex){
            System.out.printf("bad");
        }
    }
    @Override
    public void clear() {
        var statment = new String[]{"TRUNCATE TABLE users", "TRUNCATE TABLE authData", "TRUNCATE TABLE games"};
        try {
            executeUpdate(statment);
        }catch(DataAccessException ex){

        }
    }

    @Override
    public void createUser(UserData user) {
        //basically register user
        //can i get the whole object as a json string?
        String jsonString = new Gson().toJson(user);
        var statement = new String[] {String.format("insert into users (username, email, password, json) values ('%s', '%s', '%s', '%s')", user.username(), user.email(), user.password(), jsonString)};
        try{
            executeUpdate(statement);
        }catch(DataAccessException ex){

        }
    }
    private UserData readUser(ResultSet rs) throws SQLException {
        var username = rs.getString("username");
        var json = rs.getString("json");
        UserData user = new Gson().fromJson(json, UserData.class);
        return user;
    }

    @Override
    public UserData getUser(String username) {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, json FROM users WHERE username=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs);
                    }
                }
            }
        } catch (Exception e){

        }
        return null;
    }

    @Override
    public void createLoginUser(AuthData authData) {
        //adding to my authdata database
        String jsonString = new Gson().toJson(authData);
        var statement = new String[] {String.format("insert into authData (username, authToken, json) values ('%s', '%s', '%s')", authData.username(), authData.authToken(), jsonString)};
        try{
            executeUpdate(statement);
        }catch(DataAccessException ex){

        }
    }

    @Override
    public AuthData getLoggedInData(String authToken) {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = String.format("SELECT authToken, json FROM authData WHERE authToken='%s'", authToken);
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                //ps.setString(1, authToken);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readAuth(rs);
                    }
                }
            }
        } catch (Exception e){

        }

        return null;
    }
    private AuthData readAuth(ResultSet rs) throws SQLException {
        var authToken = rs.getString("authToken");
        var json = rs.getString("json");
        AuthData data = new Gson().fromJson(json, AuthData.class);
        return data;
    }

    @Override
    public void removeLoggedInUser(String authToken) {

    }

    @Override
    public HashMap<Integer, GameData> listGames() {
        return null;
    }

    @Override
    public void createGame(GameData gameData) {
        String jsonString = new Gson().toJson(gameData);
        var statement = new String[] {String.format("insert into games (gameID, whiteUsername, blackUsername, gameName, json) values ('%s', '%s', '%s', '%s', '%s')",gameData.getGameID(), gameData.getWhiteUsername(), gameData.getBlackUsername(), gameData.getGameName(), jsonString)};
        try{
            executeUpdate(statement);
        }catch(DataAccessException ex){

        }
    }

    @Override
    public void setWhiteName(String name, int gameID) {
        var statement = new String[] {String.format("UPDATE games SET whiteUsername = '%s' WHERE gameID = '%s'",name, gameID)};
        try{
            executeUpdate(statement);
        }catch(DataAccessException ex){

        }
    }

    @Override
    public void setBlackName(String name, int gameID) {
        var statement = new String[] {String.format("UPDATE games SET blackUsername = '%s' WHERE gameID = '%s'",name, gameID)};
        try{
            executeUpdate(statement);
        }catch(DataAccessException ex){

        }
    }

    @Override
    public ArrayList<BareGameData> getGames() {
        return null;
    }

    @Override
    public GameData getGame(int gameID) {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = String.format("SELECT json FROM games WHERE gameID='%s'", gameID);
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                //ps.setString(1, authToken);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                }
            }
        } catch (Exception e){

        }


        return null;
    }
    private GameData readGame(ResultSet rs) throws SQLException {
        var json = rs.getString("json");
        var game = new Gson().fromJson(json, GameData.class);
        return game;
    }

    @Override
    public int getID() {
        //FIX THIS
        return 1;
    }
    private final String[] createUserStatements = {
            """
            CREATE TABLE IF NOT EXISTS  users (
              `id` int NOT NULL AUTO_INCREMENT,
              `username` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `json` TEXT DEFAULT NULL,
              PRIMARY KEY (`username`),
              INDEX(id)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS  authData (
              `id` int NOT NULL AUTO_INCREMENT,
              `username` varchar(256) NOT NULL,
              `authToken` varchar(256) NOT NULL,
              `json` TEXT DEFAULT NULL,
              PRIMARY KEY (`username`),
              INDEX(id)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS  games (
              `gameID` int NOT NULL AUTO_INCREMENT,
              `whiteUsername` varchar(256) NOT NULL,
              `blackUsername` varchar(256) NOT NULL,
              `gameName` varchar(256) NOT NULL,
              `json` TEXT DEFAULT NULL,
              PRIMARY KEY (`gameID`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private void configureDatabase() throws DataAccessException {
        //temp remove the database for testing
        //clear();
        DatabaseManager.createDatabase();
        try(var conn = DatabaseManager.getConnection()){
            for(var statement : createUserStatements){
                try(var preparedStatment = conn.prepareStatement(statement)){
                    preparedStatment.executeUpdate();
                }
            }
        }catch(SQLException ex){
            throw new DataAccessException("Bad Database");
        }
    }
    private void executeUpdate(String[] statements)throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()){
            for(var statement : statements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        }catch(SQLException ex){
            throw new DataAccessException("Bad Database");
        }
    }
}
