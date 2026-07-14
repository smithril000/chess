package dataaccess;

import model.UserData;

import java.util.HashMap;

//THIS IS TEMP FOR BEFORE DB
public class MemoryDataAccess {
    private static HashMap<String, UserData> userDataList = new HashMap<>();

    public static void createUserDate(UserData user) {
        userDataList.put(user.username(), user);
    }

}
