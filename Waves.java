/***************************************************************************************
* Name:        Waves
* Author:      Dylan Johnston, Stephen Drake, Scott Low
* Date:        April 13, 2009
* Purpose:     Manage waves (spawns) of aliens
****************************************************************************************/

import java.awt.*;

public class Waves {
    private QuadTreeNode  qt; // QuadTree
    private EntityManager bm; // Entity Manager
    private Player        p; // Player
    private int           sw; // Screen Width
    private int           sh; // Screen Height
    private Graphics2D    g; // Graphics Environment

    private int    currentWave; // Current wave the player is on
    private int    ticks = 100; // Sets the duration of the waves debugging screen
    private boolean showWave = false; // True if debugging mode is on
    private long   delta; // Main delta
    private long   delta1; // Delta for wave one
    private long   delta2; // Delta for wave two
    private long   delta3; // Delta for wave three
    private long   delta4; // Delta for wave four
    private long   delta5; // Delta for wave five
    private long   delta6; // Delta for wave six
    private long   delta7; // Delta for wave seven
    private long   delta8; // Delta for wave eight
    private long   delta9; // Delta for wave nine

    private static int BOUNDARY = 50;  // Spawning proximity boundary

    private int aliensAdded; // Number of aliens added (for corner spawns)
    private int alienCounter; // Number of aliens added in total
    private boolean addedBlackHole = false; // Returns true if a black hole has been added

    // Constructor
    public Waves(){
        currentWave = 2;
        delta = 0;
        aliensAdded = 0;
        alienCounter = 0;
    }

    // Gets resources
    public void init() {
        bm = Resources.get().getEntityManager();
        qt = Resources.get().getQuadTree();
        sw = Resources.get().getWidth();
        sh = Resources.get().getHeight();
        p = (Resources.get()).getPlayer();
    }

    // Allows the spawning of multiple black holes
    public void destroyBlackHole() {
        addedBlackHole = false;
    } // destroyBlackHole

    public String getCurrentWave(){
        return Integer.toString(currentWave);
    }
    
    public String getAdded() {
        return Integer.toString(aliensAdded);
    } // getCount

    // Update each wave and depending on user's score, make the game harder
    public void update( long d ){
        int score;
        
        delta += d;
        delta1 += d;
        delta2 += d;
        delta3 += d;
        delta4 += d;
        delta5 += d;
        delta6 += d;
        delta7 += d;
        delta8 += d;
        delta9 += d;

        score = p.getScore();

        wave1();
        addBlackHole();

        // Check score and make the game harder
        if(score > 200000) {
            wave5();
            wave8();
            wave9();
        } else if(score > 125000) {
            wave2();
            wave3();
            wave5();
            wave6();
            wave7();
        } else if(score > 100000) {
            aliensAdded = 0;
            wave2();
            wave3();
            wave5();
            wave6();
        } else if(score > 60000) {
            wave2();
            wave3();
            wave4();
            wave5();
        } else if(score > 30000) {
            aliensAdded = 0;
            wave2();
            wave3();
            wave5();
        } else if(score > 10000) {
            wave2();
            wave3();
            wave4();
        } else if(score > 5000) {
            wave2();
            wave3();
        } else if(score > 500) {
            wave2();
        } // else

    } // update

    // Reset deltas for a slight delay in spawning
    public void resetDelta() {
        delta = 0;
        delta1 = 0;
        delta2 = 0;
        delta3 = 0;
        delta4 = 0;
        delta5 = 0;
        delta6 = 0;
        delta7 = 0;
        delta8 = 0;
    } // resetDelta

    // Waves (1 being easy and 9 being hard)
    private void wave1(){
        if(delta1 >= 1500) {
            addRandPosAlien1();
            delta1 = 0;
        } // if
    }

    private void wave2() {
        if(delta2 >= 1500) {
            addRandPosAlien();
            delta2 = 0;
        } // if
    } // wave2
    
    private void wave3() {
        if(delta3 >= 1500) {
            addRandPosAlien2();
            delta3 = 0;
        } // if
    } // wave3
    
    private void wave4() {
        if(delta4 >= 500 && aliensAdded < 25) {
            addAlienCornerSpawn();
            delta4 = 0;
        } // if
    } // wave4
    
    private void wave5() {
        if(delta5 >= 1500) {
            addRandPosAlien();
            addRandPosAlien1();
            addRandPosAlien2();
            delta5 = 0;
        } // if
    } // wave5
    
    private void wave6() {
        if(delta6 >= 1000) {
            addRandPosAlien2();
            delta6 = 0;
        } // if
    } // wave6
    
    private void wave7() {
        if(delta7 >= 500 && aliensAdded < 25) {
            addAlien1CornerSpawn();
            addAlien2CornerSpawn();
            delta7 = 0;
        } // if
    } // wave7

    private void wave8() {
        if(delta8 >= 250) {
            for(int i = 0; i < 3; i++) {
                addRandPosAlien();
            } // for
            delta8 = 0;
        } // if
    } // wave8
    
    private void wave9() {
        if(delta9 >= 250) {
            addRandPosAlien2();
            delta9 = 0;
        } // if
    } // wave9

    // Add a black hole in a random corner of the screen
    private void addBlackHole() {
        int corner = 0;
        if(!(addedBlackHole)) {
            corner = (int)(Math.random() * 4 + 1);

            switch(corner) {
            case 1:
                 bm.addEntity(new BlackHole(150, 150));
                 addedBlackHole = true;
                 break;
            case 2:
                 bm.addEntity(new BlackHole((sw - 150), 150));
                 addedBlackHole = true;
                 break;
            case 3:
                 bm.addEntity(new BlackHole(150, (sh - 150)));
                 addedBlackHole = true;
                 break;
            case 4:
                 bm.addEntity(new BlackHole((sw - 150), (sh - 150)));
                 addedBlackHole = true;
                 break;
            } // switch
        } // if
    } // addBlackHole

    // Add a randomly placed alien (while checking for proximity to the player)
    private void addRandPosAlien(){
        int nx  = (int)(Math.random() * sw);
        int ny  = (int)(Math.random() * sh);
        Alien a = new Alien( nx, ny );
        
        while ( true ) {

            if ( a.getDistance( p ) > 100 )
                break;
                
            a.setPosX( (int)(Math.random() * sw ) );
            a.setPosY( (int)(Math.random() * sh ) );
        } // while

        bm.addEntity( a );
        alienCounter++;
    } // addRandPosAlien

    // Add a randomly placed alien (while checking for proximity to the player)
    private void addRandPosAlien1(){
        int nx  = (int)(Math.random() * sw);
        int ny  = (int)(Math.random() * sh);
        Alien1 a = new Alien1( nx, ny );
        
        while ( true ) {

            if ( a.getDistance( p ) > 100 )
                break;
                
            a.setPosX( (int)(Math.random() * sw ) );
            a.setPosY( (int)(Math.random() * sh ) );
        } // while
        
        bm.addEntity( a );
        alienCounter++;
    } // addRandPosAlien1

    // Add a randomly placed alien (while checking for proximity to the player)
    private void addRandPosAlien2(){
        int nx  = (int)(Math.random() * sw);
        int ny  = (int)(Math.random() * sh);
        Alien2 a = new Alien2( nx, ny, false );

        while ( true ) {

            if ( a.getDistance( p ) > 100 )
                break;
                
            a.setPosX( (int)(Math.random() * sw ) );
            a.setPosY( (int)(Math.random() * sh ) );
        } // while
        
        bm.addEntity( a );
        alienCounter++;
    } // addRandPosAlien2

    // Add a corner spawn of tracking aliens
    private void addAlienCornerSpawn(){
        bm.addEntity( new Alien(40, 40));
        bm.addEntity( new Alien(40, sh - 40 - 32));
        bm.addEntity( new Alien(sw - 40 - 32,  sh - 40 - 32));
        bm.addEntity( new Alien(760, sh - 40 - 32));
        aliensAdded += 4;
        alienCounter += 4;
    } // addAlienCornerSpawn
    
    // Add a corner spawn of aliens that break apart
    private void addAlien2CornerSpawn(){
        bm.addEntity( new Alien2(40, 40, false));
        bm.addEntity( new Alien2(40, sh - 40 - 32, false));
        bm.addEntity( new Alien2(sw - 40 - 32,  sh - 40 - 32, false));
        bm.addEntity( new Alien2(760, sh - 40 - 32, false));
        aliensAdded += 4;
        alienCounter += 4;
    } // addAlien2CornerSpawn
    
    private void addAlien1CornerSpawn(){
        bm.addEntity( new Alien1(40, 40));
        bm.addEntity( new Alien1(40, sh - 40 - 32));
        bm.addEntity( new Alien1(sw - 40 - 32,  sh - 40 - 32));
        bm.addEntity( new Alien1(760, sh - 40 - 32));
    }

    // Draw the current wave (debugging)
    public void draw() {
        if( showWave ) {
            if ( ticks-- > 0 ) {
                g.drawString("WAVE " + currentWave, 300, 200);
            } else {
                showWave = false;
            } // else
        } // if
    } // draw

    // Remove an alien the total number of aliens spawned
    public void alienRemoval(){
        alienCounter --;
    } // alienRemoval
} //Levels