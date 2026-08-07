package client;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import client.websocket.ClientWebSocket;
import com.google.gson.Gson;
import ui.DrawBoard;
import websocket.commands.UserGameCommand;

import java.util.Arrays;
import java.util.Scanner;

public class GamePlay {
    private final String auth;
    private final ChessGame game;
    private final String color;
    private final int id;
    private final ClientWebSocket ws;
    private final String name;

    public GamePlay(String auth, ClientWebSocket ws, ChessGame game, String color, int id, String name) {
        this.auth = auth;
        this.game = game;
        this.color = color;
        this.id = id;
        this.ws = ws;
        this.name = name;
        //TEST UPDATE MY WS
        this.ws.setColor(color);
    }

    public void run() {
        System.out.println("Welcome to the Game");
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("goodbye")) {
            System.out.print("[Logged in - in game] >> ");
            String line = scanner.nextLine();
            try {
                result = eval(line);
                System.out.println(result);
            } catch (Throwable ex) {
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
                case "leave" -> leave();
                case "valid" -> moves();
                case "move" -> makeMove(params);
                case "resign" -> resign();
                default -> help();
            };
        } catch (Throwable ex) {
            return ex.getMessage();
        }
    }

    private String resign() {
        //remove player from game and db - game over
        UserGameCommand comm = new UserGameCommand(UserGameCommand.CommandType.RESIGN, auth, id);
        ws.sendCommand(new Gson().toJson(comm));
        return "goodbye";
    }

    private String makeMove(String[] params) {
        //check auth - check valid moves - make move - update
        UserGameCommand comm = new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE, auth, id);
        comm.setPlayerColor(this.color);
        comm.setName(this.name);
        //set the move we want to make - parse the info
        int rowStart;
        int colStart;

        char[] chars = params[0].toCharArray();
        colStart = chars[0] - 'a';
        rowStart = chars[1] - '0';
        chars = params[1].toCharArray();
        int colEnd = chars[0] - 'a';
        int rowEnd = chars[1] - '0';
        ChessPosition startPos = new ChessPosition(rowStart, colStart);
        ChessPosition endPos = new ChessPosition(rowEnd, colEnd);
        //FIX PROMO PIECE
        ChessMove move = new ChessMove(startPos, endPos, null);
        comm.setMove(move);
        ws.sendCommand(new Gson().toJson(comm));
        //we need to check if game is over
        return "done with move";
    }

    private String moves() {
        //get new board with highlighted moves

        return "";
    }

    private String leave() {
        //we need to send a ws and make sure the game gets updated in db
        UserGameCommand com = new UserGameCommand(UserGameCommand.CommandType.LEAVE, auth, id);
        com.setPlayerColor(this.color);
        com.setName(this.name);
        ws.sendCommand(new Gson().toJson(com));
        return "goodbye";
    }

    private String redraw() {
        //we just need to re-print out the board
        //FIX
        DrawBoard printBoard = new DrawBoard();
        return printBoard.draw(this.game, this.color);
    }


    private String help(){
        return "Replace this with help";
    }
}
