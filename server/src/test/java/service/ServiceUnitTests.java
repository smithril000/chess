package service;

import dataaccess.ResponseException;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServiceUnitTests {
    UserData testUser = new UserData("testUser", "testEmail", "testPass");
    //UserData testUser2 = new UserData("testUser2", "testEmail2", "testPass2");

    @BeforeEach
    void clean(){
        UserService.clear();
    }
    @Test
    void registerSuccess() throws ResponseException {
        var response = UserService.register(testUser);
        Assertions.assertEquals("testUser", response.username());
    }

    @Test
    void registerTwice() throws ResponseException {
        UserService.register(testUser);
        try {
            UserService.register(testUser);
        }catch(ResponseException ex){
            Assertions.assertEquals(403, ex.getCode());
        }

    }

    @Test
    void loginSuccess() throws ResponseException {
        var response = UserService.register(testUser);
        UserService.logout(response.authToken());
        //now login
        response = UserService.login(testUser);

        Assertions.assertEquals("testUser",response.username());
    }

    @Test
    void loginNotAllowed() {
        try{
            UserService.login(testUser);
        } catch (ResponseException ex) {
            Assertions.assertEquals(401, ex.getCode());
        }
    }

    @Test
    void logoutSuccess() throws ResponseException {
        var auth = UserService.register(testUser).authToken();
        UserService.logout(auth);
    }


}
