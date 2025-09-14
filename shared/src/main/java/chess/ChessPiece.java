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
            return bishMove(board, myPosition, piece);
        }else if(piece.getPieceType() == PieceType.PAWN){
            //can move up 2 if starting and 1 anytime else
            return pawnMove(board, myPosition, piece);
        }else if(piece.getPieceType() == PieceType.KING){
            return kingMove(board, myPosition, piece);
        }
         return List.of();
    }

    private Collection<ChessMove> bishMove(ChessBoard board, ChessPosition myPosition, ChessPiece piece){
        //return List.of(new ChessMove(new ChessPosition(5,4), new ChessPosition(1,8), null));
        List<ChessMove> moves = new ArrayList<>();
        //pos pos
        int x = myPosition.getRow() + 1;
        int y = myPosition.getColumn() + 1;
        while(x <= 8 && y <= 8){
            if(board.getPiece(new ChessPosition(x,y)) == null) {
                moves.add(new ChessMove(myPosition, new ChessPosition(x, y), null));
                x++;
                y++;
            }else{
                //there is a peice there, see if its opposite color to take
                if(board.getPiece(new ChessPosition(x,y)).getTeamColor() == piece.getTeamColor()){
                    //cannot take is blocked
                    x = 10;
                    y = 10;
                }else{
                    //can take

                    moves.add(new ChessMove(myPosition, new ChessPosition(x, y), null));
                    x = 10;
                    y = 10;

                }

            }
        }
        //neg neg
        x = myPosition.getRow() - 1;
        y = myPosition.getColumn() - 1;
        while (x > 0 && y > 0) {
            if(board.getPiece(new ChessPosition(x,y)) == null) {
                moves.add(new ChessMove(myPosition, new ChessPosition(x, y), null));
                x--;
                y--;
            }else{
                //there is a peice there, see if its opposite color to take
                if(board.getPiece(new ChessPosition(x,y)).getTeamColor() == piece.getTeamColor()){
                    //cannot take is blocked
                    x = -1;
                    y = -1;
                }else{
                    //can take

                    moves.add(new ChessMove(myPosition, new ChessPosition(x, y), null));
                    x = -1;
                    y = -1;

                }

            }
        }
        //pos neg
        x = myPosition.getRow() + 1;
        y = myPosition.getColumn() - 1;
        while(x <= 8 && y > 0){
            if(board.getPiece(new ChessPosition(x,y)) == null) {
                moves.add(new ChessMove(myPosition, new ChessPosition(x, y), null));
                x++;
                y--;
            }else{
                //there is a peice there, see if its opposite color to take
                if(board.getPiece(new ChessPosition(x,y)).getTeamColor() == piece.getTeamColor()){
                    //cannot take is blocked
                    x = 10;
                    y = -1;
                }else{
                    //can take

                    moves.add(new ChessMove(myPosition, new ChessPosition(x, y), null));
                    x = 10;
                    y = -1;

                }

            }
        }
        //neg pos
        x = myPosition.getRow() - 1;
        y = myPosition.getColumn() + 1;
        while(x > 0 && y <= 8){
            if(board.getPiece(new ChessPosition(x,y)) == null) {
                moves.add(new ChessMove(myPosition, new ChessPosition(x, y), null));
                x--;
                y++;
            }else{
                //there is a peice there, see if its opposite color to take
                if(board.getPiece(new ChessPosition(x,y)).getTeamColor() == piece.getTeamColor()){
                    //cannot take is blocked
                    x = -1;
                    y = 10;
                }else{
                    //can take

                    moves.add(new ChessMove(myPosition, new ChessPosition(x, y), null));
                    x = -1;
                    y = 10;

                }

            }
        }
        return moves;
    }
    private Collection<ChessMove> pawnMove(ChessBoard board, ChessPosition myPosition, ChessPiece piece){
        List<ChessMove> moves = new ArrayList<>();
        //if pawn is white
        if(piece.getTeamColor() == ChessGame.TeamColor.WHITE){
            //if it is its first turn it can more one or two
            if(myPosition.getRow() == 2 && board.getPiece(new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn())) == null){
                //moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()), null));
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow()+2, myPosition.getColumn()), null));
            }
            //any foward movement that isn't blocked an dnot a promotion
            if(board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn())) == null && myPosition.getRow() < 8){
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()), null));
            }
            //taking peices movement
            if(myPosition.getColumn() < 8 && board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn()+1)) != null){
                //taking to right
                if(board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn()+1)).getTeamColor() == ChessGame.TeamColor.BLACK) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1), null));
                }
            }
            if(myPosition.getColumn() > 0 && board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn()-1)) != null){
                //taking to right
                if(board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn()-1)).getTeamColor() == ChessGame.TeamColor.BLACK) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1), null));
                }
            }
            //promotions
            //unblocked
            if(myPosition.getRow() == 7 && board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn())) == null){
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn()), PieceType.QUEEN));
            }

        }
        //if pawn is black\
        if(piece.getTeamColor() == ChessGame.TeamColor.BLACK){
            //if it is its first turn it can more one or two
            if(myPosition.getRow() == 7 && board.getPiece(new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn())) == null){
                //moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()), null));
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow()-2, myPosition.getColumn()), null));
            }
            //any foward movement that isn't blocked an dnot a promotion
            if(board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn())) == null && myPosition.getRow() > 0){
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()), null));
            }
            //taking peices movement
            if(myPosition.getColumn() < 8 && board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn()+1)) != null){
                //taking to right
                if(board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn()+1)).getTeamColor() == ChessGame.TeamColor.WHITE) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1), null));
                }
            }
            if(myPosition.getColumn() > 0 && board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn()-1)) != null){
                //taking to right
                if(board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn()-1)).getTeamColor() == ChessGame.TeamColor.WHITE) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1), null));
                }
            }
        }

        return moves;
    }
    private Collection<ChessMove> kingMove(ChessBoard board, ChessPosition myPosition, ChessPiece piece){
        List<ChessMove> moves = new ArrayList<>();
        //foward
        if(myPosition.getRow() < 8){
            if(board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn())) != null){
                //there is a piece there
                if(board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn())).getTeamColor() != piece.getTeamColor()){
                    // can take the piece
                    moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn()), null));
                }

            }
            else {
                //no peice there
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn()), null));
            }
        }
        //foward right
        if(myPosition.getRow() < 8 && myPosition.getColumn() < 8){
            if(board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn()+1)) != null){
                //there is a piece there
                if(board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn()+1)).getTeamColor() != piece.getTeamColor()){
                    // can take the piece
                    moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn()+1), null));
                }

            }else {
                //no peice there
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1), null));
            }
        }
        //right
        if(myPosition.getColumn() < 8){
            if(board.getPiece(new ChessPosition(myPosition.getRow(), myPosition.getColumn()+1)) != null){
                //there is a piece there
                if(board.getPiece(new ChessPosition(myPosition.getRow(), myPosition.getColumn()+1)).getTeamColor() != piece.getTeamColor()){
                    // can take the piece
                    moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow(), myPosition.getColumn()+1), null));
                }

            }else {
                //no peice there
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow(), myPosition.getColumn() + 1), null));
            }
        }
        //down right
        if(myPosition.getRow() > 0 && myPosition.getColumn() < 8){
            if(board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn()+1)) != null){
                //there is a piece there
                if(board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn()+1)).getTeamColor() != piece.getTeamColor()){
                    // can take the piece
                    moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn()+1), null));
                }

            }else {
                //no peice there
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1), null));
            }
            }
        //down
        if(myPosition.getRow() > 0){
            if(board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn())) != null){
                //there is a piece there
                if(board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn())).getTeamColor() != piece.getTeamColor()){
                    // can take the piece
                    moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn()), null));
                }

            }else {
                //no peice there
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn()), null));
            }
        }
        //down left
        if(myPosition.getRow() > 0 && myPosition.getColumn() > 0){
            if(board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn()-1)) != null){
                //there is a piece there
                if(board.getPiece(new ChessPosition(myPosition.getRow() -1, myPosition.getColumn()-1)).getTeamColor() != piece.getTeamColor()){
                    // can take the piece
                    moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn()-1), null));
                }

            }else {
                //no peice there
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1), null));
            }
        }
        //left
        if(myPosition.getColumn() > 0){
            if(board.getPiece(new ChessPosition(myPosition.getRow(), myPosition.getColumn()-1)) != null){
                //there is a piece there
                if(board.getPiece(new ChessPosition(myPosition.getRow(), myPosition.getColumn()-1)).getTeamColor() != piece.getTeamColor()){
                    // can take the piece
                    moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow(), myPosition.getColumn()-1), null));
                }

            }else {
                //no peice there
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow(), myPosition.getColumn() - 1), null));
            }
        }
        //left up
        if(myPosition.getRow() < 8 && myPosition.getColumn() > 0){
            if(board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn()-1)) != null){
                //there is a piece there
                if(board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn()-1)).getTeamColor() != piece.getTeamColor()){
                    // can take the piece
                    moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn()-1), null));
                }

            }else {
                //no peice there
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1), null));
            }
        }

        return moves;
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
