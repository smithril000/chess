package service;

import dataaccess.DataAccess;
import datamodel.AuthData;
import datamodel.UserData;
import server.ResponseException;

public class UserService {
    private final DataAccess dataAccess;
    public UserService(DataAccess dataAccess){
        this.dataAccess = dataAccess;
    }

    public AuthData register(UserData user) throws ResponseException {
        if(dataAccess.getUser(user.username())!= null){
            throw new ResponseException(403, "Error: already exists");
        }
        dataAccess.createUser(user);
        var authData = new AuthData(user.username(), generateAuth());
        return authData;
    }

    public AuthData login(UserData user) throws ResponseException{
        if(user.username() == null || user.password() == null){
            //havent registered yet
            throw new ResponseException(400,"Error: bad request");
        }
        //since the username does exist make sure the password is the same
        else if(dataAccess.getUser(user.username()) == null || user.password() != dataAccess.getUser(user.username()).password()){
            //wrong password
            throw new ResponseException(401, "Error: Unauthorized");
        }
        var authData = new AuthData(user.username(), generateAuth());
        return authData;
    }
    //finsih this
    private String generateAuth(){
        return "xyz";
    }
}
