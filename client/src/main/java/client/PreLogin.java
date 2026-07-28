package client;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class PreLogin {
    private final ServerFacade server;
    private boolean loggedIn = false;
    private final String regString = "register <USERNAME> <PASSWORD> <EMAIL> - creates a new user";
    private final String loginString = "login <USERNAME> <PASSWORD> - logges into an existing user";

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
                //System.out.println(result);
                if(loggedIn){
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
                case "login" -> null;
                case "quit" -> "quit";
                case "register" -> register(params);
                default -> help(params);
            };
        } catch (Throwable ex) {
            return ex.getMessage();
        }
    }

    private String help(String[] params){
        return String.format("%s\n%s\nhelp - shows helpful instructions :)\nquit - quits operation", regString, loginString);
    }

    private String register(String[] params){
        //create a map to mimick what our serveris expecting
        Map<String, String> data = new HashMap<>(Map.of());
        data.put("username", params[0]);
        data.put("email", params[1]);
        data.put("password", params[2]);
        server.register(data);
        return "help";
    }
}
