import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class circle extends JFrame{
    double defenseFactor1=2;
    double defenseFactor2=2;
    double result=0;
    int numberOfturns=0;
    static circle t;
    int boardSize=15;
    int gap=40;
    int Xgap=60;
    int Ygap=60;
    int size=boardSize*gap;
    ArrayList<Integer> a=new ArrayList<Integer>();
    int[][] board=new int[boardSize][boardSize];

    circle(){
//        putChess(board,boardSize/2,boardSize/2,-5);
        setSize(size+200,size+200);
        setVisible(true);
        MousePressListener l=new MousePressListener();
        addMouseListener(l);
//        train();
    }
    public void train(){
        for(int i=1;i<10;i++){
            board=new int[boardSize][boardSize];
            boolean BlackMove=true;
            defenseFactor1=Math.random()*1000;
            defenseFactor2=Math.random()*1000;
            while (CheckWin(board)==0){
                if(BlackMove){
                    AI(0,1,1);
                }else{
                    AI(1,0,-5);
                }
                BlackMove=!BlackMove;
            }
            if(CheckWin(board)==1){
                result=result*((i-1)/i)+defenseFactor1*(1/i);
                System.out.print("run: "+i+" black wins current defenseFactor: "+defenseFactor1+" total df: "+result);
            }else if(CheckWin(board)==-1){
                result=result*((i-1)/i)+defenseFactor2*(1/i);
                System.out.print("run: "+i+" white wins current defenseFactor: "+defenseFactor2+" total df: "+result);
            }
        }
    }
    public void paint(Graphics g){
        if(a.size()==4){
            int x=a.get(0)-a.get(2);
            int y=a.get(1)-a.get(3);
            int r=(int)Math.sqrt(x*x+y*y);
            g.drawOval(a.get(0)-r,a.get(1)-r,r*2,r*2);
        }
        for(int i=0;i<boardSize+1;i++){
            g.drawLine(Xgap+i*gap,Ygap,Xgap+i*gap,Ygap+size);
            g.drawLine(Xgap,Ygap+i*gap,Xgap+size,Ygap+i*gap);
        }
        for(int i=0;i<boardSize;i++){
            for(int j=0;j<boardSize;j++){

                if(board[i][j]==1){
                    g.setColor(Color.BLACK);
                    g.fillOval(Xgap+i*gap,Ygap+j*gap,gap,gap);
                }else if(board[i][j]==-5){
                    g.setColor(Color.white);
                    g.fillOval(Xgap+i*gap,Ygap+j*gap,gap,gap);
                    g.setColor(Color.BLACK);
                    g.drawOval(Xgap+i*gap,Ygap+j*gap,gap,gap);
                }
            }
        }
    }
    class MousePressListener implements MouseListener{
        public void mousePressed(MouseEvent event){
            if(t.CheckWin(board)==0){
                int X=(event.getX()-Xgap)/gap;
                int Y=(event.getY()-Ygap)/gap;
                System.out.println(X+" "+Y);
                if(X>=0&&Y>=0&&putChess(board,X,Y,1)){
                    numberOfturns++;
                    repaint();
                    System.out.println("AI MOVE");
                    if(t.CheckWin(board)==0){
                        t.AI(1,0,-5);
                    }
                }else if(X<0){
                    numberOfturns++;
                    t.AI(0,1,1);
                    t.AI(1,0,-5);
                    repaint();
                }
            }
        }
        public void mouseReleased(MouseEvent event) {}
        public void mouseClicked(MouseEvent event) {}
        public void mouseEntered(MouseEvent event) {}
        public void mouseExited(MouseEvent event) {}
    }

    private int CheckWin(int[][] board) {//1 human win 0 draw -1 AI win
        double[] s=score(board);
        double treashHold=Math.pow(1000,6);
        if(s[0]>=treashHold){
            JOptionPane.showMessageDialog(this, "Human wins", "Game Finished",
                    JOptionPane.WARNING_MESSAGE);
            return 1;
        }else if(s[1]>=treashHold){
            JOptionPane.showMessageDialog(this, "AI wins", "Game Finished",
                    JOptionPane.WARNING_MESSAGE);
            return -1;
        }
        return 0;
    }

    private void AI(int Player,int opponent,int type) {
        double[] currentScore=score(board);
        double max=Double.MIN_VALUE;
        int X=0;
        int Y=0;
        for(int i=0;i<boardSize;i++){
            for(int j=0;j<boardSize;j++){
                int[][] b=copyBoard(board);
                if(putChess(b,i,j,type)){
                    double[] nextScore=score(b);
                    double defenseFactor=0;
                    if(type==1){
                        defenseFactor=defenseFactor1;
                    }else{
                        defenseFactor=defenseFactor2;
                    }
                    double scoreDifference=defenseFactor*(currentScore[opponent]-nextScore[opponent])+(nextScore[Player]-currentScore[Player]);
                    scoreDifference+=(boardSize/2-(Math.abs(i-boardSize/2)))*(boardSize/2-(Math.abs(j-boardSize/2)));
                    if(scoreDifference>max){
                        X=i;
                        Y=j;
                        max=scoreDifference;
                    }
                }
            }
        }
        putChess(board,X,Y,type);
    }

    private int[][] copyBoard(int[][] board) {
        int[][] a=new int[board.length][board[0].length];
        for(int i=0;i<a.length;i++){
            for(int j=0;j<a[i].length;j++){
                a[i][j]=board[i][j];
            }
        }
        return a;
    }

    private boolean putChess(int[][] board,int x, int y,int type){
        if(board[x][y]==0){
            board[x][y]=type;
            return true;//put and human chess at X Y
        }else{
            return false;
        }
    }

    private double[] score(int[][] board) {
        double[] score=new double[2];//0 position is human score, 1 position is AI score
        //diagonal score caculate
        for(int i=0;i<boardSize-5;i++){
            for(int j=0;j<boardSize-5;j++){
                int[] RtoL=new int[5];
                int[] LtoR=new int[5];
                for(int k=0;k<5;k++){
                    LtoR[k]=board[i+k][j+k];
                    RtoL[k]=board[boardSize-1-i-k][j+k];
                }
                addscore(RtoL,score);
                addscore(LtoR,score);
            }
        }
        //horizontial and vertical
        for(int i=0;i<boardSize-5;i++){
            for(int j=0;j<boardSize;j++){
                int[] h=new int[5];
                int[] v=new int[5];
                for(int k=0;k<5;k++){
                    h[k]=board[i+k][j];
                    v[k]=board[j][i+k];
                }
                addscore(h,score);
                addscore(v,score);
            }

        }
//        System.out.println(" human score: "+score[0]+" AI score "+score[1]);
        return score;
    }

    private void addscore(int[] s, double[] score) {
        int sum=0;
        for(int i=0;i<5;i++){
            sum+=s[i];
        }
        if(sum==0){
            return;
        }else if(sum>0){
            int multiplier=scoreMultipler(s);
            score[0]+=multiplier*Math.pow(1000,sum-1);
        }else{
            if(sum%5==0){
                sum=-sum/5;
                int multiplier=scoreMultipler(s);
                score[1]+=multiplier*Math.pow(1000,sum-1);
            }else{
                return;
            }
        }
    }

    private int scoreMultipler(int[] s) {
        int countNoneZero=0;
        for(int i=0;i<5;i++){
            if(s[i]!=0){
                countNoneZero++;
            }
        }
        if(countNoneZero==5){
            return 1000*1000;
        }
        if(countNoneZero==4){
            return 1000;
        }else if(countNoneZero==3){
            if(s[0]==0&&s[4]==0){
                return 100;
            }
        }else if(countNoneZero==2){
            if(s[0]==0&&s[4]==0){
                return 10;
            }
        }
        return 1;

    }

    public static void main(String[] meow){
        t=new circle();
    }
}