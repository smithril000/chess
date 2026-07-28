package client;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class PostLogin {
    private final String authToken;
    private final ServerFacade server;
    public PostLogin(String authToken, ServerFacade server){
        this.authToken = authToken;
        this.server = server;
    }
    public void run(){
        System.out.println("Welcome");
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while(!result.equals("goodbye")){
            System.out.print("[Logged in] >> ");
            String line = scanner.nextLine();
            try{
                result = eval(line);
                System.out.println(result);
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
                case "logout" -> logout();
                default -> help();
            };
        } catch (Throwable ex) {
            return ex.getMessage();
        }
    }

    private String logout() {
        //first parse what my server expects
        //we need my auth token
        Map<String, String> data = new HashMap<>();
        data.put("authToken", this.authToken);
        var res = server.logout(data, this.authToken);
        if(res.get("message")==null){
            //we logout out correctly if here
            return "goodbye";
        }
        return res.get("message");
    }

    private String help(){
        String help = "help - Some helpful commands";
        String create = "create <NAME> - creates a new Chess Game";
        String list = "list - lists all games";
        String join = "join <PlayerColor> <ID> - joins a game by its id";
        String observe = "observe <ID> - joins a game as an observer by its id";
        String logout = "logout - logs a user out";
        return String.format("Help Menu\n----------------\n%s\n%s\n%s\n%s\n%s\n%s\n",help,create,list,join,observe,logout);
    }
}
