package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.ResponseException;
import io.javalin.*;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import model.AuthData;
import model.GameName;
import model.JoinGameRequest;
import model.UserData;
import org.jetbrains.annotations.NotNull;
import service.UserService;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.post("user", ctx -> register(ctx));
        javalin.delete("/db", ctx -> clear(ctx));
        javalin.post("/session", ctx -> login(ctx));
        javalin.delete("/session", ctx -> logout(ctx));
        javalin.post("/game", ctx -> createGame(ctx));
        javalin.put("/game", ctx -> joinGame(ctx));
        javalin.get("/game", ctx -> getGames(ctx));
    }

    private void getGames(@NotNull Context ctx) {
        var serialize = new Gson();
        var auth = ctx.header("authorization");
        try{
            UserService.checkAuth(auth);
            //confirmed auth
            //now just get games
            var games = UserService.getGames();
            System.out.println(games);
            ctx.result(serialize.toJson(games));
        }catch(ResponseException ex){
            ctx.status(ex.getCode());
            ctx.result(ex.toJson());
        }
    }

    private void joinGame(@NotNull Context ctx) {
        var serialize = new Gson();
        String stuff = ctx.body();
        JoinGameRequest join = serialize.fromJson(stuff, JoinGameRequest.class);
        String auth = ctx.header("authorization");
        try{
            UserService.checkAuth(auth);
            try{
                UserService.joinGame(join, auth);
            }catch(ResponseException ex){
                ctx.status(ex.getCode());
                ctx.result(ex.toJson());
            }
        } catch (ResponseException ex) {
            ctx.status(ex.getCode());
            ctx.result(ex.toJson());
        }

    }

    private void createGame(@NotNull Context ctx) {
        var serialize = new Gson();
        String stuff = ctx.body();
        //we need to check auth
        GameName gameName = serialize.fromJson(stuff, GameName.class);
        String auth = ctx.header("authorization");
        try{
            UserService.checkAuth(auth);
            //now we have checked the auth
            //so now we send off the game to be created
            try{
                var gameIdObject = UserService.createGame(gameName);
                ctx.result(serialize.toJson(gameIdObject));
            }catch(ResponseException ex){
                ctx.status(ex.getCode());
                ctx.result(ex.toJson());
            }
        }catch(ResponseException ex){
            ctx.status(ex.getCode());
            ctx.result(ex.toJson());
        }

    }

    private void logout(@NotNull Context ctx) {
        var serialize = new Gson();
        String stuff = ctx.body();
        //we have to grab the auth from the header
        var auth = ctx.header("authorization");
        try {
            UserService.checkAuth(auth);
            try {
                UserService.logout(auth);
            } catch (ResponseException ex) {
                ctx.status(ex.getCode());
                ctx.result(ex.toJson());

            }
        }catch(ResponseException ex) {
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
    private void clear(Context ctx){
        UserService.clear();
    }

    public static void login(Context ctx){
        //this time we are getting username and pass
        var serialize = new Gson();
        String stuff = ctx.body();
        var userData = serialize.fromJson(stuff, UserData.class);
        try {
            var authData = UserService.login(userData);
            ctx.result(serialize.toJson(authData));
        } catch (ResponseException ex) {
            ctx.status(ex.getCode());
            ctx.result(ex.toJson());
        }
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
