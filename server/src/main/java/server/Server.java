package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.ResponseException;
import io.javalin.*;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import model.AuthData;
import model.UserData;
import service.UserService;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.post("user", ctx -> register(ctx));
        javalin.delete("/db", ctx -> clear(ctx));
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
    private void clear(Context ctx){
        UserService.clear();
    }

    private static void register(Context ctx) {
        //create a UserData from the ctx
        var serialize = new Gson();
        String stuff = ctx.body();
        UserData user = serialize.fromJson(stuff, UserData.class);
        try {
            AuthData auth = UserService.register(user);
            //return
            ctx.result(serialize.toJson(auth));
        } catch (ResponseException ex) {
            ctx.status(ex.getCode());
            ctx.result(ex.toJson());
        }
    }
}
