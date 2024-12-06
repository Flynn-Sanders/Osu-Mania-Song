import java.awt.*;
import static java.awt.Color.white;

public class Note {
    
private int x,y,column;
private boolean isActive;
private int speed=5;

    public Note(int col){
        y=-31;
        column=col;
        switch(col){
            case 1: x=30; break;
            case 2: x=130; break;
            case 3: x=230; break;
            case 4: x=330; break;
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
        }
    }

    public boolean getActive(){
        return isActive;
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
