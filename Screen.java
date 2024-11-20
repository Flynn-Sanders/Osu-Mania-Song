import java.awt.*;
import java.awt.event.*;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Screen extends JPanel implements KeyListener, ActionListener{

    private Note[] notes = new Note[0];
    private Hitter s,d,j,k;
    private JButton play;
    private boolean game;
    Font titleFont = new Font("SansSerif", Font.BOLD, 36);

    public Screen(){
        addKeyListener(this);
        this.setFocusable(true);

        s=new Hitter(1);
        d=new Hitter(2);
        j=new Hitter(3);
        k=new Hitter(4);

        game=false;

        play = new JButton("PLAY");
        this.setLayout(null);
        play.setBounds(133,250,133,50);
        play.addActionListener(this);
        this.add(play);

    }

    @Override
	public Dimension getPreferredSize() {
        return new Dimension(400,500);
	}
	

    @Override
	public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(!game){
            g.setColor(Color.PINK);
            g.fillRect(0, 0, 400, 500);
            g.setColor(Color.darkGray);
            g.setFont(titleFont);
            g.drawString("Monday Night Movin'", 21, 150);
            play.setVisible(true);
        }
        if(game){
            drawBg(g);
            s.drawMe(g);
            d.drawMe(g);
            j.drawMe(g);
            k.drawMe(g);
        }
	} 

    public void drawBg(Graphics g){
        g.setColor(Color.BLUE);
        g.fillRect(0, 0, 400, 500);
    }

    public void animate(){
        while(true){
            try{    
                Thread.sleep(10); 
            } catch (InterruptedException ex){
                Thread.currentThread().interrupt();
            }

            repaint();
        }
    }

    @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == play) {
                play.setVisible(false);
                game=true;
            }
    }

    public void keyPressed(KeyEvent e){
        char key = e.getKeyChar();
        switch(key){
            case 's': s.setPressed(true); break;
            case 'd': d.setPressed(true); break;
            case 'j': j.setPressed(true); break;
            case 'k': k.setPressed(true); break;
        }
    }
    public void keyReleased(KeyEvent e){
        char key = e.getKeyChar();
        switch(key){
            case 's': s.setPressed(false); break;
            case 'd': d.setPressed(false); break;
            case 'j': j.setPressed(false); break;
            case 'k': k.setPressed(false); break;
        }
    }
    public void keyTyped(KeyEvent e){}
}
