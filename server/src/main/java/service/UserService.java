package service;

import dataaccess.MemoryDataAccess;
import dataaccess.ResponseException;
import model.AuthData;
import model.UserData;

import java.util.Objects;
import java.util.UUID;

public class UserService {
    public UserService(){

    }

    public static AuthData register(UserData user) throws ResponseException {
        //first do some checks
        if(user.username() == null || user.password() == null){
            throw new ResponseException(400, "Error: bad request");
        }
        else if(MemoryDataAccess.getUser(user.username())!= null){
            throw new ResponseException(403, "Error: already exists");
        }
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

    public static AuthData login(UserData userData) throws ResponseException{
        //do checks
        if(userData.username() == null || userData.password() == null){ // no user or pass entered
            throw new ResponseException(400, "Error, bad request");
        }else if(MemoryDataAccess.getUser(userData.username())== null){
            throw new ResponseException(400, "Error, bad request");
        }else if(!Objects.equals(MemoryDataAccess.getUser(userData.username()).password(), userData.password())){
            throw new ResponseException(401, "Error, unauthorized");
        }
        //create new auth
        var auth = generateToken();
        var authData = new AuthData(userData.username(), auth);
        return authData;
    }

    public static void logout(String auth) throws ResponseException {
        //make sure we have auth in the db
        String username =  MemoryDataAccess.getUsernameByAuth(auth);
        if(username == null){
            throw new ResponseException(401, "Error, something went wrong");
        }
        //now that we found the auth we can remove
        MemoryDataAccess.removeFromAuths(username);
    }
}
