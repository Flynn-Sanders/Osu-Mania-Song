import java.awt.*;

public class Note {
    
private int x,y;
private boolean isActive;
private int speed=3;

    public Note(int col){
        y=-31;
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
    }

    public void draw(Graphics g){
        g.setColor(Color.black);
        if(isActive){
            g.fillRect(x, y, 40, 40);
        }
    }

    public boolean getActive(){
        return isActive;
    }

    
    public boolean checkCollision(int col){
        int targetX;
        switch (col) {
            case 1: targetX = 30; break;
            case 2: targetX = 130; break;
            case 3: targetX = 230; break;
            case 4: targetX = 330; break;
            default: return false; 
        }
    
        int hitZoneTop = 415;
        int hitZoneBottom = 465;
    
        boolean withinX = (x == targetX); 
        boolean withinY = (y + 40 >= hitZoneTop) && (y <= hitZoneBottom); 
        boolean outsideY = (y>=hitZoneBottom+1);
    
        if (withinX && withinY) {
            isActive = false; 
            return true;
        }
        else if(outsideY){
            isActive=false;
        }
    
        return false;
    }
}
