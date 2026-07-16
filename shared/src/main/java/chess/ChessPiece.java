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
        }else if (piece.getPieceType() == PieceType.ROOK){
            moves = rookMoves(board, myPosition, piece, moves);
        }else if(piece.getPieceType() == PieceType.BISHOP){
            moves = bishopMoves(board, myPosition, piece, moves);
        }else if(piece.getPieceType() == PieceType.QUEEN){
            rookMoves(board, myPosition, piece, moves);
            bishopMoves(board, myPosition, piece, moves);
        }else if(piece.getPieceType() == PieceType.KNIGHT){
            knightMoves(board, myPosition, piece, moves);
        }else if(piece.getPieceType() == PieceType.KING){
            kingMoves(board, myPosition, piece, moves);
        }
        return moves;
    }
    private List<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece, List<ChessMove> moves){
        //for pawns they can move 2 foward at the start, one if nothing in front, and diagonal if taking

        int row = myPosition.getRow()+1;
        int col = myPosition.getColumn()+1;
        //first check if we are at starting position for black and white
        if(row == 2 && piece.getTeamColor() == ChessGame.TeamColor.WHITE){
            //now check if the area is unblocked
            if(board.getPiece(new ChessPosition(row+1, col)) == null) {
                //moves.add(new ChessMove(myPosition, new ChessPosition(row + 1, col), null));
                if(board.getPiece(new ChessPosition(row+2, col)) == null){
                    moves.add(new ChessMove(myPosition, new ChessPosition(row + 2, col), null));
                }
            }

        }
        if(row == 7 && piece.getTeamColor() == ChessGame.TeamColor.BLACK){
            //now check if the area is unblocked
            if(board.getPiece(new ChessPosition(row-1, col)) == null) {
                //moves.add(new ChessMove(myPosition, new ChessPosition(row + 1, col), null));
                if(board.getPiece(new ChessPosition(row-2, col)) == null){
                    moves.add(new ChessMove(myPosition, new ChessPosition(row - 2, col), null));
                }
            }

        }
        //now check if we can move foward or back, not hitting edge of board
        int row_dir;
        if(piece.getTeamColor() == ChessGame.TeamColor.WHITE){
            row_dir = 1;
        }else{
            row_dir = -1;
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
            if(moveOrTake(pos, board, piece.getTeamColor()).equals("true") || moveOrTake(pos, board, piece.getTeamColor()).equals("take")){
                //our move or take func doesn't really work with pawns, so we need to check manually if they can take
                //this is if there is nothing in front of them
                if(col - 1 == pos.getColumn() && board.getPiece(pos) == null ){
                    //we need to check if we are promoting
                    moveHelper(myPosition, moves, pos);
                }else if(board.getPiece(pos)!= null && board.getPiece(pos).getTeamColor() != piece.getTeamColor() && col-1 != pos.getColumn()){
                    //we MUST take and check if we promote
                    moveHelper(myPosition, moves, pos);
                }

            }
            start++;
        }


        return moves;
    }

    private void moveHelper(ChessPosition myPosition, List<ChessMove> moves, ChessPosition pos) {
        if(pos.getRow() == 7 || pos.getRow() == 0) {
            moves.add(new ChessMove(myPosition, pos, PieceType.QUEEN));
            moves.add(new ChessMove(myPosition, pos, PieceType.BISHOP));
            moves.add(new ChessMove(myPosition, pos, PieceType.ROOK));
            moves.add(new ChessMove(myPosition, pos, PieceType.KNIGHT));
        }else {
            moves.add(new ChessMove(myPosition, pos, null));
        }
    }

    private List<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece, List<ChessMove> moves){
        //I want to use my move or take function
        //lets start with moving up - shouldn't matter the color
        loopHelper(-1, 0, myPosition, board, piece.getTeamColor(), moves, true);
        //now down
        loopHelper(1, 0, myPosition, board, piece.getTeamColor(), moves, true);
        //left
        loopHelper(0, -1, myPosition, board, piece.getTeamColor(), moves, true);
        //right
        loopHelper(0, 1, myPosition, board, piece.getTeamColor(), moves, true);
        return moves;


    }
    private List<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece, List<ChessMove> moves){
        //up-left
        loopHelper(-1,-1, myPosition, board, piece.getTeamColor(), moves, true);
        //up-right
        loopHelper(-1,1,myPosition, board, piece.getTeamColor(), moves, true);
        //down-right
        loopHelper(1,1,myPosition, board, piece.getTeamColor(), moves, true);
        //downleft
        loopHelper(1,-1,myPosition, board, piece.getTeamColor(), moves, true);

        return moves;
    }
    private void knightMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece, List<ChessMove> moves){
        //left2 up
        loopHelper(-1,-2, myPosition, board, piece.getTeamColor(), moves, false);
        //left2 down
        loopHelper(1,-2, myPosition, board, piece.getTeamColor(), moves, false);
        //left1up2
        loopHelper(-2,-1, myPosition, board, piece.getTeamColor(), moves, false);
        //left1down2
        loopHelper(2,-1, myPosition, board, piece.getTeamColor(), moves, false);
        //right2 up
        loopHelper(-1,2, myPosition, board, piece.getTeamColor(), moves, false);
        //right2 down1
        loopHelper(1,2, myPosition, board, piece.getTeamColor(), moves, false);
        //right1 up2
        loopHelper(-2,1, myPosition, board, piece.getTeamColor(), moves, false);
        //right1 down2
        loopHelper(2,1, myPosition, board, piece.getTeamColor(), moves, false);
    }
    private void kingMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece, List<ChessMove> moves){
        //up-left2
        loopHelper(-1,-1, myPosition, board, piece.getTeamColor(), moves, false);
        //up-right
        loopHelper(-1,1,myPosition, board, piece.getTeamColor(), moves, false);
        //down-right
        loopHelper(1,1,myPosition, board, piece.getTeamColor(), moves, false);
        //downleft
        loopHelper(1,-1,myPosition, board, piece.getTeamColor(), moves, false);

        //sides
        loopHelper(-1, 0, myPosition, board, piece.getTeamColor(), moves, false);
        //now down
        loopHelper(1, 0, myPosition, board, piece.getTeamColor(), moves, false);
        //left
        loopHelper(0, -1, myPosition, board, piece.getTeamColor(), moves, false);
        //right
        loopHelper(0, 1, myPosition, board, piece.getTeamColor(), moves, false);
    }


    private void loopHelper(int rowDir, int colDir, ChessPosition pos, ChessBoard board, ChessGame.TeamColor color, List<ChessMove> moves, boolean repeat){
        int row = pos.getRow()+1;
        int col = pos.getColumn()+1;
        while(row >= 1 && row <= 8 && col >= 1 && col <= 8){
            if(row + rowDir <= 0){
                return;
            }else if(row + rowDir > 8){
                return;
            }
            if(col +colDir <= 0){
                return;
            }else if(col + colDir > 8){
                return;
            }
            ChessPosition newPos = new ChessPosition(row + rowDir, col + colDir);
            String check = moveOrTake(newPos, board, color);
            if(check.equals("true")){
                //add move
                moves.add(new ChessMove(pos, newPos, null));
                row = row + rowDir;
                col = col + colDir;
            }else if(check.equals("take")){
                //add move
                moves.add(new ChessMove(pos, newPos, null));
                row = -1;
            }else{
                return;
            }
            if(!repeat){
                row = -1;
            }
        }
        return;
    }
    private String moveOrTake(ChessPosition pos, ChessBoard board, ChessGame.TeamColor color){
        if(board.getPiece(pos) == null){
            return "true";
        }else if(board.getPiece(pos).getTeamColor() != color){
            return "take";
        }else{
            return "false";
        }
    }

}
