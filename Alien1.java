/***************************************************************************************
* Name:        Alien1
* Author:      Dylan Johnston, Stephen Drake, Scott Low
* Date:        April 13, 2009
* Purpose:     A "stupid" enemy that tracks to random spots on the screen
****************************************************************************************/

import java.awt.*;
import javax.swing.*;
import java.awt.image.*;

class Alien1 extends Entity {
    private ImageObserver  ob; // Image Observer
    private Sprite[]       frames; // Number of frames in the spirte (for rotation)
    private Resources      r; // Resources
    private Player         p;    // Player
    private Graphics2D     g; // Graphics Environment
    private double         mX; // X position the alien tracks towards
    private double         mY; // Y position the alien tracks towards
    private int            value; // point value
    private int            frame; // current frame of rotation
    private boolean        newpos = true; // Returns true if the alien needs to track to a new point
    private double         speed = 100; // Move speed
    
    // x - x position
    // y - y position
    // Constructor
    public Alien1( int x, int y ) {
        super( x, y );
        this.r       = Resources.get();
        this.g       = r.getGraphics();
        this.p       = r.getPlayer();
        this.ob      = r.getImageObserver();

        this.frames  = (Sprite[])r.getResource("tmpAlien1");
        this.dWidth  = ((Sprite)frames[0]).getWidth();
        this.dHeight = ((Sprite)frames[0]).getHeight();

        this.mX      = Math.random() * r.getWidth();
        this.mY      = Math.random() * r.getHeight();
        
        this.isAlien = true;
        value = 50;
    } // Constructor

    // Picks a random destination to travel to.
    public void update( long delta ) {
        if(getRect().intersects( new Rectangle( (int)mX, (int)mY, 1, 1 ) ) ) {
            this.mX = forceMoveX != 0 ? forceMoveX : Math.random() * (r.getBoardWidth() );
            this.mY = forceMoveY != 0 ? forceMoveY : Math.random() * (r.getBoardHeight() );
            this.newpos = true;
        } // if

        if ( newpos ) {
            this.angle = (int)(Math.atan(( mY - (dPosY+(dHeight/2)) )/( mX - (dPosX+(dWidth/2)) )) * (180 / 3.14f)); //grab inv tan of slope and conv to degrees. ( use 3.14 because precision is gay )
            
            if ( mX < dPosX+(dWidth/2) )
                this.angle += 180;
            else if ( mY < dPosY+(dHeight/2) )
                this.angle += 360;

            this.newpos = false;
            this.frame = (angle+90)%360;
        } // if

        this.dPosX += speed * delta/1000 * r.getCos(angle);
        this.dPosY += speed * delta/1000 * r.getSin(angle);
    } // update

    // x - x position
    // y - y positon
    // Forcibly move the object to this position. (For black holes)
    public void forceMove( int x, int y ) {
        newpos = true;
        forceMoveX = x;
        forceMoveY = y;
    } // forceMove

    // draw the image
    public void draw() {
        calcRelativePos();
        g.drawImage( frames[frame].getImage(), (int)dRelativePosX, (int)dRelativePosY, ob );
    } // draw
    
    // e - entity we collided with.
    // increment players score, create explosion and delete this.
    public void collidedWith(Entity e){
        isAlien = false;
        shouldDelete = true;
        hasCollided = true;
        Resources.get().incrementScore(value);
        Resources.get().getEntityManager().addEntity( new Explosion( (int)(dPosX+(dWidth/2)), (int)(dPosY+(dHeight/2)), Resources.get().getPlayer().getMultiScore( value ) ) );
        if ( Math.random() > 0.95 )
            r.getEntityManager().addEntity( new Powerup1( (int)(dPosX+(dWidth/2)), (int)(dPosY+(dHeight/2)) ) );
    } // collidedWith
} // Alien1