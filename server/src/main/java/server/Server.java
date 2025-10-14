package server;

import com.google.gson.Gson;
import datamodel.UserData;
import io.javalin.*;
import io.javalin.http.Context;
import io.javalin.http.ExceptionHandler;
import service.UserService;

import java.util.Map;

public class Server {

    private final Javalin javalin;
    private final UserService userService;

    public Server() {
        userService = new UserService();
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.delete("db", ctx -> ctx.result("{}"));
        javalin.post("user", ctx -> register(ctx));
        javalin.post("session", ctx -> login(ctx));
    }
    private void login(Context ctx){
        var serializer = new Gson();
        String reqJson = ctx.body();
        var req = serializer.fromJson(reqJson, Map.class);

        //maybe fake?
        //check if we got both username and password
        try {
            if(req.get("username") == null){
                throw new IllegalArgumentException();
            }

            var res = Map.of("username", req.get("username"), "authToken", "newXYZ");
            ctx.result(serializer.toJson(res));
        }catch(IllegalArgumentException e){
            //ctx.result(HttpStatus.BAD_REQUEST);
        }

    }
    //this can be the handler
    private void register(Context ctx){
        var serializer = new Gson();
        String reqJson = ctx.body();
        var user = serializer.fromJson(reqJson, UserData.class);
        //call into service and register
        var authData = userService.register(user);
        ctx.result(serializer.toJson(authData));
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
