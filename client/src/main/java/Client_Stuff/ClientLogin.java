package Client_Stuff;

import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;

import static java.awt.Color.BLUE;

public class ClientLogin {
    private final ServerFacade server;
    private final String regString = "register <USERNAME> <PASSWORD> <EMAIL> - creates a new user";
    private final String loginString = "login <USERNAME> <PASSWORD> - logges into an existing user";
    Boolean loggedIn = false;
    String username = null;
    String authData = null;
    public ClientLogin(String serverURL){
        server = new ServerFacade(serverURL);
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
                    ClientPostLogin log = new ClientPostLogin(server, authData);
                    log.run();
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
                case "register" -> register(params);
                case "quit" -> quit();
                default -> help();
            };
        } catch (Throwable ex) {
            return ex.getMessage();
        }
    }
    public String quit(){
        return "quit";
    }
    public String help(){
        return String.format("%s\n%s\nhelp - shows helpful instructions :)\nquit - quits operation", regString, loginString);
    }
    public String login(String[] params){
        if(params.length != 2){
            //wrong amount of args
            System.out.println("Wrong amount of arguments -" + loginString);
        }else{
            Map<String, String> paramsAsMap = Map.of("username", params[0], "password", params[1]);
            try{
                var userInfo = server.login(paramsAsMap);
                username = userInfo.get("username");
                authData = userInfo.get("authToken");
                loggedIn = true;
                //we need to clear our save username
                return "Logged in as "+ username;
            }catch(Exception ex){
                return ex.getMessage();
            }
        }
        return "";
    }
    public String register(String[] params){
        if (params.length != 3){
            System.out.println("Wrong amount of arguments -" + regString);
        }else{
            //call the actual register
            Map<String, String> paramsAsMap =  Map.of("username", params[0], "password", params[1], "email", params[2]);
            try {
                var response = server.register(paramsAsMap);
                username = response.get("username");
                authData = response.get("authToken");
                loggedIn = true;
                return "Successfully registered";
            }catch(Exception ex){
                return ex.getMessage();
            }
        }
        return "";
    }
}
