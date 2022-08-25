/**
 *
 * @author mahir
 */

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
        this.src= new ChessPos(src);
        this.dst= new ChessPos(dst);
    }
}


abstract class Piece {
    ChessPos pos;
    char color;
    String file_path;
    ChessBoard board_handle; //used for king
    boolean moved; //used for king rook pawn
	
    Piece(char color, String file_path, ChessPos pos){
        this.color=color;
        this.file_path=file_path;
        this.pos=pos;
        moved=false;
    }
    
    /*Piece(Piece piece){
    	this.color=piece.color;
    	this.pos=new ChessPos(piece.pos);
    	this.file_path=piece.file_path;
    }*/
    
    void move(ChessPos dst){
        pos=new ChessPos(dst);
        moved=true;
    };
    
    ChessPos get_pos(){
    	return new ChessPos(pos);
    }
    
    abstract ArrayList<ChessPos> get_moveable_pos(ChessBoard board);
    abstract ArrayList<ChessPos> get_threatened_pos(ChessBoard board); //for pawns only
}


class King extends Piece{   
    
    King(char color, String file_path, ChessPos pos, ChessBoard board_handle){ //for starting game
        super(color, file_path, pos);
        this.board_handle=board_handle; 
        this.moved=false;
    }
    
    King(char color, String file_path, ChessPos pos, ChessBoard board_handle, boolean moved){ //for copying pieces
        super(color, file_path, pos);
        this.board_handle=board_handle; 
        this.moved=moved;
    }
    
    @Override
    void move(ChessPos dst){
        pos=dst;
        board_handle.set_king_pos(dst, color);
        moved=true;
    }
    
    @Override
    ArrayList<ChessPos> get_moveable_pos(ChessBoard board){
        ArrayList<ChessPos> positions=new ArrayList<ChessPos>();
        ChessPos temp;
        ChessPos increments[]={new ChessPos(1,0),new ChessPos(1,1),new ChessPos(1,-1),new ChessPos(0,1),new ChessPos(0,-1),new ChessPos(-1,0),new ChessPos(-1,1),new ChessPos(-1,-1)};
        
        for(ChessPos inc : increments){
            temp=board.spot_search(get_pos(), color, inc.x, inc.y, false, false);
            if(temp!=null) positions.add(temp);
        }
        
        return positions;
    }
    
    @Override
    ArrayList<ChessPos> get_threatened_pos(ChessBoard board){
        return  new ArrayList<ChessPos>();
    };   
}


class Queen extends Piece{
    Queen(char color, String file_path, ChessPos pos){
        super(color, file_path, pos);
    }
    
    Queen(Queen queen){
        super(queen.color, queen.file_path, queen.get_pos());       
    }
    
    @Override
    ArrayList<ChessPos> get_moveable_pos(ChessBoard board){
        ArrayList<ChessPos> positions=new ArrayList<ChessPos>();
        ArrayList<ChessPos> temp_positions;
        ChessPos increments[]={new ChessPos(1,0),new ChessPos(1,1),new ChessPos(1,-1),new ChessPos(0,1),new ChessPos(0,-1),new ChessPos(-1,0),new ChessPos(-1,1),new ChessPos(-1,-1)};
        for(ChessPos inc : increments){
            positions.addAll(board.beam_search(get_pos(), color, inc.x, inc.y));
        }

        return positions;
    }
    
    @Override
    ArrayList<ChessPos> get_threatened_pos(ChessBoard board){
        return  new ArrayList<ChessPos>();
    };
}


class Knight extends Piece{
    Knight(char color, String file_path, ChessPos pos){
        super(color, file_path, pos);
    }

    Knight(Knight knight){
        super(knight.color, knight.file_path, knight.get_pos());   
    }
    @Override
    ArrayList<ChessPos> get_moveable_pos(ChessBoard board){
        ArrayList<ChessPos> positions=new ArrayList<ChessPos>();
        ChessPos temp;
        ChessPos increments[]={new ChessPos(2,1),new ChessPos(2,-1),new ChessPos(-2,1),new ChessPos(-2,-1),new ChessPos(1, 2),new ChessPos(1,-2),new ChessPos(-1,2),new ChessPos(-1,-2)};
        
        for(ChessPos inc : increments){
            temp=board.spot_search(get_pos(), color, inc.x, inc.y, false, false);
            if(temp!=null) positions.add(temp);
        }

        return positions;
    }
    
    @Override
    ArrayList<ChessPos> get_threatened_pos(ChessBoard board){
        return  new ArrayList<ChessPos>();
    };

}



class Rook extends Piece{
    Rook(char color, String file_path, ChessPos pos, boolean moved){
        super(color, file_path, pos);
        this.moved=moved;
    }
    Rook(char color, String file_path, ChessPos pos){
        super(color, file_path, pos);
    }
    Rook(Rook rook){
        super(rook.color, rook.file_path, rook.get_pos());     
    }

    @Override
    ArrayList<ChessPos> get_moveable_pos(ChessBoard board){
        ArrayList<ChessPos> positions=new ArrayList<ChessPos>();
        ArrayList<ChessPos> temp_positions;
        ChessPos increments[]={new ChessPos(1,0),new ChessPos(-1,0),new ChessPos(0,1),new ChessPos(0,-1)};
        for(ChessPos inc : increments){
            positions.addAll(board.beam_search(get_pos(), color, inc.x, inc.y));
        }

        return positions;
    }
    
    @Override
    ArrayList<ChessPos> get_threatened_pos(ChessBoard board){
        return  new ArrayList<ChessPos>();
    };

}



class Bishop extends Piece{
    Bishop(char color, String file_path, ChessPos pos){
        super(color, file_path, pos);
    }

    Bishop(Bishop bishop){
        super(bishop.color, bishop.file_path, bishop.get_pos());       
    }
    
    @Override
    ArrayList<ChessPos> get_moveable_pos(ChessBoard board){
        ArrayList<ChessPos> positions=new ArrayList<ChessPos>();
        ArrayList<ChessPos> temp_positions;
        ChessPos increments[]={new ChessPos(1,1),new ChessPos(1,-1),new ChessPos(-1,1),new ChessPos(-1,-1)};
        for(ChessPos inc : increments){
            positions.addAll(board.beam_search(get_pos(), color, inc.x, inc.y));            
        }

        return positions;
    }
    
    @Override
    ArrayList<ChessPos> get_threatened_pos(ChessBoard board){
        return  new ArrayList<ChessPos>();
    };
}



class Pawn extends Piece{
    
    Pawn(char color, String file_path, ChessPos pos){
        super(color, file_path, pos);
    }
    
    Pawn(char color, String file_path, ChessPos pos, boolean moved){
        super(color, file_path, pos);
        this.moved=moved;
    }
    
    Pawn(Pawn pawn){
        super(pawn.color, pawn.file_path, pawn.get_pos());
        this.moved=pawn.moved;        
    }
    
    @Override
    ArrayList<ChessPos> get_threatened_pos(ChessBoard board){
        ArrayList<ChessPos> positions=new ArrayList<ChessPos>();
        ChessPos temp;
        ArrayList<ChessPos> threat_increments= new ArrayList<ChessPos>();
        
        threat_increments.add(new ChessPos(1,1));
        threat_increments.add(new ChessPos(-1,1));
		
        for(ChessPos inc: threat_increments){
            if(color=='w') inc.y=-inc.y;
            temp=board.spot_search(get_pos(), color, inc.x, inc.y, true, false);
            if(temp!=null) positions.add(temp);
        }
        return positions;
    }

    @Override
    ArrayList<ChessPos> get_moveable_pos(ChessBoard board){
        ArrayList<ChessPos> positions=new ArrayList<ChessPos>();
        ArrayList<ChessPos> free_increments=new ArrayList<ChessPos>();
        ArrayList<ChessPos> threat_increments= new ArrayList<ChessPos>();
        ChessPos temp;
        
        free_increments.add(new ChessPos(0,1));
        if(!moved) free_increments.add(new ChessPos(0,2));

        for(ChessPos inc: free_increments){
            if(color=='w') inc.y=-inc.y;
            temp=board.spot_search(get_pos(), color, inc.x, inc.y, false, true);
            if(temp!=null) positions.add(temp);
        }
        
        positions.addAll(get_threatened_pos(board));
 
        return positions;
    }
}



class ChessBoard {
    ArrayList<Piece> pieceList;
    ChessPos w_king_pos, b_king_pos;

    
    ChessBoard(){
        pieceList=new ArrayList<Piece>();        
        b_king_pos=new ChessPos(5, 1);
        w_king_pos=new ChessPos(5, 8);
    }
    
    
    void init(){
    	pieceList.add(new King('b', "./Pictures/bking.png", new ChessPos(5,1), this));
        pieceList.add(new Queen('b', "./Pictures/bqueen.png", new ChessPos(4,1)));
        pieceList.add(new Knight('b', "./Pictures/bknight.png", new ChessPos(2,1)));
        pieceList.add(new Rook('b', "./Pictures/brook.png", new ChessPos(1,1)));
        pieceList.add(new Bishop('b', "./Pictures/bbishop.png", new ChessPos(3,1)));
        pieceList.add(new Knight('b', "./Pictures/bknight.png", new ChessPos(7,1)));
        pieceList.add(new Rook('b', "./Pictures/brook.png", new ChessPos(8,1)));
        pieceList.add(new Bishop('b', "./Pictures/bbishop.png", new ChessPos(6,1)));
        pieceList.add(new Pawn('b', "./Pictures/bpawn.png", new ChessPos(1,2)));
        pieceList.add(new Pawn('b', "./Pictures/bpawn.png", new ChessPos(2,2)));
        pieceList.add(new Pawn('b', "./Pictures/bpawn.png", new ChessPos(3,2)));
        pieceList.add(new Pawn('b', "./Pictures/bpawn.png", new ChessPos(4,2)));
        pieceList.add(new Pawn('b', "./Pictures/bpawn.png", new ChessPos(5,2)));
        pieceList.add(new Pawn('b', "./Pictures/bpawn.png", new ChessPos(6,2)));
        pieceList.add(new Pawn('b', "./Pictures/bpawn.png", new ChessPos(7,2)));
        pieceList.add(new Pawn('b', "./Pictures/bpawn.png", new ChessPos(8,2)));
        
        pieceList.add(new King('w', "./Pictures/wking.png", new ChessPos(5,8), this));
        pieceList.add(new Queen('w', "./Pictures/wqueen.png", new ChessPos(4,8)));
        pieceList.add(new Knight('w', "./Pictures/wknight.png", new ChessPos(2,8)));
        pieceList.add(new Rook('w', "./Pictures/wrook.png", new ChessPos(1,8)));
        pieceList.add(new Bishop('w', "./Pictures/wbishop.png", new ChessPos(3,8)));
        pieceList.add(new Knight('w', "./Pictures/wknight.png", new ChessPos(7,8)));
        pieceList.add(new Rook('w', "./Pictures/wrook.png", new ChessPos(8,8)));
        pieceList.add(new Bishop('w', "./Pictures/wbishop.png", new ChessPos(6,8)));
        pieceList.add(new Pawn('w', "./Pictures/wpawn.png", new ChessPos(1,7)));
        pieceList.add(new Pawn('w', "./Pictures/wpawn.png", new ChessPos(2,7)));
        pieceList.add(new Pawn('w', "./Pictures/wpawn.png", new ChessPos(3,7)));
        pieceList.add(new Pawn('w', "./Pictures/wpawn.png", new ChessPos(4,7)));
        pieceList.add(new Pawn('w', "./Pictures/wpawn.png", new ChessPos(5,7)));
        pieceList.add(new Pawn('w', "./Pictures/wpawn.png", new ChessPos(6,7)));
        pieceList.add(new Pawn('w', "./Pictures/wpawn.png", new ChessPos(7,7)));
        pieceList.add(new Pawn('w', "./Pictures/wpawn.png", new ChessPos(8,7)));
    }
    
    ChessBoard copy_board(){
        ChessBoard newBoard= new ChessBoard();
        newBoard.pieceList=new ArrayList<Piece>();
        
        for(int ix=0; ix<pieceList.size(); ix++){
                if(pieceList.get(ix).file_path=="./Pictures/bking.png" || pieceList.get(ix).file_path=="./Pictures/wking.png"){
                    newBoard.pieceList.add(new King(pieceList.get(ix).color, pieceList.get(ix).file_path, pieceList.get(ix).get_pos(), newBoard, pieceList.get(ix).moved));
                }
                if(pieceList.get(ix).file_path=="./Pictures/bqueen.png" || pieceList.get(ix).file_path=="./Pictures/wqueen.png"){
                    newBoard.pieceList.add(new Queen(pieceList.get(ix).color, pieceList.get(ix).file_path, pieceList.get(ix).get_pos()));
                }               
                if(pieceList.get(ix).file_path=="./Pictures/bknight.png" || pieceList.get(ix).file_path=="./Pictures/wknight.png"){
                    newBoard.pieceList.add(new Knight(pieceList.get(ix).color, pieceList.get(ix).file_path, pieceList.get(ix).get_pos()));
                }               
                if(pieceList.get(ix).file_path=="./Pictures/brook.png" || pieceList.get(ix).file_path=="./Pictures/wrook.png"){
                    newBoard.pieceList.add(new Rook(pieceList.get(ix).color, pieceList.get(ix).file_path, pieceList.get(ix).get_pos(), pieceList.get(ix).moved));
                }               
                if(pieceList.get(ix).file_path=="./Pictures/bbishop.png" || pieceList.get(ix).file_path=="./Pictures/wbishop.png"){
                    newBoard.pieceList.add(new Bishop(pieceList.get(ix).color, pieceList.get(ix).file_path, pieceList.get(ix).get_pos()));
                }
                if(pieceList.get(ix).file_path=="./Pictures/bpawn.png" || pieceList.get(ix).file_path=="./Pictures/wpawn.png"){
                    newBoard.pieceList.add(new Pawn(pieceList.get(ix).color, pieceList.get(ix).file_path, pieceList.get(ix).get_pos(), pieceList.get(ix).moved));
                }
        }
        
        newBoard.w_king_pos= new ChessPos(w_king_pos);
        newBoard.b_king_pos= new ChessPos(b_king_pos);
        return newBoard;
    }
    
    Piece get_piece(ChessPos pos){
        for(Piece piece : pieceList){     	
            if(piece.get_pos().equals(pos)) return piece;  
        }
        return null;
    }
    
    void set_king_pos(ChessPos pos, char color){
        if(color=='w') w_king_pos=new ChessPos(pos);
        else b_king_pos=new ChessPos(pos);
    }
    
    ArrayList<ChessPos> beam_search(ChessPos start_pos, char own_color, int inc_x, int inc_y){
    	Piece piece;
    	ChessPos pos;
        ArrayList<ChessPos> positions= new ArrayList<ChessPos>();
        int x= start_pos.x+inc_x;
        int y= start_pos.y+inc_y;
        while(x>=1 && y>=1 && x<=8 && y<=8){  
            pos=new ChessPos(x, y);
            piece=get_piece(pos);
            if(piece != null){
                if(piece.color!=own_color) positions.add(pos);
                break;
            }
            positions.add(pos);
            x+=inc_x;
            y+=inc_y;
        }
        
        return positions;
    }
    
    
    ChessPos spot_search(ChessPos start_pos, char own_color, int inc_x, int inc_y, boolean threat_only, boolean free_only){
    	Piece piece;
    	ChessPos pos;
        int x=start_pos.x+=inc_x;
        int y=start_pos.y+=inc_y;
        
        if(x>=1 && y>=1 && x<=8 && y<=8){   	
            pos= new ChessPos(x,y);       	
            piece=get_piece(pos);
            if(piece!=null){
                if(free_only){
                    return null;
                }
                if(piece.color!=own_color){
                	return pos;
                }
                else return null;
            }
            if(threat_only){
             return null;
            }
            else return pos;
        }
        return null;
    }
    
    
    
    void execute_move(MoveCommand move){
        
        Piece source_piece=get_piece(move.src);
        
        //check castling move
        Piece dst_piece=get_piece(move.dst);
        if(dst_piece!=null){
            if(source_piece.color==dst_piece.color){
                if(dst_piece.get_pos().x==1){
                    source_piece.move(new ChessPos(3,source_piece.get_pos().y));
                    dst_piece.move(new ChessPos(4, source_piece.get_pos().y));
                }
                else{
                    source_piece.move(new ChessPos(7,source_piece.get_pos().y));
                    dst_piece.move(new ChessPos(6, source_piece.get_pos().y));
                }
                return;
            }
        }
        
        int ix=0;
        for(Piece piece : pieceList){
            if(piece.get_pos().equals(move.dst)){
                pieceList.remove(ix);
                break;
            }
            ix++;
        }
        source_piece.move(move.dst);
    }
    
    boolean check_for_check(ArrayList<ChessPos> check_pos_list, char turn){
            ArrayList<ChessPos> positions =new ArrayList<ChessPos>();
            for(Piece piece : pieceList){
                if(piece.color==turn) continue;
                if(piece.file_path=="./Pictures/wpawn.png" || piece.file_path=="./Pictures/bpawn.png"){
                    positions=piece.get_threatened_pos(this);
                }
                else positions=piece.get_moveable_pos(this);
        
                for(ChessPos pos: positions){
                    for(ChessPos check_pos: check_pos_list){
                        if(pos.equals(check_pos)) return true;
                    }  
                }
            }

            return false;
    }    
}



class ChessGame {
    char turn;
    ChessBoard board;
    AppWindow app_win;
    
    ChessGame(AppWindow app_win){
        board=new ChessBoard();
        board.init();
        turn='w';
        this.app_win= app_win;
    }
    
    void play(MoveCommand input_move){
	System.out.println("Move input");
        if(!try_move(input_move)){
            System.out.println("Invalid Move");
            return;
	}
        System.out.println("Executing Move");
        board.execute_move(input_move);       
        promotion_check();
        if(turn=='w') turn='b';
        else turn='w';
    }
    
    boolean try_move(MoveCommand move){
        ChessBoard board_copy= board.copy_board();
        ArrayList<ChessPos> pos_list;
        Piece src_piece= board_copy.get_piece(new ChessPos(move.src));
        Piece dst_piece= board_copy.get_piece(new ChessPos(move.dst));
        if(src_piece==null || src_piece.color!=turn) return false;
        
        //castling move check
        if(dst_piece!=null){
            if(dst_piece.color==turn){
        	if(src_piece.file_path=="./Pictures/wking.png" || src_piece.file_path=="./Pictures/bking.png"){
                    if(dst_piece.file_path=="./Pictures/wrook.png" || dst_piece.file_path=="./Pictures/brook.png"){
        		if(src_piece.moved==false && dst_piece.moved==false){
                                        
                            //get the king travelling squares
                            ArrayList<ChessPos> king_travel_squares=new ArrayList<ChessPos>();
                            int x=dst_piece.get_pos().x;
                            int y=dst_piece.get_pos().y;
                            king_travel_squares.add(new ChessPos(5,y));
                            Piece temp;
                            if(x==1){
                                //check empty                                              
                                for(int inc=-1; inc>-4; inc--){
                                    temp=board_copy.get_piece(new ChessPos(5+inc, y));
                                    if(temp!=null) return false;                                            
                                }
  
                                        
                                king_travel_squares.add(new ChessPos(4,y));
                                king_travel_squares.add(new ChessPos(3,y));
                            }
        				
                            else{
                                            
                                for(int inc=1; inc<3; inc++){
                                    temp=board_copy.get_piece(new ChessPos(5+inc, y));
                                    if(temp!=null) return false;
                                    }
                                            
                                king_travel_squares.add(new ChessPos(6,y));
                                king_travel_squares.add(new ChessPos(7,y));
                            }
                            
                            return !board_copy.check_for_check(king_travel_squares, turn);
                        }
                    }
                }       	
                return false;
            }
        }
        
        
        
        //normal move check
        boolean foundFlag=false;

        pos_list=src_piece.get_moveable_pos(board_copy);
        
        for(ChessPos pos: pos_list){
            if(pos.equals(new ChessPos(move.dst))) foundFlag=true; 
        }
        if(!foundFlag) return false;
        
        board_copy.execute_move(move);
        
        //check for check
        ArrayList<ChessPos> check_pos_list=new ArrayList<ChessPos>();
        if(turn=='w') check_pos_list.add(board_copy.w_king_pos);
        else check_pos_list.add(board_copy.b_king_pos);
        
        return !board_copy.check_for_check(check_pos_list, turn);
    }
    
    void promotion_check(){
        Piece piece;
        int x, y;
        if(turn=='w') y=1;
        else y=8;
        for(x=1; x<=8; x++){
            piece=board.get_piece(new ChessPos(x, y));
            if(piece==null) continue;
            if(piece.file_path=="./Pictures/wpawn.png" || piece.file_path=="./Pictures/bpawn.png"){

                //remove the pawn
                int ix=0;
                for(Piece pc : board.pieceList){
                    if(pc.get_pos().equals(piece.get_pos())){
                        board.pieceList.remove(ix);
                        break;
                    }
                    ix++;
                }
                
                PromotionWindow prom_win= new PromotionWindow(piece.get_pos(), board, turn, app_win);
                prom_win.setSize(400,80);
                prom_win.setLayout(new FlowLayout());
                prom_win.setTitle("Piece Promotion");
                prom_win.setVisible(true);
            }
        }      
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
                    color_matrix[i][j]=Color.CYAN;
                else
                    color_matrix[i][j]=Color.BLUE;
            }
        }
        
        game=new ChessGame(this);
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
                            app_win.color_matrix[i][j]=Color.CYAN;
                        else
                             app_win.color_matrix[i][j]=Color.BLUE;
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



class PromotionWindow extends Frame implements ActionListener{
    Button queen, rook, knight, bishop;
    String piece_selected="";
    ChessPos pos;
    ChessBoard board;
    char turn;
    AppWindow app_win;
    
    PromotionWindow(ChessPos pos, ChessBoard board, char turn, AppWindow app_win){
        this.pos=pos;
        this.board=board;
        this.turn=turn;
        this.app_win=app_win;
        
        queen = new Button("Queen");    
        rook = new Button("Rook");
        knight = new Button("Knight");
        bishop = new Button("Bishop");
        
        add(queen);
        add(rook);
        add(knight);
        add(bishop);
        
        queen.addActionListener(this);
        rook.addActionListener(this);
        knight.addActionListener(this);
        bishop.addActionListener(this); 
        
        this.addWindowListener(new MyWindowAdapter());
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        
        if(ae.getSource()==queen){
            if(turn=='w') board.pieceList.add(new Queen(turn, "./Pictures/wqueen.png", pos));            
            else board.pieceList.add(new Queen(turn, "./Pictures/bqueen.png", pos));           
        }
        else if(ae.getSource()==rook){
            if(turn=='w') board.pieceList.add(new Rook(turn, "./Pictures/wrook.png", pos, true));            
            else board.pieceList.add(new Rook(turn, "./Pictures/brook.png", pos, true));
        }
        else if(ae.getSource()==knight){
            if(turn=='w') board.pieceList.add(new Knight(turn, "./Pictures/wknight.png", pos));            
            else board.pieceList.add(new Knight(turn, "./Pictures/bknight.png", pos));
        }
        else if(ae.getSource()==bishop){
            if(turn=='w') board.pieceList.add(new Bishop(turn, "./Pictures/wbishop.png", pos));            
            else board.pieceList.add(new Bishop(turn, "./Pictures/bbishop.png", pos));
        }
        this.app_win.repaint();
        this.dispose();
    }
}
    
    

