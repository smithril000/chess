package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.HashMap;

//THIS IS TEMP FOR BEFORE DB
public class MemoryDataAccess {
    private static final HashMap<String, UserData> userDataList = new HashMap<>();
    private static final HashMap<String, AuthData> authDataList = new HashMap<>();

    public static void createUserDate(UserData user) {
        userDataList.put(user.username(), user);
    }


    public static void createAuthData(AuthData authData) {
        authDataList.put(authData.authToken(), authData);
    }
}
