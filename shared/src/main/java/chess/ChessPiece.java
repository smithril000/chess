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

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    @Override
    public String toString() {
        return String.format("[%s]", type);
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
        List<ChessMove> moves = new ArrayList<>();
        ChessPiece piece = board.getPiece(myPosition);
        if(piece.getPieceType() == PieceType.BISHOP){
            moves.addAll(allMove(board, myPosition, piece, 1,1,true));
            moves.addAll(allMove(board, myPosition, piece, 1,-1,true));
            moves.addAll(allMove(board, myPosition, piece, -1,1,true));
            moves.addAll(allMove(board, myPosition, piece, -1,-1,true));

        }else if(piece.getPieceType() == PieceType.KNIGHT){
            moves.addAll(allMove(board, myPosition, piece, 2,1,false));
            moves.addAll(allMove(board, myPosition, piece, 2,-1,false));
            moves.addAll(allMove(board, myPosition, piece, 1,2,false));
            moves.addAll(allMove(board, myPosition, piece, -1,2,false));
            moves.addAll(allMove(board, myPosition, piece, -2,1,false));
            moves.addAll(allMove(board, myPosition, piece, -2,-1,false));
            moves.addAll(allMove(board, myPosition, piece, 1,-2,false));
            moves.addAll(allMove(board, myPosition, piece, -1,-2,false));

        }else if(piece.getPieceType() == PieceType.KING){
            moves.addAll(allMove(board, myPosition, piece, 1,1,false));
            moves.addAll(allMove(board, myPosition, piece, -1,1,false));
            moves.addAll(allMove(board, myPosition, piece, 1,-1,false));
            moves.addAll(allMove(board, myPosition, piece, -1,-1,false));
            moves.addAll(allMove(board, myPosition, piece, 1,0,false));
            moves.addAll(allMove(board, myPosition, piece, 0,1,false));
            moves.addAll(allMove(board, myPosition, piece, -1,0,false));
            moves.addAll(allMove(board, myPosition, piece, 0,-1,false));
        }else if(piece.getPieceType() == PieceType.QUEEN){
            moves.addAll(allMove(board, myPosition, piece, 1,1,true));
            moves.addAll(allMove(board, myPosition, piece, 1,-1,true));
            moves.addAll(allMove(board, myPosition, piece, -1,1,true));
            moves.addAll(allMove(board, myPosition, piece, -1,-1,true));

            moves.addAll(allMove(board, myPosition, piece, 1,0,true));
            moves.addAll(allMove(board, myPosition, piece, 0,1,true));
            moves.addAll(allMove(board, myPosition, piece, -1,0,true));
            moves.addAll(allMove(board, myPosition, piece, 0,-1,true));
        }else if(piece.getPieceType() == PieceType.ROOK){
            moves.addAll(allMove(board, myPosition, piece, 1,0,true));
            moves.addAll(allMove(board, myPosition, piece, 0,1,true));
            moves.addAll(allMove(board, myPosition, piece, -1,0,true));
            moves.addAll(allMove(board, myPosition, piece, 0,-1,true));
        }else if(piece.getPieceType() == PieceType.PAWN){
            moves.addAll(pawnMove(board,myPosition, piece));
        }
        return moves;
    }
    public Collection<ChessMove> allMove(ChessBoard board, ChessPosition myPosition, ChessPiece piece, int rowMove, int colMove, boolean repeat){
        List<ChessMove> moves = new ArrayList<>();
        int x = myPosition.getColumn();
        int y = myPosition.getRow();
        while(x + colMove <= 8 && x + colMove >=1 && y + rowMove <=8 && y + rowMove >=1){
            //we are for sure in bounds
            //check to see if not blocked
            if(board.getPiece(new ChessPosition(y + rowMove, x + colMove)) == null){
                //nothing here can move no problem
                moves.add(new ChessMove(myPosition, new ChessPosition(y + rowMove, x + colMove), null));
                if(!repeat){
                    x = 100;
                }
            }else{
                //piece in the way
                if(board.getPiece(new ChessPosition(y + rowMove, x + colMove)).getTeamColor() != piece.getTeamColor()){
                    //we can take the piece - movement stops
                    moves.add(new ChessMove(myPosition, new ChessPosition(y + rowMove, x + colMove), null));
                    x = 100;
                }else{
                    //cant move
                    x = 100;
                }
            }
            x += colMove;
            y += rowMove;
        }
        return moves;
    }
    public Collection<ChessMove> pawnMove(ChessBoard board, ChessPosition myPosition, ChessPiece piece){
        List<ChessMove> moves = new ArrayList<>();
        // white first
        if(piece.getTeamColor() == ChessGame.TeamColor.WHITE){
            //make sure we will be in bounds
            if(myPosition.getRow() < 8 ){
                //normal unblocked foward move
                if(board.getPiece(new ChessPosition(myPosition.getRow()+1, myPosition.getColumn())) == null){
                    //nothing there so move
                    //check if we will be promoting
                    if(myPosition.getRow() == 7){
                        //we are promoting
                        moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn()), PieceType.QUEEN));
                        moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn()), PieceType.ROOK));
                        moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn()), PieceType.BISHOP));
                        moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn()), PieceType.KNIGHT));
                    }else {
                        //not promoting
                        moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn()), null));
                    }
                }
                //initial move
                if(myPosition.getRow() == 2 && board.getPiece(new ChessPosition(myPosition.getRow()+2, myPosition.getColumn())) == null && board.getPiece(new ChessPosition(myPosition.getRow()+1, myPosition.getColumn())) == null){
                    moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn()), null));
                }
                //now we see if we can take any pieces
                //check right first
                if(myPosition.getColumn() <8) {
                    if (board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1)) != null){
                        //something is there, check if enemy
                        if(board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1)).getTeamColor() != piece.getTeamColor()){
                            //we can take it
                            //check if we will be promoting
                            if(myPosition.getRow() == 7){
                                //we are promoting
                                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn()+1), PieceType.QUEEN));
                                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn()+1), PieceType.ROOK));
                                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn()+1), PieceType.BISHOP));
                                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn()+1), PieceType.KNIGHT));
                            }else {
                                //not promoting
                                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn()+1), null));
                            }
                        }
                    }
                }
                //check capture left
                if(myPosition.getColumn() >1) {
                    if (board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1)) != null){
                        //something is there, check if enemy
                        if(board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1)).getTeamColor() != piece.getTeamColor()){
                            //we can take it
                            //check if we will be promoting
                            if(myPosition.getRow() == 7){
                                //we are promoting
                                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn()-1), PieceType.QUEEN));
                                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn()-1), PieceType.ROOK));
                                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn()-1), PieceType.BISHOP));
                                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn()-1), PieceType.KNIGHT));
                            }else {
                                //not promoting
                                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn()-1), null));
                            }
                        }
                    }
                }
            }
        }
        //now for black movement
        if(piece.getTeamColor() == ChessGame.TeamColor.BLACK){
            //make sure we will be in bounds
            if(myPosition.getRow() > 1){
                //normal unblocked foward move
                if(board.getPiece(new ChessPosition(myPosition.getRow()-1, myPosition.getColumn())) == null){
                    //nothing there so move
                    //check if we will be promoting
                    if(myPosition.getRow() == 2){
                        //we are promoting
                        moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn()), PieceType.QUEEN));
                        moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn()), PieceType.ROOK));
                        moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn()), PieceType.BISHOP));
                        moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn()), PieceType.KNIGHT));
                    }else {
                        //not promoting
                        moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn()), null));
                    }
                }
                //initial move
                if(myPosition.getRow() == 7 && board.getPiece(new ChessPosition(myPosition.getRow()-2, myPosition.getColumn())) == null && board.getPiece(new ChessPosition(myPosition.getRow()-1, myPosition.getColumn())) == null){
                    moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn()), null));
                }
                //now we see if we can take any pieces
                //check right first
                if(myPosition.getColumn() <8) {
                    if (board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1)) != null){
                        //something is there, check if enemy
                        if(board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1)).getTeamColor() != piece.getTeamColor()){
                            //we can take it
                            //check if we will be promoting
                            if(myPosition.getRow() == 2){
                                //we are promoting
                                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn()+1), PieceType.QUEEN));
                                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn()+1), PieceType.ROOK));
                                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn()+1), PieceType.BISHOP));
                                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn()+1), PieceType.KNIGHT));
                            }else {
                                //not promoting
                                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn()+1), null));
                            }
                        }
                    }
                }
                //check capture left
                if(myPosition.getColumn() >1) {
                    if (board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1)) != null){
                        //something is there, check if enemy
                        if(board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1)).getTeamColor() != piece.getTeamColor()){
                            //we can take it
                            //check if we will be promoting
                            if(myPosition.getRow() == 2){
                                //we are promoting
                                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn()-1), PieceType.QUEEN));
                                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn()-1), PieceType.ROOK));
                                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn()-1), PieceType.BISHOP));
                                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn()-1), PieceType.KNIGHT));
                            }else {
                                //not promoting
                                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn()-1), null));
                            }
                        }
                    }
                }
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
