import java.awt.*;
import java.awt.event.*;
import javax.swing.JPanel;

public class Screen extends JPanel implements KeyListener{

    private Note[] notes = new Note[0];
    private Hitter s,d,j,k;

    public Screen(){
        addKeyListener(this);
        setFocusable(true);

        s=new Hitter(1);
        d=new Hitter(2);
        j=new Hitter(3);
        k=new Hitter(4);
    }

    @Override
	public Dimension getPreferredSize() {
        return new Dimension(400,500);
	}
	

    @Override
	public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBg(g);
        s.drawMe(g);
        d.drawMe(g);
        j.drawMe(g);
        k.drawMe(g);
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
