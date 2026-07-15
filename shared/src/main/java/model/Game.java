package model;

public record Game(int gameID, String whiteName, String blackName, String gameName) {
    public Game changeWhite(String newWhiteName){
        return new Game(gameID, newWhiteName, blackName, gameName);
    }
    public Game changeBlack(String newBlackName){
        return new Game(gameID, whiteName, newBlackName, gameName);
    }
}
