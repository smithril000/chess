package service;

import datamodel.AuthData;
import datamodel.UserData;

public class UserService {
    public AuthData register(UserData userData){
        return new AuthData(userData.username(), generateAuth());
    }
    //finsih this
    private String generateAuth(){
        return "xyz";
    }
}
