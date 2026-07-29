package client;

import chess.ChessGame;
import model.ResponseException;
import model.Game;
import model.GamesReturned;
import model.JoinGameRequest;
import ui.DrawBoard;

import java.util.*;

public class PostLogin {
    private final String authToken;
    private final ServerFacade server;
    private final Map<Integer, ChessGame> gamesById = new HashMap<>();
    private final Map<Integer, Integer> idLog = new HashMap<>();

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
            //check if we still have an auth
            if(authToken==null){
                System.out.println("Sorry, there has been an issue with your account");
                result = "goodbye";
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
                case "observe" -> observerGame(params);
                default -> help();
            };
        } catch (Throwable ex) {
            return ex.getMessage();
        }
    }

    private String observerGame(String[] params) {
        //check the params
        if(params.length != 1){
            System.out.print("Wrong amount of arguments, expected something like - ");
            System.out.println("observe <ID>");
        }
        //check mage sure we got an int
        try{
            Integer.parseInt(params[0]);
        }catch(NumberFormatException ex){
            System.out.println("Hmm, couldn't recognize that game id");
            return "";
        }
        //check if that id exists
        if(!gamesById.containsKey(Integer.parseInt(params[0]))){
            System.out.println("Sorry, can't find a game by that id");
            return "";
        }
        //we just want to draw the game for now
        //find the game to draw
        ChessGame game = gamesById.get(Integer.parseInt(params[0]));
        //drawing
        DrawBoard printBoard = new DrawBoard();
        return printBoard.draw(game, "WHITE");
    }

    private String joinGame(String[] params){
        //check games
        if(params.length != 2){
            System.out.print("Wrong amount of arguments, expected something like - ");
            System.out.println("join <PlayerColor> <ID>");
            return "";
        }
        //we need to check that we have the right arguments here as well
        try{
            Integer.parseInt(params[1]);
        }catch(NumberFormatException ex){
            System.out.println("Hmm, something seems to be wrong with you game ID");
            return "";
        }
        if(!params[0].equalsIgnoreCase("BLACK")){
            if(!params[0].equalsIgnoreCase("WHITE")){
                System.out.println("Something seems to be wrong with the entered player color");
                return "";
            }
        }
        //first we need to find out actual game
        ChessGame game = gamesById.get(Integer.parseInt(params[1]));
        int id = idLog.get(Integer.parseInt(params[1]));
        try {
            JoinGameRequest join = new JoinGameRequest(params[0].toUpperCase(), id);
            server.joinGame(join, authToken);
            //now we need to display the game
            DrawBoard printBoard = new DrawBoard();
            return printBoard.draw(game, params[0].toUpperCase());
        }catch(ResponseException ex){
            System.out.println(ex.getMessage());
        }
        return "";
    }

    private String list(){
        try{

            GamesReturned gamesModel = server.listGames(authToken);
            List<Game> games = gamesModel.games();
            StringBuilder out = new StringBuilder();
            int i = 1;
            //everytime we call this we want to reset our logs
            gamesById.clear();
            idLog.clear();
            for(Game game : games){
                //we want to add the data to a string stream to out
                //start with the number
                out.append("ID: ").append(i);
                //now name
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
                idLog.put(i, game.gameID());
                i++;
            }
            return out.toString();


        }catch(Exception ex){
            return ex.getMessage();
        }
    }


    private String createGame(String[] params){
        if(params.length!=1){
            System.out.println("Wrong number of params, expecting something like - create <NAME>");
            return "";
        }
        Map<String, String> data = new HashMap<>(Map.of());
        data.put("gameName", params[0]);
        try {
            server.createGame(data, this.authToken);
        }catch(ResponseException ex){
            System.out.println(ex.getMessage());
            return "";
        }
        return "Game created successfully!";

    }

    private String logout() {
        //first parse what my server expects
        //we need my auth token
        try {
            server.logout(this.authToken);
        }catch(ResponseException ex){
            System.out.println(ex.getMessage());
            return "";
        }
        return "goodbye";
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
