package client;

import dataaccess.ResponseException;
import org.junit.jupiter.api.*;
import server.Server;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;


    Map<String, String> createRegUser(String username, String password, String email){
        Map<String, String> testUserReg = new HashMap<>();
        testUserReg.put("username", username);
        testUserReg.put("password", password);
        testUserReg.put("email", email);
        return testUserReg;
    }
    private Map<String, String> createLoginUser(String username, String password) {
        Map<String, String> testUserReg = new HashMap<>();
        testUserReg.put("username", username);
        testUserReg.put("password", password);
        return testUserReg;
    }

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @AfterAll
    static void stopServer() throws ResponseException {
        //clear my db
        facade.clear();
        server.stop();
    }


    @Test
    void register() throws Exception {
        var authData = facade.register(createRegUser("player1", "password", "p1@email.com"));
        assertTrue(authData.authToken().length() > 10);
    }
    @Test
    void doubleReg() throws Exception{
        facade.register(createRegUser("player1", "password", "p1@email.com"));
        //register agian with same info
        var authData = facade.register(createRegUser("player1", "password", "p1@email.com"));
        assertNull(authData);
    }

    @Test
    void login() throws Exception{
        //first red
        facade.register(createRegUser("player1", "password", "p1@email.com"));
        var auth = facade.login(createLoginUser("player1", "password"));
        assertTrue(auth.authToken().length() > 10);
    }

    @Test
    void badLogin() throws Exception{
        facade.register(createRegUser("player1", "password", "p1@email.com"));
        var auth = facade.login(createLoginUser("player1", "BADpassword"));
        assertNull(auth);
    }


}
