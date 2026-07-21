package dataaccess;

import model.UserData;

public interface DataAccess {
    void createUserDate(UserData user) throws ResponseException;

}
