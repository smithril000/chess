package client;

import com.google.gson.Gson;

import java.util.*;

public class PostLogin {
    private final String authToken;
    private final ServerFacade server;
    private Map<Integer, Integer> gamesById = new HashMap<>();

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
                default -> help();
            };
        } catch (Throwable ex) {
            return ex.getMessage();
        }
    }

    private String list(){
        try{
            Map<String, Map> games = server.listGames(authToken);
            return listGamesHelper(games);


        }catch(Exception ex){
            return ex.getMessage();
        }
    }

    private String listGamesHelper(Map<String, Map> games){
        String out = "";
        //now we actually list the games
        //we need to run it into a string
        ArrayList<Map> gameList = (ArrayList) games.get("games");
        int i = 1; //my counter for the id's
        for(var game : gameList){
            String whiteName;
            String blackName;
            String gameName = game.get("gameName").toString();
            if(game.get("whiteUsername") == null){
                whiteName = "No Player";
            }else{
                whiteName = game.get("whiteUsername").toString();
            }

            if(game.get("blackUsername") == null){
                blackName = "No Player";
            }else{
                blackName = game.get("blackUsername").toString();
            }
            //now i need to keep track of the ids, by the ones i created
            int gameID = (int) (Double.parseDouble(game.get("gameID").toString()));
//            ChessGame chessGame = new Gson().fromJson(game.get("game").toString(), ChessGame.class);
            gamesById.put(i,gameID);
//            chessGames.put(i, chessGame);
            out = out + String.format("%d -- %s\n \twhite player: %s\n \tblack player: %s\n\n",gameID, gameName, whiteName, blackName);

            i++;
        }
        return out;
    }

    private String createGame(String[] params){
        if(params.length!=1){
            System.out.println("Wrong number of params");
            return "help";
        }
        Map<String, String> data = new HashMap<>(Map.of());
        data.put("gameName", params[0]);
        var res = server.createGame(data, this.authToken);
        if(res.get("message")==null){
            //we log out correctly if here
            return "Game created successfully!";
        }
        return res.get("message");
    }

    private String logout() {
        //first parse what my server expects
        //we need my auth token
        Map<String, String> data = new HashMap<>();
        data.put("authToken", this.authToken);
        var res = server.logout(data, this.authToken);
        if(res.get("message")==null){
            //we log out correctly if here
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
