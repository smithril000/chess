package ui;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Objects;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLACK;

public class DrawBoard {
    private ChessGame game;
    private String color = "";

    public  String draw(ChessGame chessGame, String playerColor){
        color = playerColor;
        game = chessGame;
        StringBuilder out = new StringBuilder();
        out.append(headerRow());
        if(color.equalsIgnoreCase("BLACK")){
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
    private String headerRow(){
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
    private String row(int row){
        StringBuilder out = new StringBuilder();
        out.append(SET_BG_COLOR_LIGHT_GREY);
        out.append(SET_TEXT_COLOR_BLACK);
        out.append(" ").append(row).append(" ");
        if(Objects.equals(color, "BLACK")){
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
                    out.append(SET_BG_COLOR_DARK_GREY);
                }else{
                    out.append(SET_BG_COLOR_LIGHT_GREY);
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
        out.append(" ").append(row).append(" ");
        out.append(RESET_BG_COLOR);
        out.append("\n");
        return out.toString();
    }

    private static String getPieceType(ChessPiece piece){
        StringBuilder out = new StringBuilder();
        if(piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            setUp(piece, out, SET_TEXT_COLOR_WHITE, WHITE_KING, WHITE_QUEEN, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK, WHITE_PAWN);
        }else{
            setUp(piece, out, EscapeSequences.SET_TEXT_COLOR_BLACK, EscapeSequences.BLACK_KING, EscapeSequences.BLACK_QUEEN, EscapeSequences.BLACK_BISHOP, EscapeSequences.BLACK_KNIGHT, EscapeSequences.BLACK_ROOK, EscapeSequences.BLACK_PAWN);
        }
        return out.toString();
    }

    private static void setUp(ChessPiece piece, StringBuilder out, String setTextColorWhite, String whiteKing, String whiteQueen, String whiteBishop, String whiteKnight, String whiteRook, String whitePawn) {
        out.append(setTextColorWhite);
        switch (piece.getPieceType()) {
            case KING -> out.append(whiteKing);
            case QUEEN -> out.append(whiteQueen);
            case BISHOP -> out.append(whiteBishop);
            case KNIGHT -> out.append(whiteKnight);
            case ROOK -> out.append(whiteRook);
            case PAWN -> out.append(whitePawn);
        }
    }
}
