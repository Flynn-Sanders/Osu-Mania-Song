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
    private AudioInputStream audioStream;
    private SourceDataLine sourceLine;

    private double timeSincePlay;
    private long startTime;
    
    private Note testNote = new Note(0, 0);
    private double travelTime = testNote.getTravelTime();

    private Timer animationTimer;

    private Clip[] hitSounds;
    private int hitSoundIndex = 0;
    private static final int maxHitSounds = 5;

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
    private String noteMap = "s:3400,k:5125,s:6000,d:6380,j:6790,s:7600,s:8100,d:8470,d:8940,j:9300,j:9770,k:10170,s:11050,d:11050,j:11480,k:11480,s:11900,k:11900,d:12250,j:12250,s:12730,j:12730,d:13130,k:13130,s:13550,s:14000,d:14255,k:14390,j:14600,d:14800,k:15250,s:15600,k:16085,s:16510,d:16510,j:16970,k:16970,d:17380,j:17380";
    ///                                                                                                                                                                                                                            promethazine                    habit   lean    habit   co              deine           habit
    /// 
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
            g.setColor(Color.RED);
            g.setFont(titleFont);
            g.drawString("A GREAT CHAOS", 55, 150);
            g.setFont(descFont);
            g.setColor(Color.lightGray);
            g.drawString("HOW TO PLAY:", 115, 350);
            g.drawString("Use the s, d, j, and k keys when",15, 380);
            g.drawString("the notes in the 4 columns align",15, 400);
            g.drawString("with the hitters (bottom screen)",17, 420);
            
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
            hitSounds = new Clip[maxHitSounds];
            URL hitUrl = this.getClass().getClassLoader().getResource("osu-hit-sound.wav");
            System.out.println("Loading hit sounds");
            for (int i = 0; i < maxHitSounds; i++) {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(hitUrl);
                hitSounds[i] = AudioSystem.getClip();
                hitSounds[i].open(audioStream);
                System.out.println("Clip " + i + " loaded successfully.");
            }
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
            noteTime[i] = Integer.parseInt(sepParts[1]) - (int)travelTime;
            noteCol[i] = sepParts[0].charAt(0);
        }
    }


    public void playHitSound() {
        if (hitSounds == null || hitSounds.length == 0) {
            System.out.println("No hit sounds loaded!");
            return;
        }
    
        Clip currentClip = hitSounds[hitSoundIndex];
        if (currentClip != null) {
            if (currentClip.isRunning()) {
                currentClip.stop(); 
            }
            currentClip.setFramePosition(0); 
            currentClip.start(); 
            hitSoundIndex = (hitSoundIndex + 1) % maxHitSounds; 
        } else {
            System.out.println("Clip " + hitSoundIndex + " is null!");
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
                        //System.out.println("Note " + noteIndex + " at " + timeSincePlay + " ms (expected: " + noteTime[noteIndex] + " ms)");
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

                new Timer(500, evt -> {
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
