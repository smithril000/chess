package dataaccess;

import model.ResponseException;
import model.UserData;

public interface DataAccess {
    void createUserDate(UserData user) throws ResponseException;

}
