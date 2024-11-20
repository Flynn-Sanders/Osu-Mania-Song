import java.awt.*;

public class Hitter {
    private int x,y;
    private boolean isPressed;

    public Hitter(int col){
        y=425;
        switch(col){
            case 1: x=35; break;
            case 2: x=135; break;
            case 3: x=235; break;
            case 4: x=335; break;
        }
    }

    public void drawMe(Graphics g){
            Graphics2D g2d = (Graphics2D) g;
            if(isPressed) { g2d.setStroke(new BasicStroke(3)); }
            g.setColor(Color.white);
            g.drawRect(x, y, 30, 30);
            g2d.setStroke(new BasicStroke(0));
        
    }

    public void setPressed(boolean p){
        isPressed=p;
    }
}
