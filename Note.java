import java.awt.*;

public class Note {
    
private int x,y;
private boolean isActive;

    public Note(int x,int y){
        this.x=x;
        this.y=y;
        isActive=true;
    }

    public void move(){
        y++;
    }

    public void draw(Graphics g){
        g.setColor(Color.black);
        g.fillRect(x, y, 30, 30);
    }

    
    public boolean checkCollision(int col){
        int checkX;
        switch(col){
            case 1: checkX=35; break;
            case 2: checkX=135; break;
            case 3: checkX=235; break;
            case 4: checkX=335; break;
            default: checkX = 0; break;
        }

        if(y+30<=450&&y>=480&&x==checkX){
	        return true;		
        }
        else{
            return false;
        }

    }
}
