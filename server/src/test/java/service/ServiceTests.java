package service;

import dataaccess.DataAccess;
import dataaccess.DatabaseAccess;
import datamodel.AuthData;
import datamodel.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.ResponseException;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceTests {
    static final DataAccess dataAccess = new DatabaseAccess();

    @BeforeEach
    void clear() throws ResponseException{
        try {
            dataAccess.clear();
        }catch(ResponseException ex){
            throw new ResponseException(500, "Error getting clear");

        }
    }
    @Test
    void createUser() throws ResponseException{
        var user = new UserData("testUser", "testEmail", "TestPass");
        dataAccess.createUser(user);
        var userBack = dataAccess.getUser("testUser");
        assertEquals(user.username(),userBack.username());
    }

    @Test
    void createUserWithHashedPass() throws ResponseException{
        var user = new UserData("testUser", "testEmail", "TestPass");
        dataAccess.createUser(user);
        var userBack = dataAccess.getUser("testUser");
        assertNotEquals(user.password(), userBack.password());
    }

    @Test
    void checkVerifyHash()throws ResponseException{
        var user = new UserData("testUser", "testEmail", "TestPass");
        dataAccess.createUser(user);

        assertTrue(dataAccess.verifyUser(user.username(), user.password()));
    }

    @Test
    void checkBadHash() throws ResponseException{
        var user = new UserData("testUser", "testEmail", "TestPass");
        dataAccess.createUser(user);
        assertFalse(dataAccess.verifyUser(user.username(), "NotMyPass"));
    }

    @Test
    void tryLogin() throws ResponseException{
        var userData = new AuthData("testUsername", "xyz");
        dataAccess.createLoginUser(userData);
        assertEquals(dataAccess.getLoggedInData(userData.authToken()).username(), userData.username());
    }

}

