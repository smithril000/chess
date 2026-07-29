package client;

import org.junit.jupiter.api.*;
import server.Server;

import java.util.HashMap;
import java.util.Map;

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

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    void register() throws Exception {
        var authData = facade.register(createRegUser("player1", "password", "p1@email.com"));
        assertTrue(authData.authToken().length() > 10);
    }

}
