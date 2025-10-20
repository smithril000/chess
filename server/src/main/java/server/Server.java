package server;

import com.google.gson.Gson;
import dataaccess.MemoryDataAccess;
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
        var dataAccess = new MemoryDataAccess();
        userService = new UserService(dataAccess);
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
                var user = serializer.fromJson(reqJson, UserData.class);
                var authData = userService.login(user);
                ctx.result(serializer.toJson(authData));
            }catch(ResponseException ex){
                ctx.status(ex.getCode());
                ctx.result(ex.toJson());
            }


    }
    //this can be the handler
    private void register(Context ctx){
        var serializer = new Gson();
        String reqJson = ctx.body();
        var user = serializer.fromJson(reqJson, UserData.class);
        try {

            //call into service and register
            var authData = userService.register(user);
            ctx.result(serializer.toJson(authData));
        }catch(ResponseException ex){

            ctx.status(ex.getCode());
            ctx.result(ex.toJson());
        }
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
