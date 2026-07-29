package client;

import model.JoinGameRequest;
import model.ResponseException;
import org.junit.jupiter.api.*;
import server.Server;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


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

    private Map<String, String> createGameReq(String name) {
        Map<String, String> test = new HashMap<>();
        test.put("gameName", name);
        return test;
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
    void register() {
        var authData = facade.register(createRegUser("player", "passwor", "p@email.com"));
        assertTrue(authData.authToken().length() > 10);
    }
    @Test
    void doubleReg() {
        facade.register(createRegUser("player1", "password", "p1@email.com"));
        //register agian with same info
        var authData = facade.register(createRegUser("player1", "password", "p1@email.com"));
        assertNull(authData);
    }

    @Test
    void login() {
        //first red
        facade.register(createRegUser("player1", "password", "p1@email.com"));
        var auth = facade.login(createLoginUser("player1", "password"));
        assertTrue(auth.authToken().length() > 10);
    }

    @Test
    void badLogin() {
        facade.register(createRegUser("player1", "password", "p1@email.com"));
        var auth = facade.login(createLoginUser("player", "BADpassword"));
        assertNull(auth);
    }

    @Test
    void logout() throws ResponseException {
        var auth = facade.register(createRegUser("player1", "password", "p1@email.com"));
        var res = facade.logout(auth.authToken());
        assertNull(res);
    }

    @Test
    void badLogout() throws ResponseException {
        var auth = facade.register(createRegUser("player1", "password", "p1@email.com"));
        facade.clear();
        boolean foundError = false;
        try {
            facade.logout(auth.authToken());
        }catch(ResponseException ex){
            foundError = true;
        }
        assertTrue(foundError);
    }
    @Test
    void createGame() throws ResponseException {
        var auth = facade.register(createRegUser("player1", "password", "p1@email.com"));
        var res = facade.createGame(createGameReq("myGameName"),auth.authToken());
        assertNotNull(res);
    }

    @Test
    void createGameNoAuth() throws ResponseException {
        var auth = facade.register(createRegUser("player1", "password", "p1@email.com"));
        facade.clear();
        boolean foundError = false;
        try {
            facade.createGame(createGameReq("myGameName2"),auth.authToken());
        }catch(ResponseException ex){
            foundError = true;
        }
        assertTrue(foundError);
    }

    @Test
    void listGames() throws ResponseException {
        var auth = facade.register(createRegUser("player1", "password", "p1@email.com"));
        facade.createGame(createGameReq("myGameName"),auth.authToken());
        var res = facade.listGames(auth.authToken());
        assertNotNull(res);
    }

    @Test
    void listGamesBad() throws ResponseException {
        var auth = facade.register(createRegUser("player1", "password", "p1@email.com"));
        facade.createGame(createGameReq("myGameName"),auth.authToken());
        facade.clear();
        boolean foundError = false;
        try {
            facade.listGames(auth.authToken());
        }catch(ResponseException ex){
            foundError = true;
        }
        assertTrue(foundError);
    }

    @Test
    void joinGame() throws ResponseException {
        var auth = facade.register(createRegUser("player1", "password", "p1@email.com"));
        facade.createGame(createGameReq("myGameName"),auth.authToken());
        facade.listGames(auth.authToken());

        boolean foundError = false;
        try {
            facade.joinGame(new JoinGameRequest("WHITE", 1),auth.authToken());
        }catch(ResponseException ex){
            foundError = true;
        }
        assertFalse(foundError);
    }
    @Test
    void joinGameNoAuth() throws ResponseException {
        var auth = facade.register(createRegUser("player1", "password", "p1@email.com"));
        facade.createGame(createGameReq("myGameName"),auth.authToken());
        facade.listGames(auth.authToken());
        facade.clear();
        boolean foundError = false;
        try {
            facade.joinGame(new JoinGameRequest("WHITE", 1),auth.authToken());
        }catch(ResponseException ex){
            foundError = true;
        }
        assertTrue(foundError);
    }
}
