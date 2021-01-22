import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.*;

public class Pathfinder extends SSSP{
    private JFrame frame;


    public Pathfinder() {
        frame = new JFrame("Bubbles Program");
        frame.setSize(800, 800);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setPreferredSize(frame.getSize());
        frame.add(new Word(frame.getSize()));
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String... argv) {
        new Pathfinder();
    }

    public static class Word extends JPanel implements Runnable, MouseListener  {


        private Thread animator;
        Dimension d;
        String str = "";
        int xPos = 0;
        int yPos = 0;
        int fontSize = 20;
        int GRID_X = 6;
        int GRID_Y = 8;
        int START = SSSP.nd;
        int GOAL = SSSP.goal;
        int X_INC = 35;
        int Y_INC = 35;
        ArrayList<Integer> save_path;
        int ANIMATE_IDX = 0;
        int TRUE_IDX = 0;
        Location current_loc;


        Color co = new Color(255,255,255);
        Color[] coArray = {
                new Color(255,255,255), new Color(0,255,255), new Color(255,255,0),new Color(255,0,255),new Color(0,0,255)
        };




        public Word (Dimension dimension) {
            setSize(dimension);
            setPreferredSize(dimension);
            addMouseListener(this);
            addKeyListener(new TAdapter());
            setFocusable(true);
            d = getSize();
            save_path = new ArrayList<Integer>();

            //for animating the screen - you won't need to edit
            if (animator == null) {
                animator = new Thread(this);
                animator.start();
            }
            setDoubleBuffered(true);
            save_path = SSSP.develop_graph();
        }

        @Override
        public void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D)g;
            g2.setBackground(Color.WHITE);
            g2.fillRect(0, 0,(int)d.getWidth() , (int)d.getHeight());
            for(int i = 0; i < SSSP.expanded_grid.length; i++){
                for(int j = 0; j < SSSP.expanded_grid[0].length; j++){
                    int x = (j+1) * X_INC;
                    int y = (i+1) * Y_INC;
                    SSSP_Runner.animation_procedure.put(expanded_grid[i][j], new Location(x,y));
                    if(SSSP.expanded_grid[i][j] == START){
                        g2.setColor(Color.GREEN);
                        //g2.drawRect( (i+1) * X_INC, (i+1)*(Y_INC), X_INC, X_INC);
                        g2.fillRect( (j+1) * X_INC, (i+1)*(Y_INC), X_INC, X_INC);
                        g2.setColor(Color.BLACK);
                        g2.drawRect( (j+1) * X_INC, (i+1)*(Y_INC), X_INC, X_INC);
                    } else if(SSSP.expanded_grid[i][j] == GOAL){
                        g2.setColor(Color.RED);
                        //g2.drawRect( (i+1) * X_INC, (i+1)*(Y_INC), X_INC, X_INC);
                        g2.fillRect( (j+1) * X_INC, (i+1)*(Y_INC), X_INC, X_INC);
                        g2.setColor(Color.BLACK);
                        g2.drawRect( (j+1) * X_INC, (i+1)*(Y_INC), X_INC, X_INC);
                    } else if(SSSP.expanded_grid[i][j] == -1){
                        g2.setColor(Color.PINK);
                        g2.fillRect( (j+1) * X_INC, (i+1)*(Y_INC), X_INC, X_INC);
                        g2.setColor(Color.BLACK);
                        g2.drawRect( (j+1) * X_INC, (i+1)*(Y_INC), X_INC, X_INC);
                    } else {
                        float f = normalize_color(global_height_mp[i][j]);
                        g2.setColor(new Color(f,f,f));
                        //g2.drawRect( (i+1) * X_INC, (i+1)*(Y_INC), X_INC, X_INC);
                        g2.fillRect( (j+1) * X_INC, (i+1)*(Y_INC), X_INC, X_INC);
                        g2.setColor(Color.BLACK);
                        g2.drawRect( (j+1) * X_INC, (i+1)*(Y_INC), X_INC, X_INC);
                    }
                }
            }
            if(ANIMATE_IDX == 15) {
                ANIMATE_IDX = 0;
                ++TRUE_IDX;
            }
            if(TRUE_IDX >= save_path.size()){
                TRUE_IDX = 0;
            }

            Location l = SSSP_Runner.animation_procedure.get(save_path.get(TRUE_IDX));
            g2.setColor(Color.YELLOW);
            g2.fillRect(l.x,l.y,X_INC,Y_INC);
            ANIMATE_IDX += 1;
            ANIMATE_IDX %= 1e9;



            /*
            g2.setColor(Color.black);
            g2.fillRect(0, 0,(int)d.getWidth() , (int)d.getHeight());



            g2.setColor(co);

            g2.setFont(new Font("TimesRoman", Font.PLAIN, fontSize));
            g2.drawString("String " + str,20,40);
            */









        }



        public void mousePressed(MouseEvent e) {
            xPos = e.getX();
            yPos = e.getY();



        }

        public void mouseReleased(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }

        public void mouseClicked(MouseEvent e) {
        }

        private class TAdapter extends KeyAdapter {

            public void keyReleased(KeyEvent e) {
                int keyr = e.getKeyCode();

            }

            public void keyPressed(KeyEvent e) {

                int kkey = e.getKeyChar();
                String   cc = Character.toString((char) kkey);
                str = " " + kkey;

                //key events related to strings below. You should NOT need
                // int key = e.getKeyCode();
                //String c = KeyEvent.getKeyText(e.getKeyCode());
                // String   c = Character.toString((char) key);

            }
        }//end of adapter

        public void run() {
            long beforeTime, timeDiff, sleep;
            beforeTime = System.currentTimeMillis();
            int animationDelay = 37;
            long time = System.currentTimeMillis();
            while (true) {// infinite loop
                // spriteManager.update();


                repaint();
                try {
                    time += animationDelay;
                    Thread.sleep(Math.max(0, time - System.currentTimeMillis()));
                } catch (InterruptedException e) {
                    System.out.println(e);
                } // end catch
            } // end while loop
        }// end of run




    }//end of class
}
