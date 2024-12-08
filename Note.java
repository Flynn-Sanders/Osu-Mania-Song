import java.awt.*;
import static java.awt.Color.white;

public class Note {
    
private int x,y,column;
private boolean isActive;
private int speed=5;
private double travelTime = 7200/speed + 80;
private double spawnTime;

    public Note(int col,double spawnTime){
        y=-30;
        column=col;
        this.spawnTime = spawnTime;
        switch(col){
            case 1: x=30; break;
            case 2: x=130; break;
            case 3: x=230; break;
            case 4: x=330; break;
            default: x=-100; break;
        }
        isActive=true;
    }

    public void move(){
        y+=speed;
        if(y>500){
            isActive=false;
        }
    }

    public void draw(Graphics g){
        g.setColor(white);
        if(isActive){
            g.fillRect(x, y, 40, 40);

            g.setColor(Color.black);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setStroke(new BasicStroke(3));
            g.drawRect(x-1, y-1, 42, 42);
        }
    }

    public boolean getActive(){
        return isActive;
    }
    public int getY(){
        return y;
    }
    public double getTravelTime(){
        return travelTime;
    }
    public int getCol(){
        return column;
    }
    public boolean isNearHitZone(){
        int hitZoneTop = 420;
        int hitZoneBottom = 460;

        return (y + 40 >= hitZoneTop) && (y <= hitZoneBottom); 
    }
}
