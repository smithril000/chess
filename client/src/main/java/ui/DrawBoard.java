package ui;

import chess.ChessGame;

import java.util.Objects;

import static ui.EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLACK;

public class DrawBoard {
    private ChessGame game;
    private String color;

    public String draw(ChessGame chessGame, String playerColor){
        color = playerColor;
        game = chessGame;
        StringBuilder out = new StringBuilder();
        out.append(head());
        return out.toString();
    }

    private String head() {
        //this needs to have the alphabet in diff directions
        StringBuilder out = new StringBuilder();
        out.append(SET_BG_COLOR_LIGHT_GREY);
        out.append(SET_TEXT_COLOR_BLACK);
        if(Objects.equals(color, "WHITE")) {
            out.append("    a  b  c  d  e  f  g  h    ");
        }else{
            out.append("    h  g  f  e  d  c  b  a    ");
        }
        return out.toString();
    }
}
