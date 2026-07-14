package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class UserService {
    public UserService(){

    }

    public static void register(UserData user) throws DataAccessException {
        //we need to creat both the userdata and the authdata
        MemoryDataAccess.createUserDate(user);
        //create the auth data
        String auth = generateToken();
        //we can create the authData here
        AuthData authData = new AuthData(auth, user.username());
        MemoryDataAccess.createAuthData(authData);
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
