/**
 *
 * @author mahir
 */

//create a directory in the same directory as the src file, named Pictures and add the necessary png icons

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.ImageIO;
import java.io.*;
import java.util.ArrayList;

class ChessPos{
    int x;
    int y;
    
    ChessPos(int x, int y){
        this.x=x;
        this.y=y;
    }
    
    ChessPos(ChessPos pos){
        x=pos.x;
        y=pos.y;
    }
    boolean equals(ChessPos pos){
        if(x==pos.x && y==pos.y) return true;
        return false;
    }
}

class MoveCommand {
    ChessPos src;
    ChessPos dst;

    MoveCommand(ChessPos src, ChessPos dst){
        this.src=src;
        this.dst=dst;
    }
}

abstract class Piece {
    ChessPos pos;
    char color;
    String file_path;
    ChessBoard board_handle;

    Piece(char color, String file_path, ChessPos pos, ChessBoard board_handle){
        this.color=color;
        this.file_path=file_path;
        this.pos=pos;
        this.board_handle=board_handle;
    }
    void move(ChessPos dst){
        pos=dst;
    };

    abstract ArrayList<ChessPos> get_threatened_pos(ChessBoard board);
    abstract ArrayList<ChessPos> get_moveable_pos(ChessBoard board);
}

class King extends Piece{
    King(char color, String file_path, ChessPos pos, ChessBoard board_handle){
        super(color, file_path, pos, board_handle);
    }
    
    public void move(ChessPos dst){
        pos=dst;
        if(color=='w') board_handle.w_king_pos=pos;
        else board_handle.b_king_pos=pos;
    }
    
    ArrayList<ChessPos> get_threatened_pos(ChessBoard board){
        ArrayList<ChessPos> positions=new ArrayList<ChessPos>();
        ChessPos temp;
        ChessPos increments[]={new ChessPos(1,-1),new ChessPos(1,0),new ChessPos(1,1),new ChessPos(0,1),new ChessPos(0,-1),new ChessPos(-1,-1),new ChessPos(1,-1),new ChessPos(1,-1)};
        for(ChessPos inc : increments){
            temp=board_handle.spot_search_threat(pos, color, inc.x, inc.y, false, false);
            if(temp!=null) positions.add(temp);
        }
        
        return positions;
    }
    
    
    ArrayList<ChessPos> get_moveable_pos(ChessBoard board){
        return get_threatened_pos(board);
    }
    
}
class Queen extends Piece{
    Queen(char color, String file_path, ChessPos pos, ChessBoard board_handle){
        super(color, file_path, pos, board_handle);
    }

    ArrayList<ChessPos> get_threatened_pos(ChessBoard board){
        ArrayList<ChessPos> positions=new ArrayList<ChessPos>();
        ArrayList<ChessPos> temp_positions;
        ChessPos increments[]={new ChessPos(1,-1),new ChessPos(1,0),new ChessPos(1,1),new ChessPos(0,1),new ChessPos(0,-1),new ChessPos(-1,-1),new ChessPos(1,-1),new ChessPos(1,-1)};
        for(ChessPos inc : increments){
            temp_positions=board_handle.beam_search_threat(pos, color, inc.x, inc.y);
            for(ChessPos temp : temp_positions){
                positions.add(temp);
            }
        }

        return positions;
    }

    ArrayList<ChessPos> get_moveable_pos(ChessBoard board){
        return get_threatened_pos(board);
    }
}
class Knight extends Piece{
    Knight(char color, String file_path, ChessPos pos, ChessBoard board_handle){
        super(color, file_path, pos, board_handle);
    }

    ArrayList<ChessPos> get_threatened_pos(ChessBoard board){
        ArrayList<ChessPos> positions=new ArrayList<ChessPos>();
        ChessPos temp;
        ChessPos increments[]={new ChessPos(2,-1),new ChessPos(2,1),new ChessPos(-2,1),new ChessPos(-2,-1),new ChessPos(-1, -2),new ChessPos(-1,2),new ChessPos(1,-2),new ChessPos(1,2)};
        for(ChessPos inc : increments){
            temp=board_handle.spot_search_threat(pos, color, inc.x, inc.y, false, false);
            if(temp!=null) positions.add(temp);
        }

        return positions;
    }

    ArrayList<ChessPos> get_moveable_pos(ChessBoard board){
        return get_threatened_pos(board);
    }
}
class Rook extends Piece{
    Rook(char color, String file_path, ChessPos pos, ChessBoard board_handle){
        super(color, file_path, pos, board_handle);
    }

    ArrayList<ChessPos> get_threatened_pos(ChessBoard board){
        ArrayList<ChessPos> positions=new ArrayList<ChessPos>();
        ArrayList<ChessPos> temp_positions;
        ChessPos increments[]={new ChessPos(1,0),new ChessPos(-1,0),new ChessPos(0,1),new ChessPos(0,-1)};
        for(ChessPos inc : increments){
            temp_positions=board_handle.beam_search_threat(pos, color, inc.x, inc.y);
            for(ChessPos temp : temp_positions){
                positions.add(temp);
            }
        }

        return positions;
    }

    ArrayList<ChessPos> get_moveable_pos(ChessBoard board){
        return get_threatened_pos(board);
    }
}
class Bishop extends Piece{
    Bishop(char color, String file_path, ChessPos pos, ChessBoard board_handle){
        super(color, file_path, pos, board_handle);
    }

    ArrayList<ChessPos> get_threatened_pos(ChessBoard board){
        ArrayList<ChessPos> positions=new ArrayList<ChessPos>();
        ArrayList<ChessPos> temp_positions;
        ChessPos increments[]={new ChessPos(1,-1),new ChessPos(1,1),new ChessPos(-1,1),new ChessPos(-1,-1)};
        for(ChessPos inc : increments){
            temp_positions=board_handle.beam_search_threat(pos, color, inc.x, inc.y);
            for(ChessPos temp : temp_positions){
                positions.add(temp);
            }
        }

        return positions;
    }

    ArrayList<ChessPos> get_moveable_pos(ChessBoard board){
        return get_threatened_pos(board);
    }
}
class Pawn extends Piece{
    boolean moved;
    
    ArrayList<ChessPos> positions;
    Pawn(char color, String file_path, ChessPos pos, ChessBoard board_handle){
        super(color, file_path, pos, board_handle);
        moved=false;
    }

    public void move(ChessPos dst){
        pos=dst;
        moved=true;
    }
    ArrayList<ChessPos> get_threatened_pos(ChessBoard board){
        positions=new ArrayList<ChessPos>();
        ChessPos temp;
        ChessPos increments[]={new ChessPos(1,1), new ChessPos(-1,1)};

        for(ChessPos inc : increments){
            if(color=='w') inc.y= -inc.y;
            System.out.println("Increment is:"+inc.x+","+inc.y);
            
            temp=board_handle.spot_search_threat(pos, color, inc.x, inc.y, false, false);
            if(temp==null)System.out.println("Null");
            if(temp!=null) positions.add(temp);
        }
        System.out.println("InsidePawn:");
		System.out.println(positions);
		System.out.println("InsidePawn:");
        return positions;
    }

    ArrayList<ChessPos> get_moveable_pos(ChessBoard board){
        positions=new ArrayList<ChessPos>();
        ArrayList<ChessPos> increments=new ArrayList<ChessPos>();
        ChessPos temp;
        if(moved) increments.add(new ChessPos(0,1));
        else{
            increments.add(new ChessPos(0,1));
            increments.add(new ChessPos(0,2));
        }

        for(ChessPos inc: increments){
            if(color=='w') inc.y=-inc.y;
            temp=board_handle.spot_search_threat(pos, color, inc.x, inc.y, false, true);
            if(temp!=null) positions.add(temp);
        }

        increments=new ArrayList<ChessPos>();
        for(ChessPos inc: increments){
            if(color=='w') inc.y=-inc.y;
            temp=board_handle.spot_search_threat(pos, color, inc.x, inc.y, true, false);
            if(temp!=null) positions.add(temp);
        }
		System.out.println("InsidePawn:");
		System.out.println(positions);
		System.out.println("InsidePawn:");
        return positions;
    }


}
class ChessBoard {
    ArrayList<Piece> pieceList;
    ChessPos w_king_pos, b_king_pos;

    
    ChessBoard(){
        //constructor list of king, queen ... pawn
        pieceList=new ArrayList<Piece>();
        pieceList.add(new King('b', "./Pictures/bking.png", new ChessPos(5,1), this));
        pieceList.add(new Queen('b', "./Pictures/bqueen.png", new ChessPos(4,1), this));
        pieceList.add(new Knight('b', "./Pictures/bknight.png", new ChessPos(2,1), this));
        pieceList.add(new Rook('b', "./Pictures/brook.png", new ChessPos(1,1), this));
        pieceList.add(new Bishop('b', "./Pictures/bbishop.png", new ChessPos(3,1), this));
        pieceList.add(new Knight('b', "./Pictures/bknight.png", new ChessPos(7,1), this));
        pieceList.add(new Rook('b', "./Pictures/brook.png", new ChessPos(8,1), this));
        pieceList.add(new Bishop('b', "./Pictures/bbishop.png", new ChessPos(6,1), this));
        pieceList.add(new Pawn('b', "./Pictures/bpawn.png", new ChessPos(1,2), this));
        pieceList.add(new Pawn('b', "./Pictures/bpawn.png", new ChessPos(2,2), this));
        pieceList.add(new Pawn('b', "./Pictures/bpawn.png", new ChessPos(3,2), this));
        pieceList.add(new Pawn('b', "./Pictures/bpawn.png", new ChessPos(4,2), this));
        pieceList.add(new Pawn('b', "./Pictures/bpawn.png", new ChessPos(5,2), this));
        pieceList.add(new Pawn('b', "./Pictures/bpawn.png", new ChessPos(6,2), this));
        pieceList.add(new Pawn('b', "./Pictures/bpawn.png", new ChessPos(7,2), this));
        pieceList.add(new Pawn('b', "./Pictures/bpawn.png", new ChessPos(8,2), this));
        
        pieceList.add(new King('w', "./Pictures/wking.png", new ChessPos(4,8), this));
        pieceList.add(new Queen('w', "./Pictures/wqueen.png", new ChessPos(5,8), this));
        pieceList.add(new Knight('w', "./Pictures/wknight.png", new ChessPos(2,8), this));
        pieceList.add(new Rook('w', "./Pictures/wrook.png", new ChessPos(1,8), this));
        pieceList.add(new Bishop('w', "./Pictures/wbishop.png", new ChessPos(3,8), this));
        pieceList.add(new Knight('w', "./Pictures/wknight.png", new ChessPos(7,8), this));
        pieceList.add(new Rook('w', "./Pictures/wrook.png", new ChessPos(8,8), this));
        pieceList.add(new Bishop('w', "./Pictures/wbishop.png", new ChessPos(6,8), this));
        pieceList.add(new Pawn('w', "./Pictures/wpawn.png", new ChessPos(1,7), this));
        pieceList.add(new Pawn('w', "./Pictures/wpawn.png", new ChessPos(2,7), this));
        pieceList.add(new Pawn('w', "./Pictures/wpawn.png", new ChessPos(3,7), this));
        pieceList.add(new Pawn('w', "./Pictures/wpawn.png", new ChessPos(4,7), this));
        pieceList.add(new Pawn('w', "./Pictures/wpawn.png", new ChessPos(5,7), this));
        pieceList.add(new Pawn('w', "./Pictures/wpawn.png", new ChessPos(6,7), this));
        pieceList.add(new Pawn('w', "./Pictures/wpawn.png", new ChessPos(7,7), this));
        pieceList.add(new Pawn('w', "./Pictures/wpawn.png", new ChessPos(8,7), this));
        
        //constructor list end
        
        b_king_pos=new ChessPos(4, 1);
        w_king_pos=new ChessPos(5, 8);
    }
    
    public ChessBoard copy_board(){
        ChessBoard newBoard= new ChessBoard();
        for(Piece piece : this.pieceList){
        	newBoard.pieceList.add(piece);
        }
        newBoard.w_king_pos= new ChessPos(w_king_pos);
        newBoard.b_king_pos= new ChessPos(b_king_pos);
        return newBoard;
        }
    
    public Piece get_piece(ChessPos pos){
        for(Piece piece : pieceList){
            if(piece.pos.equals(pos)) return piece;
        }
        return null;
    }
    
    public ArrayList<ChessPos> beam_search_threat(ChessPos start_pos, char own_color, int inc_x, int inc_y){
    	Piece piece;
        ArrayList<ChessPos> threatList= new ArrayList<ChessPos>();
        int x= start_pos.x+inc_x;
        int y= start_pos.y+inc_y;
        while(x>=1 && y>=1 && x<=8 && y<=8){
            piece=get_piece(new ChessPos(x, y));
            if(piece != null){
                if(piece.color!=own_color) threatList.add(piece.pos);
                break;
            }
            threatList.add(piece.pos);
            x+=inc_x;
            y+=inc_y;
        }
        System.out.println(threatList);
        return threatList;
    }
    
    public ChessPos spot_search_threat(ChessPos start_pos, char own_color, int inc_x, int inc_y, boolean threat_only, boolean free_only){
        int x=start_pos.x+=inc_x;
        int y=start_pos.y+=inc_y;
        ChessPos pos;
        Piece piece;
        
        if(x>=1 && y>=1 && x<=8 && y<=8){
        	
        	pos= new ChessPos(x,y);
            piece=get_piece(pos);
            
            if(piece!=null){
                if(free_only){
                    return null;
                }
                if(piece.color!=own_color){
                	return piece.pos;
                }
                else return null;
            }
            if(!threat_only){
             return pos;
            }
            else return null;
        }
        return null;
    }
    
    public void execute_move(MoveCommand move){
        Piece source_piece=get_piece(move.src);
        int ix=0;
        for(Piece piece : pieceList){
            if(piece.pos.equals(move.dst)){
                pieceList.remove(ix);
                break;
            }
            ix++;
        }
        source_piece.move(move.dst);
    }
}






class ChessGame {
    char turn;
    ChessBoard board;
    
    ChessGame(){
        board=new ChessBoard();
        turn='w';
    }
    
    void play(MoveCommand input_move){
		System.out.println("Move input");
        if(!try_move(input_move)){
			System.out.println("Invalid Move");
			return;
		}
		System.out.println("Executing Move");
        board.execute_move(input_move);
        if(turn=='w') turn='b';
        else turn='w';
    }
    
    boolean try_move(MoveCommand move){
        ChessBoard board_copy= board.copy_board();
        ArrayList<ChessPos> pos_list;
        Piece src_piece= board_copy.get_piece(move.src);
        if(src_piece==null || src_piece.color!=turn) return false;
        
        boolean foundFlag=false;
        pos_list=src_piece.get_threatened_pos(board_copy);
        
        for(ChessPos pos : pos_list){
            if(pos.equals(move.dst)) foundFlag=true; 
        }
        pos_list=src_piece.get_moveable_pos(board_copy);
  
        for(ChessPos pos : pos_list){
            if(pos.equals(move.dst)) foundFlag=true; 
        }
        
        if(!foundFlag) return false;
        board_copy.execute_move(move);
        if(turn=='w'){
            for(Piece piece : board_copy.pieceList){
                if(piece.color==turn) continue;
                pos_list=piece.get_threatened_pos(board_copy);
                for(ChessPos pos: pos_list){
                    if(pos.equals(board_copy.w_king_pos)) return false;
                }
            }
        }
        if(turn=='b'){
            for(Piece piece : board_copy.pieceList){
                if(piece.color==turn) continue;
                pos_list=piece.get_threatened_pos(board_copy);
                for(ChessPos pos: pos_list){
                    if(pos.equals(board_copy.b_king_pos)) return false;
                }
            }
        }
        
        return true;
    }
    
    
   
}

public class AppWindow extends Frame{

    Color color_matrix[][];
    ChessGame game;


    AppWindow(){
        color_matrix=new Color[8][8];
        for(int i=0; i<8; i++){
            for(int j=0; j<8; j++){
                if ((i % 2 == 0) == (j % 2 == 0))
                    color_matrix[i][j]=Color.BLUE;
                else
                    color_matrix[i][j]=Color.CYAN;
            }
        }
        
        game=new ChessGame();
        addWindowListener(new MyWindowAdapter());
        addMouseListener(new MyMouseAdapter(this));

    }

    @Override
    public void paint(Graphics g){
        BufferedImage img;

        //paint board
        for(int row=0; row<8; row++){
            for(int col=0; col<8; col++){
                g.setColor(color_matrix[row][col]);
                g.fillRect(50+50*row, 50+50*col, 50, 50);
            }
        }

        //paint pieces
        for(Piece piece : game.board.pieceList){
            try{
                img=ImageIO.read(new File(piece.file_path));
                //determine coordinates
                int x=(piece.pos.x-1)*50 + 50;
                int y=(piece.pos.y-1)*50 + 50;
                //display image in coordinates
                g.drawImage(img, x, y, this);


            }catch(IOException e){
                System.out.println("Image not found");
            }
        }
    }

    public static void main(String args[]){
        AppWindow app_win= new AppWindow();
        app_win.setSize(500, 500);
        app_win.setTitle("Mahir's Chess Board");
        app_win.setVisible(true);
    }

}


class MyMouseAdapter extends MouseAdapter{
    AppWindow app_win;
    int row, col;
    int lower=50;
    int higher=450;
    int x, y;
    ChessPos start_pos;
    ChessPos end_pos;
    int click_counter;

    MyMouseAdapter(AppWindow app_win){
        this.app_win=app_win;
        click_counter=0;
    }

    public void mousePressed(MouseEvent me){
        x=me.getX();
        y=me.getY();
        if(x>=lower && x<=higher){
            //determine row
            col=(x-50)/50;
            row=(y-50)/50;
			System.out.println((col+1)+","+(row+1)+" selected");
            app_win.color_matrix[col][row]=Color.RED;
            click_counter++;
            app_win.repaint();

            if(click_counter==1) start_pos= new ChessPos(col+1, row+1);
            else if(click_counter==2){
                end_pos=new ChessPos(col+1, row+1);
                click_counter=0;
                for(int i=0; i<8; i++){
                    for(int j=0; j<8; j++){
                        if ((i % 2 == 0) == (j % 2 == 0))
                            app_win.color_matrix[i][j]=Color.BLUE;
                        else
                             app_win.color_matrix[i][j]=Color.CYAN;
                    }
                }
                app_win.game.play(new MoveCommand(start_pos, end_pos));
            }
        }
    }


}


class MyWindowAdapter extends WindowAdapter {
    public void windowClosing(WindowEvent we){
        System.exit(0);
    }
}

