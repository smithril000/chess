package dataaccess;

import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.UserService;

public class DatabaseUnitTests {
    UserData testUser = new UserData("testUser", "testEmail", "testPass");
    //UserData testUser2 = new UserData("testUser2", "testEmail2", "testPass2");
    DataBaseAccess dataAccess = new DataBaseAccess();
    UserService userService = new UserService(dataAccess);
    @BeforeEach
    void clean() throws ResponseException {
        userService.clear();
    }
    @Test
    void registerSuccess() throws ResponseException {
        var response = userService.register(testUser);
        Assertions.assertEquals("testUser", response.username());
    }

    @Test
    void registerTwice() throws ResponseException {
        userService.register(testUser);
        try {
            userService.register(testUser);
        }catch(ResponseException ex){
            Assertions.assertEquals(403, ex.getCode());
        }

    }

    @Test
    void loginSuccess() throws ResponseException {
        var response = userService.register(testUser);
        userService.logout(response.authToken());
        //now login
        response = userService.login(testUser);

        Assertions.assertEquals("testUser",response.username());
    }

    @Test
    void loginNotAllowed() {
        try{
            userService.login(testUser);
        } catch (ResponseException ex) {
            Assertions.assertEquals(401, ex.getCode());
        }
    }

    @Test
    void logoutSuccess() throws ResponseException {
        var auth = userService.register(testUser).authToken();
        userService.logout(auth);
    }
}
