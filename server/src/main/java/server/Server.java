package server;

import com.google.gson.Gson;
import dataaccess.ResponseException;
import io.javalin.*;
import io.javalin.http.Context;
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
        javalin.post("user", Server::register);
        javalin.delete("/db", this::clear);
        javalin.post("/session", Server::login);
        javalin.delete("/session", this::logout);
        javalin.post("/game", this::createGame);
        javalin.put("/game", this::joinGame);
        javalin.get("/game", this::getGames);
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
        System.out.println("Starting");
        var serialize = new Gson();
        String stuff = ctx.body();
        JoinGameRequest join = serialize.fromJson(stuff, JoinGameRequest.class);
        String auth = ctx.header("authorization");
        try{
            UserService.checkAuth(auth);
            System.out.println("CheckedAuth fine");
            try{
                UserService.joinGame(join, auth);
                System.out.println("Got to join correct");
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
    private void clear(Context ctx) throws ResponseException{
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
