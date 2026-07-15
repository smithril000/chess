package model;

public record Game(int gameID, String whiteUsername, String blackUsername, String gameName) {
    public Game changeWhite(String newWhiteName){
        return new Game(gameID, newWhiteName, blackUsername, gameName);
    }
    public Game changeBlack(String newBlackName){
        return new Game(gameID, whiteUsername, newBlackName, gameName);
    }
}
