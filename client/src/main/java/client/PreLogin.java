package client;

import model.ResponseException;

import java.net.http.WebSocket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class PreLogin {
    private final ServerFacade server;
    private boolean loggedIn = false;
    private final String regString = "register <USERNAME> <PASSWORD> <EMAIL> - creates a new user";
    private final String loginString = "login <USERNAME> <PASSWORD> - logs into an existing user";
    private String authToken;
    String username = "";


    public PreLogin(int port){
        server = new ServerFacade(port);
    }


    public void run(){
        System.out.println("Welcome to chess. Type Help to get started");
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while(!result.equals("quit")){
            System.out.print("[Logged Out] >> ");
            String line = scanner.nextLine();
            try{
                result = eval(line);
                System.out.println(result);
                if(loggedIn){
                    //now that we are logged in we can pause here and run our other ui
                    PostLogin post = new PostLogin(authToken, server, username);
                    post.run();
                    loggedIn = false;
                }
            }catch (Throwable ex){
                var errorMessage = ex.toString();
                System.out.println(errorMessage);
            }

        }
    }

    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "login" -> login(params);
                case "quit" -> "quit";
                case "register" -> register(params);
                default -> help();
            };
        } catch (Throwable ex) {
            return ex.getMessage();
        }
    }

    private String help(){
        return String.format("%s\n%s\nhelp - shows helpful instructions :)\nquit - quits operation", regString, loginString);
    }

    private String register(String[] params){
        //fist check if number of params is what we expect
        if(params.length != 3){
            System.out.println("Wrong number of arguments");
            return "Expected some thing like - " + regString;
        }
        //create a map to mimic what our servers expecting
        Map<String, String> data = new HashMap<>(Map.of());
        data.put("username", params[0]);
        data.put("email", params[2]);
        data.put("password", params[1]);
        try {
            var res = server.register(data);
            authToken = res.authToken();
            this.username = params[0];
            loggedIn = true;
            return "";
        }catch(ResponseException ex){
            System.out.println(ex.getMessage());
            return "";
        }
    }
    private String login(String[] params){
        if(params.length !=2){
            System.out.println("Wrong number of arguments");
            return "Expected something like " + loginString;
        }
        Map<String, String> data = new HashMap<>(Map.of());
        data.put("username", params[0]);
        data.put("password", params[1]);
        try {
            var res = server.login(data);
            authToken = res.authToken();
            loggedIn = true;
            this.username = params[0];
            return "";
        }catch(ResponseException ex){
            System.out.println(ex.getMessage());
            return"";
        }

    }
}
