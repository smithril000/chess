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
            throw new ResponseException(403, "Error, already exists");
        }
        dataAccess.createUser(user);
        var authData = new AuthData(user.username(), generateAuth());
        return authData;
    }

    public AuthData login(UserData user) throws Exception{
        if(dataAccess.getUser(user.username()) == null){
            //havent registered yet
            throw new Exception("No account with that name");
        }
        //since the username does exist make sure the password is the same
        else if(user.password() != dataAccess.getUser(user.username()).password()){
            //wrong password
            throw new Exception("Wrong Password");
        }
        var authData = new AuthData(user.username(), generateAuth());
        return authData;
    }
    //finsih this
    private String generateAuth(){
        return "xyz";
    }
}
