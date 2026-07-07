package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

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
        this.board = new ChessBoard();
        this.board.resetBoard();
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, board);
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
        if(piece == null)return null;
        Collection<ChessMove> moves = piece.pieceMoves(this.board, startPosition);
        Collection<ChessMove> goodMoves = new ArrayList<>();
        //we need to filter which ones don't put us in check
        //we can start to see if it is even our turn
        //if(piece.getTeamColor() != this.teamTurn)return null;
        for(ChessMove move : moves){
            //make the move in a temp board
            ChessBoard temp = this.board.copyBoard();
            //check if we are in check and move puts us out of check
            if(checkHelper(piece.getTeamColor(), temp)){
                makeTempMove(move, temp);
                if(!checkHelper(piece.getTeamColor(), temp)){
                    //still in check - not valid
                    goodMoves.add(move);
                }
            }else {
                makeTempMove(move, temp);


                //check if the move puts us in check
                if (!checkHelper(piece.getTeamColor(), temp)) {
                    //cant use this move
                    goodMoves.add(move);
                }
            }
        }
        return goodMoves;
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        //check if move is valid
        Collection<ChessMove> moves = validMoves(move.getStartPosition());
        if(moves == null || !moves.contains(move)){
            throw new InvalidMoveException("Cant make that move");
        }
        //we want to add and delete the peice from each move
        ChessPiece piece = this.board.getPiece(move.getStartPosition());
        //check if we are out of turn
        if(piece.getTeamColor() != this.teamTurn){
            throw new InvalidMoveException("Cant make that move");
        }
        //check if we are promoting
        if(piece.getPieceType() == ChessPiece.PieceType.PAWN && move.getPromotionPiece()!=null){
            piece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
        }
        //add
        this.board.addPiece(move.getEndPosition(), piece);
        //delete
        this.board.removePiece(move.getStartPosition());
        //change the move
        if(this.teamTurn == TeamColor.WHITE){
            this.teamTurn = TeamColor.BLACK;
        }else{
            this.teamTurn = TeamColor.WHITE;
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return checkHelper(teamColor, this.board);
    }

    private boolean checkHelper(TeamColor color, ChessBoard board){
        //we need to check if there are any moves from the opps that can hit the king
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPosition kingPos = null;
        for(int i = 0; i < 64; i++){
            ChessPosition pos = new ChessPosition((i / 8)+1, (i % 8)+1);
            ChessPiece pieceCheck = board.getPiece(pos);
            if(pieceCheck != null) {
                if (pieceCheck.getTeamColor() != color) {
                    //add its possible moves to our moves
                    moves.addAll(pieceCheck.pieceMoves(board, pos));
                }
                //we also want to find our king
                if (pieceCheck.getTeamColor() == color && pieceCheck.getPieceType() == ChessPiece.PieceType.KING) {
                    kingPos = pos;
                }
            }
        }
        for(ChessMove move : moves){
            assert kingPos != null;
            if(move.getEndPosition().getRow() == kingPos.getRow() && move.getEndPosition().getColumn() == kingPos.getColumn()){
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
            ChessPosition pos = new ChessPosition((i / 8)+1, (i % 8)+1);
            ChessPiece pieceCheck = this.board.getPiece(pos);
            if(pieceCheck != null) {
                if (pieceCheck.getTeamColor() == teamColor) {
                    //add its possible moves to our moves
                    moves.addAll(pieceCheck.pieceMoves(this.board, pos));
                }
            }
        }
        for(ChessMove move : moves){
            //we want to test making the move to see if we are still in check
            //create a new board
            ChessBoard temp = this.board.copyBoard();
            makeTempMove(move, temp);
            //check if we are still in check
            if(!checkHelper(teamColor, temp)){
                return false;
            }
        }
        return true;
    }

    private void makeTempMove(ChessMove move, ChessBoard temp){
        //we want to add and delete the peice from each move
        ChessPiece piece = temp.getPiece(move.getStartPosition());
        //add
        temp.addPiece(move.getEndPosition(), piece);
        //delete
        temp.removePiece(move.getStartPosition());

    }



    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        //if we run thorugh all the pieces and none have valid moves it is stalemate
        if(!this.isInCheck(teamColor)) {
            return isInCheckmate(teamColor);
        }
        return false;
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
