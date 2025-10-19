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

        if(req.get("username") == null || req.get("password") == null){
            ctx.status(400);
            ctx.result(serializer.toJson(new ResponseException(400, "Error: bad request")));
        }else {

            var res = Map.of("username", req.get("username"), "authToken", "newXYZ");
            ctx.result(serializer.toJson(res));
        }

    }
    //this can be the handler
    private void register(Context ctx){
        try {
            var serializer = new Gson();
            String reqJson = ctx.body();
            var user = serializer.fromJson(reqJson, UserData.class);
            //call into service and register
            var authData = userService.register(user);
            ctx.result(serializer.toJson(authData));
        }catch(Exception ex){
            //change the error message to a json response
            var err = new ResponseException(403, "Error: already taken");
            //ctx.status(403).result(err.getMessage());
            var serializer = new Gson();
            ctx.status(403);
            ctx.result(serializer.toJson(err));
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
