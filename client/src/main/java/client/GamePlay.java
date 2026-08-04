package client;

import chess.ChessGame;
import ui.DrawBoard;

import java.util.Arrays;
import java.util.Scanner;

public class GamePlay {
    private final String auth;
    private final ServerFacade server;
    private ChessGame game;
    private final String color;
    public GamePlay(String auth, ServerFacade server, ChessGame game, String color){
        this.auth = auth;
        this.server = server;
        this.game = game;
        this.color = color;
    }

    public void run(){
        System.out.println("Welcome to the Game");
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while(!result.equals("goodbye")){
            System.out.print("[Logged in - in game] >> ");
            String line = scanner.nextLine();
            try{
                result = eval(line);
                System.out.println(result);
            }catch (Throwable ex){
                var errorMessage = ex.toString();
                System.out.println(errorMessage);
            }
            //check if we still have an auth
        }
    }
    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "redraw" -> redraw();
                default -> help();
            };
        } catch (Throwable ex) {
            return ex.getMessage();
        }
    }

    private String redraw() {
        //we just need to re-print out the board
        DrawBoard printBoard = new DrawBoard();
        return printBoard.draw(this.game, this.color);
    }

    private String help(){
        return "Replace this with help";
    }
}
