/***************************************************************************************
* Name:        Player
* Author:      Dylan Johnston, Stephen Drake, Scott Low
* Date:        April 13, 2009
* Purpose:     Handles the player
****************************************************************************************/

import java.awt.Graphics;
import java.awt.*;
import javax.swing.*;
import java.awt.image.*;
import java.awt.event.*;
import java.awt.geom.*;

class Player extends Entity {
    private int     canshoot = 2;       // number of updates before we can shoot!
    private int     canbomb  = 2;       // number of updates before we can bomb
    private int     score    = 0;       // total score
    private int     streak   = 0;       // streak
    private int     multi    = 1;       // current multiplyer
    private boolean ffire    = false;   // true if the player has fantastic fire
    private int     hp       = 3;       // Health Points
    private int     numLives = 5;       // number of lives
    private int     numBombs = 3;       // number of bombs
    private int     mousex;             // X position of the mouse
    private int     mousey;             // Y position of the mouse
    
    private double speed     = 3.5;     // Move speed
    private double dx        = 0;       // Change in X position
    private double dy        = 0;       // Change in Y position

    
    // Basically how this will work is if a player gets a "buff" we'll
    // call setBuff( Player.SUPERBULLETS ) which will set buffs[1] to 1.
    // it will also set buffduration[1] to 10 ( 10 seconds )
    // we'll go through all these and decrement them each update()
    private String[] buffn;
    private int[] buffs; // which buffs are on.         // "powerups"

    // Buff Constants
    private int SUPERBULLETS = 0;
    private int GODMODE      = 1;
    private int MAX_BUFFS    = 5;

    private boolean  madeportal = false; // have we made a black hole yet? DEBUG
    private Sprite[] frames; // Frames in each sprite
    private Sprite   sheild; // Sprite of the shield
    private Image    life;   // Life Image
    private Image    nuke;   // Bomb Image

    private EntityManager bm;    // Bullet Manager
    private InputHandler  input; // Input Handler
    private Graphics2D    g;     // Graphics Environment
    private ImageObserver ob;    // Image Observer
    private ScreenManager sm;    // Screen Manager
    private Resources     r;     // Resources

    public int getScore()               { return score; }
    public int getStreak()              { return streak; }
    public int getMulti()               { return multi; }
    public int getMultiScore( int c )   { return c * multi; }
    public int getLives()               { return numLives; }
    public int getBombs()               { return numBombs; }

    public void incrementScore( int n ) { score += n * multi; streak += n * multi; }

    public void giveLife() { numLives++; }
    public void giveBomb() { numBombs++; }

    public void setBuffSuperBullets() { buffs[SUPERBULLETS] = 15000;  } // 15 seconds
    public void setBuffGodMode()      { buffs[GODMODE] = 5000; } // 5 seconds.

    public int getBulletType() {
        if ( buffs[SUPERBULLETS] > 0 )
            return 1;
        return 0;
    } // getBulletType

    // s - ???
    // x - x pos
    // y - y pos
    // Constructor
    public Player( int x, int y ) {
        super( x, y );
        frames  = (Sprite[])(Resources.get()).getResource( "tmpChar" );
        sheild  = (Sprite)Resources.get().getResource( "halo" );
        life    = ((Sprite)Resources.get().getResource( "life" )).getImage();
        nuke    = ((Sprite)Resources.get().getResource( "nuke" )).getImage();
        dWidth  = ((Sprite)frames[0]).getWidth();
        dHeight = ((Sprite)frames[0]).getHeight();

        buffs   = new int[MAX_BUFFS];
        buffn   = new String[]{ "Super Bullets", "GODMODE!" };

        isPlayer = true;
        setBuffGodMode();
    } // Constructor
    
    // Initialize Resources
    public void init() {
        r     = Resources.get();
        input = r.getInput();
        bm    = r.getEntityManager();
        g     = r.getGraphics();
        ob    = r.getImageObserver();
    } // init

    // delta - update time
    // Moves the character, rotates it, and fires bullets
    public void update( long delta ) {
        this.mousex    = input.getX();
        this.mousey    = input.getY();
        this.dx        = 0;
        this.dy        = 0;
        this.multi     = 1;
        
        // Determines the angle
        this.angle     = (int)(Math.atan( (( mousey - (dRelativePosY+(dHeight/2)) )/( mousex - (dRelativePosX+(dWidth/2)))) ) * 57.2957795);
        this.canshoot += delta;
        this.canbomb  += delta;

        // set up multipliers
        if (streak > 20000)  { multi = 2; }
        if (streak > 100000) { multi = 3; }
        if (streak > 500000) { multi = 4; }

        if ( mousex < dRelativePosX+(dWidth/2) ) 
            this.angle += 180; 
        else if ( mousey < dRelativePosY+(dHeight/2) ) 
            this.angle += 360;

        if ( input.isKeyDown(KeyEvent.VK_A) ) { dx -= delta * 0.05; }
        if ( input.isKeyDown(KeyEvent.VK_D) ) { dx += delta * 0.05; }
        if ( input.isKeyDown(KeyEvent.VK_W) ) { dy -= delta * 0.05; }
        if ( input.isKeyDown(KeyEvent.VK_S) ) { dy += delta * 0.05; }
        
        /* DEBUG CHEATS :D (Hard coded enemy spawns)
        if ( input.isKeyDown(KeyEvent.VK_E) ) {
            bm.addEntity( new Alien( (int)(Math.random() * r.getWidth()), (int)(Math.random() * r.getHeight() ) ) );
        }
        if ( input.isKeyDown(KeyEvent.VK_R) ) {
            bm.addEntity( new Alien1( (int)(Math.random() * r.getWidth()), (int)(Math.random() * r.getHeight() ) ) );
        }
        if ( input.isKeyDown(KeyEvent.VK_T) ) {
            bm.addEntity( new Alien2( (int)(Math.random() * r.getWidth()), (int)(Math.random() * r.getHeight() ), false ) );
        }*/
        
        // Deploy a bomb if the right mouse button or space bar is pressed
        if ( input.isMouseButtonDown(MouseEvent.BUTTON3) || input.isKeyDown(KeyEvent.VK_SPACE) ) {
            if ( canbomb >= 100 && numBombs > 0 ) {
                bm.clearScreen();
                numBombs --;
            } // if
            canbomb = 0;
        } // if
        
        // if the buff is off we skip
        // if the buff duration is -1 we leave it alone
        // if the buff duration is over 0 we decrease
        // otherwise we turn the buff off.
        for ( int i = 0; i < MAX_BUFFS; i++ ) {
            if ( buffs[i] <= 0 )
                continue;

            if ( buffs[i] > 0 ) {
               buffs[i] -= delta;
            } // if
        } // for

        /* Hard coded black hole spawning
        if ( !madeportal ) {
            if ( input.isKeyDown(KeyEvent.VK_V) ) {
                bm.addEntityNoQ( new BlackHole( 100, 100 ) );
                madeportal = true;
            }
            if ( input.isKeyDown(KeyEvent.VK_V) ) {
                bm.addEntityNoQ( new BlackHole( 300, 300 ) );
                madeportal = true;
            }
            if ( input.isKeyDown(KeyEvent.VK_V) ) {
                bm.addEntityNoQ( new BlackHole( 100, 300 ) );
                madeportal = true;
            }
            if ( input.isKeyDown(KeyEvent.VK_V) ) {
                bm.addEntityNoQ( new BlackHole( 300, 100 ) );
                madeportal = true;
            }
        }*/

        // Fire if the left mouse button is pressed
        if ( input.isMouseButtonDown(MouseEvent.BUTTON1) ) {
            if ( canshoot >= 150 ) {
                if ( score > 10000 ) {
                     // 3 bullets, middle is slightly faster.
                     bm.addBullet( (int)(dPosX + (dWidth / 2)), (int)(dPosY + (dHeight / 2)), (360+angle-5)%360, getBulletType(), 0.6d );
                     bm.addBullet( (int)(dPosX + (dWidth / 2)), (int)(dPosY + (dHeight / 2)), angle, getBulletType(), 0.62d );
                     bm.addBullet( (int)(dPosX + (dWidth / 2)), (int)(dPosY + (dHeight / 2)), (angle+5)%360, getBulletType(), 0.6d );
                } else {
                     bm.addBullet( (int)(dPosX + (dWidth / 2)), (int)(dPosY + (dHeight / 2)), angle, getBulletType(), 0.7d );
                } // else
                canshoot = 0;
            } // if
        } // if

        this.angle += 90;
        this.angle %= 360;

        this.dPosX += ( dx * speed );
        this.dPosY += ( dy * speed );
        if(dPosX < 0) dPosX = 0;
        if(dPosX > r.getBoardWidth()) dPosX = r.getBoardWidth(); 
        if(dPosY < 0) dPosY = 0;
        if(dPosY > r.getBoardHeight()) dPosY = r.getBoardHeight();
        r.setCameraPosition(dPosX, dPosY);
    } // update

    // Draw the player
    public void draw() {
        calcRelativePos();
        if ( buffs[GODMODE] > 0 ) {
            g.drawImage(sheild.getImage(), (int)(dRelativePosX - (sheild.getWidth()/4)), (int)(dRelativePosY - (sheild.getHeight()/4)), ob );
        } // if

        for ( int i = 0; i < numLives; i++ ) {
            g.drawImage( life, r.getWidth() - ( i * 18 )-20, 10, ob );
        } // for

        for ( int i = 0; i < numBombs; i++ ) {
            g.drawImage( nuke, r.getWidth() - ( i * 18 )-20, 20, ob );
        } // for

        g.drawImage( frames[angle].getImage(), (int)dRelativePosX, (int)dRelativePosY, ob );
        g.drawString( "buffs:", 20, 35 );
        for ( int i = 0; i < MAX_BUFFS; i++ ) {
            if ( buffs[i] <= 0 )
                continue;

            g.drawString( buffn[i] + ": " + buffs[i]/1000 + "s", 20, 35+(i*10) );
        } // for

    } // draw

    // Reset the score streak, kill everything on the screen, descrease hp.
    public void collidedWith(Entity other){
        if ( other.isAlien() ) {
            if ( buffs[GODMODE] <= 0 && hp-- <= 0 ) {
                streak   = 0;
                numLives --;
                hp       = 3;
                dPosX    = r.getWidth() /2 ;
                dPosY    = r.getHeight() /2;
                buffs[GODMODE] = 3000;
                bm.clearScreen();
            } // if
        } // if
    } // collidedWith
} // Player