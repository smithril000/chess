package ui;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Objects;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLACK;

public class DrawBoard {
    static ChessGame game;
    static String color = "NONE";

    public static String draw(ChessGame chessGame, String playerColor){
        color = playerColor;
        game = chessGame;
        StringBuilder out = new StringBuilder();
        out.append(headerRow());
        if(Objects.equals(color, "BLACK")){
            for(int i = 1; i <= 8; i++){
                out.append(row(i));
            }
        }else{
            for(int i = 8; i >= 1; i--){
                out.append(row(i));
            }
        }

        out.append(headerRow());
        out.append(RESET_TEXT_COLOR);
        return out.toString();
    }
    private static String headerRow(){
        StringBuilder out = new StringBuilder();
        out.append(SET_BG_COLOR_LIGHT_GREY);
        out.append(SET_TEXT_COLOR_BLACK);
        if(Objects.equals(color, "WHITE")) {
            out.append("    a  b  c  d  e  f  g  h    ");
        }else{
            out.append("    h  g  f  e  d  c  b  a    ");
        }
        out.append(RESET_BG_COLOR);
        out.append("\n");
        return out.toString();
    }
    private static String row(int row){
        StringBuilder out = new StringBuilder();
        out.append(SET_BG_COLOR_LIGHT_GREY);
        out.append(SET_TEXT_COLOR_BLACK);
        out.append(" " + row + " ");
        if(color == "BLACK"){
            for(int i = 0; i < 8; i++){

                if(i % 2  + (row % 2) == 1){
                    out.append(SET_BG_COLOR_LIGHT_GREY);
                }else{
                    out.append(SET_BG_COLOR_DARK_GREY);
                }
                if(game.getBoard().getPiece(new ChessPosition(row,i+1)) != null){
                    out.append(getPieceType(game.getBoard().getPiece(new ChessPosition(row,i+1))));
                }else{
                    out.append(EMPTY);
                }

            }
        }else{
            for(int i = 8; i >= 1; i--){

                if(i % 2  + (row % 2) == 1){
                    out.append(SET_BG_COLOR_LIGHT_GREY);
                }else{
                    out.append(SET_BG_COLOR_DARK_GREY);
                }
                if(game.getBoard().getPiece(new ChessPosition(row,i)) != null){
                    out.append(getPieceType(game.getBoard().getPiece(new ChessPosition(row,i))));
                }else{
                    out.append(EMPTY);
                }

            }
        }

        out.append(SET_BG_COLOR_LIGHT_GREY);
        out.append(SET_TEXT_COLOR_BLACK);
        out.append(" " + row + " ");
        out.append(RESET_BG_COLOR);
        out.append("\n");
        return out.toString();
    }

    private static String getPieceType(ChessPiece piece){
        StringBuilder out = new StringBuilder();
        if(piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            out.append(SET_TEXT_COLOR_WHITE);
            switch (piece.getPieceType()) {
                case KING -> out.append(WHITE_KING);
                case QUEEN -> out.append(WHITE_QUEEN);
                case BISHOP -> out.append(WHITE_BISHOP);
                case KNIGHT -> out.append(WHITE_KNIGHT);
                case ROOK -> out.append(WHITE_ROOK);
                case PAWN -> out.append(WHITE_PAWN);
            }
        }else{
            out.append(SET_TEXT_COLOR_BLACK);
            switch (piece.getPieceType()) {
                case KING -> out.append(BLACK_KING);
                case QUEEN -> out.append(BLACK_QUEEN);
                case BISHOP -> out.append(BLACK_BISHOP);
                case KNIGHT -> out.append(BLACK_KNIGHT);
                case ROOK -> out.append(BLACK_ROOK);
                case PAWN -> out.append(BLACK_PAWN);
            }
        }
        //out.append(RESET_TEXT_COLOR);
        //out.append(RESET_TEXT_BOLD_FAINT);
        return out.toString();
    }
}
