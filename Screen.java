import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Screen extends JPanel implements KeyListener, ActionListener{
    private Clip hitSound;
    private AudioInputStream audioStream;
    private SourceDataLine sourceLine;

    private double timeSincePlay;
    private long startTime;
    
    private Timer animationTimer;

    ArrayList<Note> notes = new ArrayList<>();
    private final Hitter s,d,j,k;
    private final JButton play;
    private boolean game;
    Font titleFont = new Font("SansSerif", Font.BOLD, 36);
    Font statsFont = new Font("SansSerif", Font.PLAIN, 16);
    Font descFont = new Font("SansSerif", Font.PLAIN, 24);
    private int hits,misses;
    private double accuracy;

    private char[] noteCol;
    private int[] noteTime;
    private String noteMap = "s:1500,j:1500,k:2500,d:2500,s:3500,k:3500,d:4500,j:4500";
    private int noteIndex = 0;
    
    private BufferedImage menuImg;

    public Screen(){
        preloadSounds();
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


        try {
            menuImg = ImageIO.read(new File("agc.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
	public Dimension getPreferredSize() {
        return new Dimension(400,500);
	}
	

    @Override
	public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if(!game){
            g.drawImage(menuImg, 0, 0, 400, 500, null);
            g.setColor(Color.WHITE);
            g.setFont(titleFont);
            g.drawString("A GREAT CHAOS", 45, 150);
            g.setFont(descFont);
            g.drawString("HOW TO PLAY:\n", 3040, 350);
            
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

    
    private void preloadSounds() {
        try {
            URL hitUrl = this.getClass().getClassLoader().getResource("osu-hit-sound.wav");
            hitSound = AudioSystem.getClip();
            hitSound.open(AudioSystem.getAudioInputStream(hitUrl));
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }
    private void parseNoteMap(String map){
        String[] noteEntries = map.split(",");
        noteCol = new char[noteEntries.length];
        noteTime = new int[noteEntries.length];

        for (int i = 0; i < noteEntries.length; i++){
            String[] sepParts = noteEntries[i].split(":");
            noteTime[i] = Integer.parseInt(sepParts[1]) - 1440;
            noteCol[i] = sepParts[0].charAt(0);
        }
    }


    public void playHitSound() {
        if (hitSound != null) {
            hitSound.setFramePosition(0); 
            hitSound.start();
        }
    }
    private void playSong1Stream() {
    try {
        URL ssUrl = this.getClass().getClassLoader().getResource("ss.wav");
        audioStream = AudioSystem.getAudioInputStream(ssUrl);
        AudioFormat format = audioStream.getFormat();
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        sourceLine = (SourceDataLine) AudioSystem.getLine(info);
        sourceLine.open(format);
        sourceLine.start();

        new Thread(() -> {
            byte[] buffer = new byte[4096];
            int bytesRead;
            try {
                while ((bytesRead = audioStream.read(buffer)) != -1) {
                    sourceLine.write(buffer, 0, bytesRead);
                }
                sourceLine.drain();
                sourceLine.close();
                audioStream.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    } catch (Exception exc) {
        exc.printStackTrace();
    }
}


    public void drawBg(Graphics g){
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 400, 500);
    }
    public void drawStats(Graphics g){
        g.setColor(Color.WHITE);
        g.setFont(statsFont);
        String hitText = "hits: "+hits;
        String missText = "misses: "+misses;
        if(hits+misses > 0){
            accuracy = (hits*100.0) / (hits+misses);
        }
        else{
            accuracy=0;
        }
        String accuracyText = "accuracy: " + String.format("%.2f", accuracy) + "%";

        g.drawString(hitText, 40, 480);
        g.drawString(missText, 140, 480);
        g.drawString(accuracyText, 250, 480);
    }

   public void checkHit(int col) {
    Note currentNote = null;

    for (Note n : notes) {
        if (n.getCol() == col && n.isNearHitZone()) {
            hits++;
            currentNote = n;
            notes.remove(n);
            playHitSound(); 
            break;
        }
    }
    if (currentNote == null) {
        misses++;
    }
}
    
    public void removeInactiveNotes() {
        notes.removeIf(note -> !note.getActive());
    }


    public void animate() {
        startTime = System.nanoTime();
        animationTimer = new Timer(16, e -> {
            long elapsedNanoSec = System.nanoTime() - startTime;
            timeSincePlay=elapsedNanoSec / 1_000_000.0;

            while(noteIndex<noteTime.length && noteTime[noteIndex] <= timeSincePlay){
                do{
                    char col = noteCol[noteIndex];
                    int colInt = switch(col){
                        case 's' -> 1;
                        case 'd' -> 2;
                        case 'j' -> 3;
                        case 'k' -> 4;
                        default -> 0;
                    };
                    if(colInt!=0){
                        notes.add(new Note(colInt,noteTime[noteIndex]));
                        System.out.println("Note " + noteIndex + " spawned at " + timeSincePlay + " ms (expected: " + noteTime[noteIndex] + " ms)");
                    }
                    noteIndex++;
                } while (noteIndex < noteTime.length && noteTime[noteIndex] == noteTime[noteIndex - 1]);
            }

            removeInactiveNotes();
            
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
                game = true;
                repaint();

                new Timer(1500, evt -> {
                parseNoteMap(noteMap);
                animate();
                playSong1Stream();
                ((Timer) evt.getSource()).stop();
            }).start();
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
        repaint();
    }
    public void keyReleased(KeyEvent e){
        char key = e.getKeyChar();
        switch(key){
            case 's': s.setPressed(false); break;
            case 'd': d.setPressed(false); break;
            case 'j': j.setPressed(false); break;
            case 'k': k.setPressed(false); break;
        }
        repaint();
    }
    public void keyTyped(KeyEvent e){}
}
