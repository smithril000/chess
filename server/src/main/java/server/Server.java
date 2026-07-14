package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.*;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import model.UserData;
import service.UserService;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.post("user", ctx -> register(ctx));
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private static void register(Context ctx) {
        //create a UserData from the ctx
        var serialize = new Gson();
        String stuff = ctx.body();
        UserData user = serialize.fromJson(stuff, UserData.class);
        try {
            UserService.register(user);
        } catch (DataAccessException ex) {
            ctx.status(HttpStatus.valueOf("400"));
        }
    }
}
