package chess;
import java.util.Arrays;
import java.util.Objects;

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
        //resetBoard();
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

    public ChessBoard copyBoard(){
        ChessBoard copy = new ChessBoard();
        for(int x = 0; x < 8; x++){
            for(int y = 0; y < 8; y++){
                copy.squares[y][x] = this.squares[y][x];
            }
        }
        return copy;
    }

    public void removePiece(ChessPosition pos){
        if(this.squares[pos.getRow()][pos.getColumn()] != null){
            this.squares[pos.getRow()][pos.getColumn()] = null;
        }
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
        setPieceFrame(pieceFrame);
        //black at top - we will make that 0 for now
        for(int i = 0; i < 8; i++){
            addPiece(new ChessPosition(2, (i % 8)+1), new ChessPiece(ChessGame.TeamColor.WHITE, pieceFrame[i]));
        }
        for(int i = 8; i < 16; i++){
            addPiece(new ChessPosition((i / 8), (i % 8)+1), new ChessPiece(ChessGame.TeamColor.WHITE, pieceFrame[i]));
        }
        //now white at bottom
        for(int i = 0; i < 16; i++){
            addPiece(new ChessPosition((i / 8) + 7, (i % 8)+1), new ChessPiece(ChessGame.TeamColor.BLACK, pieceFrame[i]));
        }
    }

    private void setPieceFrame(ChessPiece.PieceType[] arr){
        for(int i = 0; i <8; i++){
            arr[i] = ChessPiece.PieceType.PAWN;
        }
        arr[8] = ChessPiece.PieceType.ROOK;
        arr[9] = ChessPiece.PieceType.KNIGHT;
        arr[10] = ChessPiece.PieceType.BISHOP;
        arr[11] = ChessPiece.PieceType.QUEEN;
        arr[12] = ChessPiece.PieceType.KING;
        arr[13] = ChessPiece.PieceType.BISHOP;
        arr[14] = ChessPiece.PieceType.KNIGHT;
        arr[15] = ChessPiece.PieceType.ROOK;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }

    @Override
    public String toString() {
        return "ChessBoard{" +
                "squares=" + Arrays.toString(squares) +
                '}';
    }
}
