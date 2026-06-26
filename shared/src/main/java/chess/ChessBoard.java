package chess;
import java.util.Arrays;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    //allocate an array
    ChessPiece[][] squares = new ChessPiece[8][8];
    public ChessBoard() {
        resetBoard();
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {

        this.squares[position.getRow()][position.getColumn()] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return this.squares[position.getRow()][position.getColumn()];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {

        //set the board up for a base game

        ChessPiece.PieceType[] pieceFrame = new ChessPiece.PieceType[16];
        pieceFrame = setPieceFrame(pieceFrame);
        //black at top - we will make that 0 for now

    }

    private ChessPiece.PieceType[] setPieceFrame(ChessPiece.PieceType[] arr){
        for(int i = 0; i <=8; i++){
            arr[i] = ChessPiece.PieceType.PAWN;
        }
        arr[9] = ChessPiece.PieceType.ROOK;
        arr[10] = ChessPiece.PieceType.KNIGHT;
        arr[11] = ChessPiece.PieceType.BISHOP;
        arr[12] = ChessPiece.PieceType.QUEEN;
        arr[13] = ChessPiece.PieceType.KING;
        arr[14] = ChessPiece.PieceType.BISHOP;
        arr[15] = ChessPiece.PieceType.KNIGHT;
        arr[16] = ChessPiece.PieceType.ROOK;
        return arr;
    }
}
