import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Screen extends JPanel implements KeyListener, ActionListener{

    ArrayList<Note> notes = new ArrayList<>();
    private final Hitter s,d,j,k;
    private final JButton play;
    private boolean game;
    Font titleFont = new Font("SansSerif", Font.BOLD, 36);
    Font statsFont = new Font("SansSerif", Font.PLAIN, 16);
    private int hits,misses;
    private double accuracy;

    private final String testMapString = "sdjkdjkjksjds";

    

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
            drawStats(g);
            s.drawMe(g);
            d.drawMe(g);
            j.drawMe(g);
            k.drawMe(g);

            for(Note n : notes){
                n.draw(g);
            }
        }
	} 

    public void drawBg(Graphics g){
        g.setColor(Color.BLUE);
        g.fillRect(0, 0, 400, 500);
    }
    public void drawStats(Graphics g){
        g.setColor(Color.DARK_GRAY);
        g.setFont(statsFont);
        String hitText = "hits: "+hits;
        String missText = "misses: "+misses;
        if(hits+misses > 0){
            accuracy = (hits*100.0) / (hits+misses);
        }
        else{
            accuracy=0;
        }
        String accuracyText = "accuracy: "+accuracy+"%";

        g.drawString(hitText, 50, 480);
        g.drawString(missText, 150, 480);
        g.drawString(accuracyText, 270, 480);
    }

    public void checkHit(int col) {
        boolean hit = false;
        for (Note n : notes) {
            if (n.checkCollision(col)) {
                hits++;
                hit = true;
                break; 
            }
        }
        if (!hit) {
            misses++;
        }
    }

    public void runNotes(String map) {
        char[] beatList = map.toCharArray();
        Timer noteTimer = new Timer(125, new ActionListener() {
            private int index = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (index < beatList.length && game) {
                    char c = beatList[index++];
                    switch (c) {
                        case 's': notes.add(new Note(1)); break;
                        case 'd': notes.add(new Note(2)); break;
                        case 'j': notes.add(new Note(3)); break;
                        case 'k': notes.add(new Note(4)); break;
                    }
                } else {
                    ((Timer) e.getSource()).stop(); 
                }
            }
        });
        noteTimer.start();
}

    private Timer animationTimer;

    public void animate() {
        animationTimer = new Timer(10, e -> {
            notes.removeIf(note -> !note.getActive());
            for (Note n : notes) {
                n.move();
            }
            repaint();
        });
        animationTimer.start();
    }

    @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == play) {
                play.setVisible(false);
                game=true;
                try{    
                    Thread.sleep(2000); 
                } catch (InterruptedException ex){
                    Thread.currentThread().interrupt();
                }

                animate();
                new Thread(() -> runNotes(testMapString)).start();
            }
    }

    public void keyPressed(KeyEvent e){
        char key = e.getKeyChar();
        switch(key){
            case 's': s.setPressed(true); checkHit(1); break;
            case 'd': d.setPressed(true); checkHit(2); break;
            case 'j': j.setPressed(true); checkHit(3); break;
            case 'k': k.setPressed(true); checkHit(4); break;
            case 'p': game=false; break;
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
