package dataaccess;

import com.google.gson.Gson;
import datamodel.AuthData;
import datamodel.BareGameData;
import datamodel.GameData;
import datamodel.UserData;
import org.mindrot.jbcrypt.BCrypt;
import server.ResponseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class DatabaseAccess implements DataAccess{
    private ArrayList<GameData> games = new ArrayList<>();

    public DatabaseAccess(){
        try {
            configureDatabase();
        }catch(DataAccessException ex){
            System.out.printf("bad");
        }
    }
    @Override
    public void clear() throws ResponseException {
        var statment = new String[]{"TRUNCATE TABLE users", "TRUNCATE TABLE authData", "TRUNCATE TABLE games"};

        try (var conn = DatabaseManager.getConnection()){
            var statement1 = "TRUNCATE TABLE users";
            var statement2 = "TRUNCATE TABLE authData";
            var statement3 = "TRUNCATE TABLE games";
            try (var ps = conn.prepareStatement(statement1)) {

                ps.executeUpdate();
            }
            try (var ps = conn.prepareStatement(statement2)) {

                ps.executeUpdate();
            }
            try (var ps = conn.prepareStatement(statement3)) {

                ps.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new ResponseException(500, "Error in database");
        }
    }

    @Override
    public void createUser(UserData user) throws ResponseException {
        //basically register user
        //can i get the whole object as a json string?

        //we need to hash the passwords
        String hashPass = hashPass(user.password());
        UserData newUser = new UserData(user.username(), user.email(), hashPass);
        String jsonString = new Gson().toJson(newUser);
        var statement = new String[] {String.format("insert into users (username, email, password, json) values ('%s', '%s', '%s', '%s')", user.username(), user.email(), hashPass, jsonString)};
        try{
            executeUpdate(statement);
        }catch(DataAccessException ex){
            throw new ResponseException(500, "Error with database");
        }
    }
    private String hashPass(String pass){
        String hashedPassword = BCrypt.hashpw(pass, BCrypt.gensalt());
        return hashedPassword;
    }

    public boolean verifyUser(String username, String providedClearTextPassword) throws ResponseException {
        // read the previously hashed password from the database
        var hashedPassword = getUser(username).password();

        return BCrypt.checkpw(providedClearTextPassword, hashedPassword);
    }

    private UserData readUser(ResultSet rs) throws SQLException {
        var username = rs.getString("username");
        var json = rs.getString("json");
        UserData user = new Gson().fromJson(json, UserData.class);
        //rehash the pass

        return user;
    }

    @Override
    public UserData getUser(String username) throws ResponseException {
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
            throw new ResponseException(500, "Error with database");
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
    public AuthData getLoggedInData(String authToken) throws ResponseException {
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
            throw new ResponseException(500, "Error with database");
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
    public void removeLoggedInUser(String authToken) throws ResponseException {
        var statment = new String[]{String.format("DELETE FROM authData WHERE authToken = '%s'", authToken)};
        try {
            executeUpdate(statment);
        }catch(DataAccessException ex){
            throw new ResponseException(500, "Error with database");
        }
    }

    @Override
    public HashMap<Integer, GameData> listGames() {
        return null;
    }

    @Override
    public void createGame(GameData gameData) throws ResponseException {
        String jsonString = new Gson().toJson(gameData);
        var statement = "insert into games (whiteUsername, blackUsername, gameName, json) values (?,?,?,?)";
        try (var conn = DatabaseManager.getConnection()){

            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, gameData.getWhiteUsername());
                ps.setString(2,gameData.getBlackUsername());
                ps.setString(3, gameData.getGameName());
                ps.setString(4,jsonString);
                ps.executeUpdate();
            }


        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
        //now we might have to update again
        GameData updated = getGame2(gameData.getGameName());
        String newJson = new Gson().toJson(updated);
        statement = "UPDATE games SET json = ? WHERE gameName = ?";
        try (var conn = DatabaseManager.getConnection()){

            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, newJson);
                ps.setString(2,gameData.getGameName());

                ps.executeUpdate();
            }


        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private GameData getGame2(String name) throws ResponseException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID ,whiteUsername, blackUsername, json FROM games WHERE gameName =?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, name);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                }
            }
        } catch (Exception e){
            throw new ResponseException(500, "Error with database");
        }


        return null;
    }

    @Override
    public void setWhiteName(String name, int gameID) throws ResponseException {
        var statement = new String[] {String.format("UPDATE games SET whiteUsername = '%s' WHERE gameID = '%s'",name, gameID)};
        //we also need to update our json
        try{
            executeUpdate(statement);
        }catch(DataAccessException ex){
            throw new ResponseException(500, "Error with database");
        }
    }

    @Override
    public void setBlackName(String name, int gameID) throws ResponseException {
        var statement = new String[] {String.format("UPDATE games SET blackUsername = '%s' WHERE gameID = '%s'",name, gameID)};
        try{
            executeUpdate(statement);
        }catch(DataAccessException ex){
            throw new ResponseException(500, "Error with database");
        }
    }

    @Override
    public ArrayList<GameData> getGames() throws ResponseException {
        games.clear();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, json FROM games";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                //ps.setString(1, authToken);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        games.add(readGames(rs));
                    }
                }
            }
        } catch (Exception e){
            throw new ResponseException(500, "Error with database");
        }
        return games;
    }
    private GameData readGames(ResultSet rs) throws SQLException {
        var gameID = rs.getInt("gameID");
        var white = rs.getString("whiteUsername");
        var black = rs.getString("blackUsername");
        var json = rs.getString("json");
        var out = new Gson().fromJson(json, GameData.class);
        if(Objects.equals(white, "null")){
            white = null;
        }
        if(Objects.equals(black, "null")){
            black = null;
        }
        GameData newGame = new GameData(gameID,white, black, out.getGameName(), out.getGame());
        return newGame;
    }

    @Override
    public GameData getGame(int gameID) throws ResponseException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = String.format("SELECT gameID ,whiteUsername, blackUsername, json FROM games WHERE gameID ='%s'", gameID);
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                //ps.setString(1, authToken);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                }
            }
        } catch (Exception e){
            throw new ResponseException(500, "Error with database");
        }


        return null;
    }

//    private void updateGame(GameData game, GameData oldGame){
//        //func that will update the json for my game whereiver it is cchanged;
//        var statement = "UPDATE games SET json = ? WHERE json =?";
//        //getting new json for a new game
//        String json = new Gson().toJson(game);
//        String oldJson = new Gson().toJson(oldGame);
//        try (var conn = DatabaseManager.getConnection()){
//
//                try (var ps = conn.prepareStatement(statement)) {
//                    ps.setString(1,json);
//                    ps.setString(2,oldJson);
//                    ps.executeUpdate();
//                }
//
//
//        } catch (SQLException | DataAccessException e) {
//            throw new RuntimeException(e);
//        }
//    }
    private GameData readGame(ResultSet rs) throws SQLException {
        var gameID = rs.getInt("gameID");
        var white = rs.getString("whiteUsername");
        var black = rs.getString("blackUsername");
        var json = rs.getString("json");
        var out = new Gson().fromJson(json, GameData.class);
        if(Objects.equals(white, "null")){
            white = null;
        }
        if(Objects.equals(black, "null")){
            black = null;
        }
        GameData newGame = new GameData(gameID,white, black, out.getGameName(), out.getGame());
        return newGame;
    }

    @Override
    public int getID(String gameName) throws ResponseException {
        //FIX THIS
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID FROM games WHERE gameName = ?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, gameName);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readID(rs);
                    }
                }
            }
        } catch (Exception e){
            throw new ResponseException(500, "Error with database");
        }
        return 2;
    }
    public int readID(ResultSet rs) throws SQLException {
        var gameID = rs.getInt("gameID");
        return gameID;
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
              PRIMARY KEY (`authToken`),
              INDEX(id)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS  games (
              `gameID` int NOT NULL AUTO_INCREMENT,
              `whiteUsername` varchar(256),
              `blackUsername` varchar(256),
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
