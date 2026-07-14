package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class UserService {
    public UserService(){

    }

    public static AuthData register(UserData user) throws DataAccessException {
        //we need to creat both the userdata and the authdata
        MemoryDataAccess.createUserDate(user);
        //create the auth data
        String auth = generateToken();
        //we can create the authData here
        AuthData authData = new AuthData(user.username(), auth);
        MemoryDataAccess.createAuthData(authData);
        return authData;
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public static void clear() {
        MemoryDataAccess.clear();
    }
}
