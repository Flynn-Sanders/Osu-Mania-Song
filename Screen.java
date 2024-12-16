    import java.awt.*;
    import static java.awt.Color.white;
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

    //The game is done, but I would like to finish making more songs on it, but this takes far too long for the time given, so i will stay with what is here now.
    //There is a built in game reset cooldown after pressing restart, about a second or two, and any play attempts in this time will trigger endgame again.

    public class Screen extends JPanel implements KeyListener, ActionListener{
        private AudioInputStream audioStream;
        private SourceDataLine sourceLine;

        private double timeSincePlay;
        private long startTime;
        
        private Note testNote = new Note(0, 0);
        private double travelTime = testNote.getTravelTime() ;

        private Timer animationTimer;

        private Clip[] hitSounds;
        private int hitSoundIndex = 0;
        private static final int maxHitSounds = 5;

        ArrayList<Note> notes = new ArrayList<>();
        private final Hitter s,d,j,k;
        private final JButton play;
        private final JButton restart;

        private boolean game;
        private boolean startMenu;
        private boolean endMenu;

        Font titleFont = new Font("SansSerif", Font.BOLD, 36);
        Font statsFont = new Font("SansSerif", Font.PLAIN, 16);
        Font descFont = new Font("SansSerif", Font.PLAIN, 24);
        private int hits,misses;
        private double accuracy;

        private char[] noteCol;
        private int[] noteTime;
        private String noteMap = "s:3400,k:5125,s:6000,d:6380,j:6790,s:7600,s:8100,d:8470,d:8940,j:9300,j:9770,k:10170,s:11050,d:11050,j:11480,k:11480,s:11900,k:11900,d:12250,j:12250,s:12730,j:12730,d:13130,k:13130,s:13550,s:14000,d:14255,k:14390,j:14600,d:14800,k:15250,s:15600,k:16085,s:16510,d:16510,j:16970,k:16970,d:17380,j:17380,s:17750,s:18210,d:18600,k:19080,k:19180,d:19450,j:19870,s:20100,j:20340,k:20800,s:20960,s:21150,d:21350,j:21550,j:21930,k:22170,s:22250,d:22470,s:22650,k:22900,j:23050,s:23300,d:23470,j:23730,k:24080,d:24340,j:24510:,s:24720,k:24970,d:25200,d:25340,j:25550,s:25760,d:25860,k:26120,k:26500,s:26600,j:26850,d:27070,s:28300,j:28300,d:28550,k:28550,s:28680,j:28680,d:29980,k:29980,s:30255,j:30255,d:30425,k:30425,s:31100,k:31340,d:31550,j:31800,d:31970,j:32170,j:32420,s:32510,k:32940,d:33170,s:33420,j:33608,s:33800,d:34525,k:34680,j:34900,d:35080,j:35300,s:35500,s:35850,k:36050,k:36200,d:36430,d:36570,j:36800,s:36980,j:37100,k:37880,d:38120,d:38300";
        ///                                                                                                                                                                                                                            promethazine                    habit   lean    habit   co              deine           habit           kick    stand   yuh     bi-     kuh    yuh       yuh    i'm     lea      nin    i       chop    the     top     off     the     m      claren   this    aint    no   lam        b       ghi     ni      my      white    ho     got     big      teegs   she     re      mind     me     of     syd      ney    sweeny.         SYD              NEY          SWEENY,             SYD             NEY         SWEENY        i     rock    that    stick   like   its     a       purse   yeah    my      chop    a       swingn  like    syd      ney     swee    ney      my     stick   have     big       tee   glers     un     der   neath   like    syd    ney                      
        ///                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      
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
            startMenu=true;

            play = new JButton("PLAY");
            this.setLayout(null);
            play.setBounds(133,250,133,50);
            play.addActionListener(this);
            this.add(play);

            restart = new JButton("RESTART");
            this.setLayout(null);
            restart.setBounds(133,250,133,50);
            restart.addActionListener(this);
            this.add(restart);
            restart.setVisible(false);


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
            restart.setVisible(false);
            play.setVisible(false);
            if(startMenu){
                g.drawImage(menuImg, 0, 0, 400, 500, null);
                g.setColor(Color.RED);
                g.setFont(titleFont);
                g.drawString("NOTE MANIA", 85, 150);
                g.setFont(descFont);
                g.setColor(Color.lightGray);
                g.drawString("HOW TO PLAY:", 115, 350);
                g.drawString("Use the s, d, j, and k keys when",15, 380);
                g.drawString("the notes in the 4 columns align",15, 400);
                g.drawString("with the hitters (bottom screen)",17, 420);
                
                play.setVisible(true);
            }
            else if (endMenu){
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, 400, 500);
                g.setColor(white);
                g.setFont(titleFont);
                g.drawString("YOU DID IT", 100, 150);
                drawStats(g);

                restart.setVisible(true);
                
            }
            else if(game){
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
        public void playSong1Stream() {
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
                        while (!Thread.currentThread().isInterrupted() &&
                            (bytesRead = audioStream.read(buffer)) != -1) {
                            sourceLine.write(buffer, 0, bytesRead);
                        }
                    } catch (IOException ex) {
                        if (!"Stream closed".equals(ex.getMessage())) {
                            ex.printStackTrace();
                        }
                    } finally {
                        try {
                            sourceLine.drain();
                            sourceLine.close();
                            if (audioStream != null) audioStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        }
        
        public void stopSong() {
            if (sourceLine != null && sourceLine.isOpen()) {
                sourceLine.stop();
                sourceLine.close();
            }
            if (audioStream != null) {
                try {
                    audioStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        public void drawBg(Graphics g){
            g.setColor(Color.lightGray);
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
            for(Note n : notes){
                if(n.getY()>500){
                    misses++;
                    n.move();
                }
            }
            notes.removeIf(note -> !note.getActive());
        }
        private void resetGame() {
            if (animationTimer != null) {
                animationTimer.stop();
                animationTimer = null;
            }

            restart.setVisible(false);

            notes.clear();
            noteIndex = 0;
            hits = 0;
            misses = 0;
            accuracy = 0;
            timeSincePlay = 0;

            game=false;
            startMenu=true;
            endMenu=false;

            stopSong();
            repaint();
        }


        public void animate() {
            if (animationTimer != null && animationTimer.isRunning()) {
                animationTimer.stop();
            }

            animationTimer = new Timer(16, e -> {
                long elapsedNanoSec = System.nanoTime() - startTime;
                timeSincePlay=elapsedNanoSec / 1_000_000.0;

                while (noteIndex < noteTime.length && noteTime[noteIndex] <= timeSincePlay && game) {
                    char col = noteCol[noteIndex];
                    int colInt = switch (col) {
                        case 's' -> 1;
                        case 'd' -> 2;
                        case 'j' -> 3;
                        case 'k' -> 4;
                        default -> 0;
                    };
                
                    if (colInt != 0) {
                        notes.add(new Note(colInt, noteTime[noteIndex]));
                        System.out.println("Note "+noteIndex+" at "+timeSincePlay);
                    }
                
                    noteIndex++; 
                }
                    if(noteIndex >= noteTime.length){
                        
                        new Timer((int)travelTime + 1000, evt -> {
                            game=false;
                            endMenu = true; 
                            stopSong();
                    ((Timer) evt.getSource()).stop();
                }).start();
                    }
                

                removeInactiveNotes();
                
                for (Note n : notes) {
                    n.move();
                }
                repaint();
                //System.out.println("start "+startMenu+"\ngame "+game+"\nend "+endMenu+"\n");
            });
            animationTimer.start();
        }
        @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == play) {
                    play.setVisible(false);
                    restart.setVisible(false);
                    game = true;
                    startMenu = false;
                    endMenu=false;
                    repaint();

                    new Timer(500, evt -> {
                    parseNoteMap(noteMap);
                    startTime = System.nanoTime();
                    playSong1Stream();
                    animate();
                    
                    ((Timer) evt.getSource()).stop();
                }).start();
                }
                if (e.getSource() == restart) {
                    resetGame();

                }
        }

        public void keyPressed(KeyEvent e){
            char key = e.getKeyChar();
            switch(key){
                case 's': s.setPressed(true); checkHit(1); break;
                case 'd': d.setPressed(true); checkHit(2); break;
                case 'j': j.setPressed(true); checkHit(3); break;
                case 'k': k.setPressed(true); checkHit(4); break;
                case 'p': game=false; endMenu = true; stopSong(); break;
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
