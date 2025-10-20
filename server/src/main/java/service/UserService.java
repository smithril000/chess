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
        if(user.username() == null || user.password() == null){
            throw new ResponseException(400, "Error: bad request");
        }
        else if(dataAccess.getUser(user.username())!= null){
            throw new ResponseException(403, "Error: already exists");
        }
        dataAccess.createUser(user);

        var authData = new AuthData(user.username(), generateAuth());
        dataAccess.createLoginUser(authData);
        return authData;
    }

    public AuthData login(UserData user) throws ResponseException{
        if(user.username() == null || user.password() == null){
            //havent registered yet
            throw new ResponseException(400,"Error: bad request");
        }
        //since the username does exist make sure the password is the same
        else if(dataAccess.getUser(user.username()) == null){
            //wrong password
            throw new ResponseException(401, "Error: Unauthorized");
        }else if(!user.password().equals(dataAccess.getUser(user.username()).password())){

            throw new ResponseException(401, "Error: Unauthorized");
        }
        var authData = new AuthData(user.username(), generateAuth());
        dataAccess.createLoginUser(authData);
        return authData;
    }

    public void logout(String authToken) throws ResponseException{
        if(dataAccess.getLoggedInData(authToken) == null){
            //TEST
            var test = dataAccess.getLoggedInData(authToken);
            //auth token not in data base
            throw new ResponseException(401, "Error: unauthorized");
        }else{
            //remove the authtoken fomr the temp memory
            dataAccess.removeLoggedInUser(authToken);
        }
    }

    public void clear(){
        dataAccess.clear();
    }
    //finsih this
    private String generateAuth(){
        return "xyz";
    }
}
