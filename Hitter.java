import java.awt.*;

public class Hitter {
    private int x,y;
    private boolean isPressed;

    public Hitter(int col){
        y=420;
        switch(col){
            case 1: x=30; break;
            case 2: x=130; break;
            case 3: x=230; break;
            case 4: x=330; break;
        }
    }

    public void drawMe(Graphics g){
            Graphics2D g2d = (Graphics2D) g;
            if(isPressed) { g2d.setStroke(new BasicStroke(3)); }
            g.setColor(Color.white);
            g.drawRect(x, y, 40, 40);
            g2d.setStroke(new BasicStroke(0));
        
    }

    public void setPressed(boolean p){
        isPressed=p;
    }
}
