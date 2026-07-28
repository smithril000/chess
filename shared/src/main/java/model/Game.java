package model;

import chess.ChessGame;

public record Game(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
    public Game changeWhite(String newWhiteName){
        return new Game(gameID, newWhiteName, blackUsername, gameName, game);
    }
    public Game changeBlack(String newBlackName){
        return new Game(gameID, whiteUsername, newBlackName, gameName, game);
    }
}
