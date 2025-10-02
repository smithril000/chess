package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor turn;
    private ChessBoard board;
    public ChessGame() {
        this.turn = TeamColor.WHITE;
        this.board = new ChessBoard();
        this.board.resetBoard();
    }
    public ChessGame(ChessGame other){
        this.turn = other.turn;
        this.board = other.board;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        if(team == TeamColor.WHITE){
            this.turn = TeamColor.WHITE;
        }else{
            this.turn = TeamColor.BLACK;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, turn);
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "turn=" + turn +
                ", board=" + board +
                '}';
    }


    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    private void changeTeam(){
        if(turn == TeamColor.WHITE){
            turn = TeamColor.BLACK;
        }else{
            turn = TeamColor.WHITE;
        }
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        //check if we have a piece there
        ChessPiece piece = board.getPiece(startPosition);
        if(piece != null){
            moves = piece.pieceMoves(board, startPosition);
        }else{
            return null;
        }
        //we need to see if we are currently in check
        if(this.isInCheck(piece.getTeamColor())){
            //we are in check, so the moves have to remove that

            Collection<ChessMove> movesToRemove = new ArrayList<>();
            for (ChessMove move : moves) {
                ChessBoard copy = this.board.copyBoard();

                this.makeTempMove(move, this);
                if (this.isInCheck(piece.getTeamColor())) {
                    //we cant use that move
                    movesToRemove.add(move);
                }
                //we need to put the board back
                this.board.squares = copy.squares;
            }
            //now to remove the moves we can't use
            for(ChessMove move : movesToRemove){
                moves.remove(move);
            }
        }else {
            //we need to make a copy of the moves to not mess things up
            Collection<ChessMove> movesToRemove = new ArrayList<>();
            for (ChessMove move : moves) {
                ChessBoard copy = this.board.copyBoard();

                this.makeTempMove(move, this);
                if (this.isInCheck(piece.getTeamColor())) {
                    //we cant use that move
                    movesToRemove.add(move);
                }
                //we need to put the board back
                this.board.squares = copy.squares;
            }
            //now to remove the moves we can't use
            for(ChessMove move : movesToRemove){
                moves.remove(move);
            }
        }
        return moves;
    }

    public void makeTempMove(ChessMove move, ChessGame tempGame){
        ChessPiece piece = tempGame.board.getPiece(move.getStartPosition());
        //we are doing the checkks elsewhere so just move the piece here
        tempGame.board.addPiece(move.getEndPosition(), piece);
        tempGame.board.removePiece(move.getStartPosition());
    }
    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        //check if it is the correct turn and the move is legal
        ChessPiece piece = board.getPiece(move.getStartPosition());
        Collection<ChessMove> moves = this.validMoves(move.getStartPosition());
        if(piece != null && piece.getTeamColor() == turn && moves.contains(move)){
            //we are good to make the move
            //check if we are promoting
            if(move.getPromotionPiece() != null){
                piece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
            }
            board.addPiece(move.getEndPosition(), piece);
            board.removePiece(move.getStartPosition());
            this.changeTeam();
        }else{
            throw new InvalidMoveException("Cant make that move");
        }

    }

    private ChessPosition kingPos(TeamColor teamColor){
        for(int x = 1; x <= 8; x++) {
            for (int y = 1; y <= 8; y++) {
                if(this.board.getPiece(new ChessPosition(y,x)) != null && this.board.getPiece(new ChessPosition(y,x)).getPieceType() == ChessPiece.PieceType.KING && this.board.getPiece(new ChessPosition(y,x)).getTeamColor() == teamColor){
                    //found the king, return its pos
                    return new ChessPosition(y,x);
                }
            }
        }
        return null;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        boolean check = false;
        ChessPosition kingSpot = this.kingPos(teamColor);
        //loop over every square checking if it is an enemy
        for(int x = 1; x <= 8; x++){
            for(int y = 1; y <= 8; y++){
                //check the board at that location and see if its an enemy piece
                ChessPiece piece = this.board.getPiece(new ChessPosition(y,x));
                if(piece != null && piece.getTeamColor() != teamColor){
                    //this is an enemy piece
                    //check if any of its moves can hit the king
                    Collection<ChessMove> moves = piece.pieceMoves(this.board, new ChessPosition(y,x));
                    for(ChessMove move : moves){
                        if(move.getEndPosition().getRow() == kingSpot.getRow() && move.getEndPosition().getColumn() == kingSpot.getColumn()){
                            //we are in check
                            return true;
                        }
                    }
                }
            }

        }
        return check;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        boolean checkmate = false;
        Collection<ChessMove> moves = new ArrayList<>();
        //run through each piece of the team side and see if any of their moves will not be in check
        //we can do this by seeing if there are any valid moves
        for(int x = 1; x <= 8; x++){
            for(int y =1; y <= 8; y++){
                //check if we are looking at the right piece
                if(this.board.getPiece(new ChessPosition(y,x)) != null && this.board.getPiece(new ChessPosition(y,x)).getTeamColor() == teamColor){
                    //see what valid moves it has
                    moves.addAll(this.validMoves(new ChessPosition(y,x)));
                }
            }
        }
        //if our list of moves is empty we are in checkmate
        if(moves.isEmpty()){
            return true;
        }else{
            return false;
        }
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        //we can do something similar.
        //if we run thorugh all the pieces and none have valid moves it is stalemate
        if(!this.isInCheck(teamColor)) {
            return isInCheckmate(teamColor);
        }
        return false;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }



}
