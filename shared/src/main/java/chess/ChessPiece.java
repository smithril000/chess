package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessPiece.PieceType type;
    private final ChessGame.TeamColor teamColor;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.type = type;
        this.teamColor = pieceColor;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return type == that.type && teamColor == that.teamColor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, teamColor);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "type=" + type +
                ", teamColor=" + teamColor +
                '}';
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.teamColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        //first find out what piece we've got
        ChessPiece piece = board.getPiece(myPosition);
        //create our collection
        List<ChessMove> moves = new ArrayList<>();
        if(piece.getPieceType() == PieceType.PAWN){
            moves = pawnMoves(board, myPosition, piece, moves);
        }
        return moves;
    }
    private List<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece, List<ChessMove> moves){
        //for pawns they can move 2 foward at the start, one if nothing in front, and diagonal if taking

        int row = myPosition.getRow()+1;
        int col = myPosition.getColumn()+1;
        //first check if we are at starting position for black and white
        if(row == 2 && piece.getTeamColor() == ChessGame.TeamColor.BLACK){
            //now check if the area is unblocked
            if(board.getPiece(new ChessPosition(row+1, col)) == null) {
                moves.add(new ChessMove(myPosition, new ChessPosition(row + 1, col), null));
            }
            if(board.getPiece(new ChessPosition(row+2, col)) == null){
                moves.add(new ChessMove(myPosition, new ChessPosition(row + 2, col), null));
            }
        }
        if(row == 7 && piece.getTeamColor() == ChessGame.TeamColor.WHITE){
            //now check if the area is unblocked
            if(board.getPiece(new ChessPosition(row-1, col)) == null) {
                moves.add(new ChessMove(myPosition, new ChessPosition(row + 1, col), null));
            }
            if(board.getPiece(new ChessPosition(row-2, col)) == null){
                moves.add(new ChessMove(myPosition, new ChessPosition(row + 2, col), null));
            }
        }
        //now check if we can move foward or back, not hitting edge of board
        int row_dir = 0;
        if(piece.getTeamColor() == ChessGame.TeamColor.WHITE){
            row_dir = -1;
        }else{
            row_dir = 1;
        }
        int start = -1;
        int end = 1;
        if(col == 1){
            start = 0;
        }else if(col == 8){
            end = 0;
        }
        while(start <= end){
            ChessPosition pos = new ChessPosition(row + row_dir, col + start);
            if(moveOrTake(pos, board, piece.getTeamColor())){
                moves.add(new ChessMove(myPosition, pos, null));
            }
            start++;
        }

        return moves;
    }

    private Boolean moveOrTake(ChessPosition pos, ChessBoard board, ChessGame.TeamColor color){
        if(board.getPiece(pos) == null){
            return true;
        }else if(board.getPiece(pos).getTeamColor() != color){
            return true;
        }else{
            return false;
        }
    }
}
