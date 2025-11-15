package Client_Stuff;

import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;

public class ClientPostLogin {
    private final ServerFacade server;
    private final String authToken;
    public ClientPostLogin(ServerFacade passedServer, String auth){
        server = passedServer;
        authToken = auth;
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
                case "create" -> create(params);
                case "logout" -> logout(authToken);
                case "list" -> list(authToken);
                case "join" -> join(params);
                default -> help();
            };
        } catch (Throwable ex) {
            return ex.getMessage();
        }
    }
    private String help(){
        String help = "help - Some helpful commands";
        String create = "create <NAME> - creates a new Chess Game";
        String list = "list - lists all games";
        String join = "join <ID> - joins a game by its id";
        String observe = "observe <ID> - joins a game as an observer by its id";
        String logout = "logout - logs a user out";
        return String.format("Help Menu\n%s\n%s\n%s\n%s\n%s\n%s\n",help,create,list,join,observe,logout);
    }
    private String join(String[] params){
        try{
            Map<String, String> paramsAsMap = Map.of("playerColor", params[1], "gameID", params[0]);
            server.join(paramsAsMap, authToken);
        }catch(Exception ex){
            return ex.getMessage();
        }
        return "test";
    }

    private String list(String authToken){
        try{
            Map<String, String> games = server.getGames(authToken);
            //now we actually list the games
        }catch(Exception ex){
            return ex.getMessage();
        }
        return "test";
    }
    private String create(String[] params){
        try{
            var paramsAsMap = Map.of("gameName", params[0]);
            server.create(paramsAsMap, authToken);
        }catch(Exception ex){
            return ex.getMessage();
        }
        return "test";
    }
    private String logout(String authToken){
        try{
            server.logout(authToken);

        }catch(Exception ex){
            return ex.getMessage();
        }
        return "goodbye";
    }

}
