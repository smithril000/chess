package dataaccess;

import model.AuthData;
import model.Game;
import model.GameID;
import model.UserData;

import java.util.HashMap;
import java.util.Objects;

//THIS IS TEMP FOR BEFORE DB
public class MemoryDataAccess {
    private static HashMap<String, UserData> userDataList = new HashMap<>();
    private static HashMap<String, AuthData> authDataList = new HashMap<>();
    private static int gameId = 0;
    private static HashMap<Integer, Game> gameList = new HashMap<>();

    public static void createUserDate(UserData user) {
        userDataList.put(user.username(), user);
    }


    public static void createAuthData(AuthData authData) {
        authDataList.put(authData.authToken(), authData);
    }

    public static void clear() {
        userDataList = new HashMap<>();
        authDataList = new HashMap<>();
    }

    public static UserData getUser(String username) {
        return userDataList.get(username);
    }

    public static String getUsernameByAuth(String auth){
        for(HashMap.Entry<String, AuthData> entry : authDataList.entrySet()){
            String username = entry.getKey();
            AuthData authData = entry.getValue();
            if(Objects.equals(authData.authToken(), auth)){
                return authData.username();
            }
        }
        return null;
    }

    public static void removeFromAuths(String username) {
        authDataList.remove(username);
    }

    public static GameID createGame(String name) {
        //for now we wil just make this a record
        gameId++;
        Game game = new Game(gameId, null, null, name);
        gameList.put(gameId, game);
        return new GameID(gameId);
    }

    public static void joinGame(String color, int id, String username) {
        Game newGame = null;
        Game game = gameList.get(id);
        if (Objects.equals(color, "WHITE")) {
            newGame = game.changeWhite(username);
        }else if(Objects.equals(color, "BLACK")){
            newGame = game.changeBlack(username);
        }

        gameList.remove(id);
        gameList.put(id, newGame);
        System.out.println(authDataList);
        System.out.println(newGame);
        System.out.println(username);
    }

    public static HashMap<Integer, Game> getGames() {
        return gameList;
    }
}
