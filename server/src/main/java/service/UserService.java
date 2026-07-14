package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.UserData;

public class UserService {
    public UserService(){

    }

    public static void register(UserData user) throws DataAccessException {
        MemoryDataAccess.createUserDate(user);
    }
}
