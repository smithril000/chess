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

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
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
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        if(piece.getPieceType() == PieceType.BISHOP){
            //return List.of(new ChessMove(new ChessPosition(5,4), new ChessPosition(1,8), null));
            List<ChessMove> moves = new ArrayList<>();
            //pos pos
            int x = myPosition.getRow() + 1;
            int y = myPosition.getColumn() + 1;
            while(x <= 8 && y <= 8){
                moves.add(new ChessMove(myPosition, new ChessPosition(x,y),null));
                x++;
                y++;
            }
            //neg neg
            x = myPosition.getRow() - 1;
            y = myPosition.getColumn() - 1;
            while (x > 0 && y > 0) {
                moves.add(new ChessMove(myPosition, new ChessPosition(x,y),null));
                x--;
                y--;
            }
            //pos neg
            x = myPosition.getRow() + 1;
            y = myPosition.getColumn() - 1;
            while(x <= 8 && y > 0){
                moves.add(new ChessMove(myPosition, new ChessPosition(x,y),null));
                x++;
                y--;
            }
            //neg pos
            x = myPosition.getRow() - 1;
            y = myPosition.getColumn() + 1;
            while(x > 0 && y <= 8){
                moves.add(new ChessMove(myPosition, new ChessPosition(x,y),null));
                x--;
                y++;
            }
            return moves;
        }else if(piece.getPieceType() == PieceType.PAWN){
            //can move up 2 if starting and 1 anytime else

        }
         return List.of();
    }
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
