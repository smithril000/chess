package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamTurn;
    private ChessBoard board;
    public ChessGame() {
        this.teamTurn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.teamTurn;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = this.board.getPiece(startPosition);
        Collection<ChessMove> moves = piece.pieceMoves(this.board, startPosition);
        //we need to filter which ones don't put us in check
        return moves;
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        //we need to check if there are any moves from the opps that can hit the king
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPosition kingPos = null;
        for(int i = 0; i < 64; i++){
            ChessPosition pos = new ChessPosition(i / 8, i % 8);
            ChessPiece pieceCheck = this.board.getPiece(pos);
            if(pieceCheck != null) {
                if (pieceCheck.getTeamColor() != this.teamTurn) {
                    //add its possible moves to our moves
                    moves.addAll(pieceCheck.pieceMoves(this.board, pos));
                }
                //we also want to find our king
                if (pieceCheck.getTeamColor() == this.teamTurn && pieceCheck.getPieceType() == ChessPiece.PieceType.KING) {
                    kingPos = pos;
                }
            }
        }
        for(ChessMove move : moves){
            if(move.getEndPosition() == kingPos){
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        //this only works if the turn is the teamcolor implemented
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPosition kingPos = null;
        for(int i = 0; i < 64; i++){
            ChessPosition pos = new ChessPosition(i / 8, i % 8);
            ChessPiece pieceCheck = this.board.getPiece(pos);
            if(pieceCheck != null) {
                if (pieceCheck.getTeamColor() != this.teamTurn) {
                    //add its possible moves to our moves
                    moves.addAll(pieceCheck.pieceMoves(this.board, pos));
                }
            }
        }
        for(ChessMove move : moves){
            //we want to test making the move to see if we are still in check

        }
        return false;
    }

    private void makeTempMove(){
        //create a new board
        ChessBoard temp = new ChessBoard();
    }



    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard to a given board
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
