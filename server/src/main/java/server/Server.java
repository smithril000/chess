package server;

import com.google.gson.Gson;
import dataaccess.DatabaseAccess;
import dataaccess.MemoryDataAccess;
import datamodel.BareGameData;
import datamodel.GameData;
import datamodel.JoinGameData;
import datamodel.UserData;
import io.javalin.*;
import io.javalin.http.Context;
import io.javalin.http.ExceptionHandler;
import service.UserService;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

public class Server {

    private final Javalin javalin;
    private final UserService userService;

    public Server() {

        var dataAccess = new DatabaseAccess();
        userService = new UserService(dataAccess);
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.delete("db", ctx->clear(ctx));
        javalin.post("user", ctx -> register(ctx));
        javalin.post("session", ctx -> login(ctx));
        javalin.delete("session", ctx -> logout(ctx));
        javalin.get("game", ctx -> listGames(ctx));
        javalin.post("game", ctx -> createGame(ctx));
        javalin.put("game", ctx -> joinGame(ctx));
    }
    private void clear(Context ctx){
        try {
            userService.clear();
        }catch(ResponseException ex){
            ctx.status(ex.getCode()).result(ex.toJson());
        }
    }

    private void joinGame(Context ctx){
        var serializer = new Gson();
        String reqJson = ctx.header("authorization");
        try{
            String authToken;
            if(reqJson !=null){
                authToken = userService.checkAuth(reqJson);
            }else{
                authToken = "0";
            }

            reqJson = ctx.body();
            JoinGameData data = serializer.fromJson(reqJson, JoinGameData.class);


            userService.joinGame(data.gameID(), authToken, data.playerColor());
            ctx.status(200).result("{}");
        }catch(ResponseException ex){
            ctx.status(ex.getCode()).result(ex.toJson());
        }

    }

    private void listGames(Context ctx){
        var serializer = new Gson();
        String reqJson = ctx.header("authorization");
        String authToken = serializer.fromJson(reqJson, String.class);
        //we mostly just need to verify the auth token one furth up
        try {
            ArrayList<GameData> out = userService.listGames(authToken);
            String outJson = String.format("{ \"games\": %s }", serializer.toJson(out));
            ctx.status(200).result(outJson);
        }catch(ResponseException ex){
            ctx.status(ex.getCode()).result(ex.toJson());
        }

    }
    private void createGame(Context ctx){
        var serializer = new Gson();
        String headerJson = ctx.header("authorization");
        String reqJson = ctx.body();
        String authToken = serializer.fromJson(headerJson, String.class);
        Map<String, String> gameNameMap = serializer.fromJson(reqJson, Map.class);
        String gameName = gameNameMap.get("gameName");

        try {

            int gameID = userService.createGame(gameName, authToken);
            String outJson = serializer.toJson(Map.of("gameID", gameID));
            ctx.status(200).result(outJson);
        }catch(ResponseException ex){
            ctx.status(ex.getCode()).result(ex.toJson());
        }
    }

    private void logout(Context ctx){
        var serializer = new Gson();
        String reqJson = ctx.header("authorization");
        var base = serializer.fromJson(reqJson, String.class);
        var auth = "0";
        try{
            //
            userService.logout(base);
            ctx.status(200).result("{}");

        }catch(ResponseException ex){
            ctx.status(ex.getCode()).result(ex.toJson());
        }
    }

    private void login(Context ctx){
        var serializer = new Gson();
        String reqJson = ctx.body();
        //var req = serializer.fromJson(reqJson, Map.class);

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
