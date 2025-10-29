package dataaccess;

import datamodel.AuthData;
import datamodel.BareGameData;
import datamodel.GameData;
import datamodel.UserData;
import server.ResponseException;

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
        var statment = "drop database chess";
        try {
            executeUpdate(statment);
        }catch(DataAccessException ex){

        }
    }

    @Override
    public void createUser(UserData user) {

    }

    @Override
    public UserData getUser(String username) {
        return null;
    }

    @Override
    public void createLoginUser(AuthData authData) {

    }

    @Override
    public AuthData getLoggedInData(String username) {
        return null;
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

    }

    @Override
    public void setWhiteName(String name, int gameID) {

    }

    @Override
    public void setBlackName(String name, int gameID) {

    }

    @Override
    public ArrayList<BareGameData> getGames() {
        return null;
    }

    @Override
    public GameData getGame(int gameID) {
        return null;
    }

    @Override
    public int getID() {
        return 0;
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
              `authData` varchar(256) NOT NULL,
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
        clear();
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
    private void executeUpdate(String statement)throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()){
            try(var preparedStatement = conn.prepareStatement(statement)){
                preparedStatement.executeUpdate();
            }
        }catch(SQLException ex){
            throw new DataAccessException("Bad Database");
        }
    }
}
