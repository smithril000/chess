package client;

import chess.ChessGame;
import com.google.gson.Gson;
import model.Game;
import model.GamesReturned;
import model.JoinGameRequest;

import java.util.*;

public class PostLogin {
    private final String authToken;
    private final ServerFacade server;
    private Map<Integer, ChessGame> gamesById = new HashMap<>();

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
                case "create" -> createGame(params);
                case "list" -> list();
                case "join" -> joinGame(params);
                default -> help();
            };
        } catch (Throwable ex) {
            return ex.getMessage();
        }
    }

    private String joinGame(String[] params){
        try {
            JoinGameRequest join = new JoinGameRequest(params[1].toUpperCase(), Integer.parseInt(params[0]));
            server.joinGame(join, authToken);
        }catch(Exception ex){
            System.out.println("Error - fix");
        }
        return "";
    }

    private String list(){
        try{
            GamesReturned gamesModel = server.listGames(authToken);
            List<Game> games = gamesModel.games();
            StringBuilder out = new StringBuilder();
            int i = 1;
            for(Game game : games){
                //we want to add the data to a string stream to out
                //start with the number
                out.append("game ID: ").append(i);
                //now gamename
                out.append(" - GameName: ").append(game.gameName());
                //now usernames
                String white = "No Player";
                String black = "No Player";
                if(game.blackUsername() != null){
                    black = game.blackUsername();
                }
                if(game.whiteUsername()!=null){
                    white = game.whiteUsername();
                }
                out.append("\n\tWhite User - ").append(white);
                out.append("\n\tBlack User - ").append(black);
                out.append("\n");
                //now add to us keeping track of them by id
                gamesById.put(i, game.game());
                i++;
            }
            return out.toString();


        }catch(Exception ex){
            return ex.getMessage();
        }
    }


    private String createGame(String[] params){
        if(params.length!=1){
            System.out.println("Wrong number of params");
            return "help";
        }
        Map<String, String> data = new HashMap<>(Map.of());
        data.put("gameName", params[0]);
        var res = server.createGame(data, this.authToken);
        if(res.gameID()!=0){
            //we log out correctly if here
            return "Game created successfully!";
        }
        return "error - fix";
    }

    private String logout() {
        //first parse what my server expects
        //we need my auth token
        Map<String, String> data = new HashMap<>();
        data.put("authToken", this.authToken);
        server.logout(data, this.authToken);
        return "";
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
