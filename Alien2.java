/***************************************************************************************
* Name:        Alien2
* Author:      Dylan Johnston, Stephen Drake, Scott Low
* Date:        April 13, 2009
* Purpose:     An alien that tracks towards the player then explodes into two smaller
*              aliens that also follow the player.
****************************************************************************************/

import java.awt.*;
import javax.swing.*;
import java.awt.image.*;

// Needs a better name - Dylan
class Alien2 extends Entity {
    private ImageObserver  ob; // Image Observer
    private Resources      r; // Resources
    private Sprite         sbig; // Sprite for the normal alien
    private Sprite         ssmall; // Sprite for the alien when it has broken apart
    private Player         p;    // Player
    private Graphics2D     g; // Graphics environment
    private double         mX; // X position the player tracks towards
    private double         mY; // y position the player tracks towards
    private int            value; // point value
    private int            frame; // current frame of rotation
    private boolean        small = false; // Returns true if the alien has split apart
    private double         speed = 100; // Move speed

    // x - x position
    // y - y position
    // Constructor
    public Alien2( int x, int y, boolean s ) {
        super( x, y );
        this.small = s;
        this.r     = Resources.get();
        this.g     = r.getGraphics();
        this.p     = r.getPlayer();
        this.ob    = r.getImageObserver();

        if ( small ) {
            this.ssmall  = (Sprite)r.getResource("tmpAlien3_small");
            this.dWidth  = ssmall.getWidth();
            this.dHeight = ssmall.getHeight();
        } else  {
            this.sbig    = (Sprite)r.getResource("tmpAlien3_big");
            this.dWidth  = sbig.getWidth();
            this.dHeight = sbig.getHeight();
        } // else

        this.isAlien = true;
        this.mX = Math.random() * r.getWidth();
        this.mY = Math.random() * r.getHeight();
        this.value = 50;
    } // Constructor

    // Follow the player and update the alien's location
    public void update( long delta ) {
        // unless we're being forced to move, follow the player.
        this.mX = forceMoveX != 0 ? forceMoveX : p.getPosX() + ( p.getWidth() / 2 );
        this.mY = forceMoveY != 0 ? forceMoveY : p.getPosY() + ( p.getHeight() / 2 );

        this.angle = (int)(Math.atan(( mY - (dPosY+(dHeight/2)) )/( mX - (dPosX+(dWidth/2)) )) * 57.2957795);
        
        if ( mX < dPosX+(dWidth/2) ) 
            this.angle += 180; 
        else if ( mY < dPosY+(dHeight/2) ) 
            this.angle += 360;

        this.dPosX += speed * delta/1000 * r.getCos(angle);
        this.dPosY += speed * delta/1000 * r.getSin(angle);

        this.angle += 90;
        this.angle %= 360;
    } // update

    // x - x position
    // y - y positon
    // Forcibly move the object to this position. (For black holes)
    public void forceMove( int x, int y ) {
        forceMoveX = x;
        forceMoveY = y;
    } // forceMove

    // draw the image
    public void draw() {
        calcRelativePos();
        if ( small ) {
            g.drawImage( ssmall.getImage(), (int)dRelativePosX, (int)dRelativePosY, ob );
        } else  {
            g.drawImage( sbig.getImage(), (int)dRelativePosX, (int)dRelativePosY, ob );
        } // if
    } // draw
    
    // e - entity we collided with.
    // Increment players score, create an explosion and delete this.
    public void collidedWith(Entity e){
        this.shouldDelete = true;
        this.hasCollided = true;
        Resources.get().incrementScore(value);
        Resources.get().getEntityManager().addEntity( new Explosion( (int)(dPosX+(dWidth/2)), (int)(dPosY+(dHeight/2)), Resources.get().getPlayer().getMultiScore( value ) ) );
        
        if ( Math.random() > 0.99 )
            r.getEntityManager().addEntity( new GodDrop( (int)(dPosX+(dWidth/2)), (int)(dPosY+(dHeight/2)) ) );
        
        if ( !small ) {
            int angle = (int)(Math.random()*360);
            int dist  = (int)(Math.random()*100);
            int nx = (int)(dPosX + (dist * r.getCos(angle)));
            int ny = (int)(dPosY + (dist * r.getSin(angle)));
            Resources.get().getEntityManager().addEntity( new Alien2( nx, ny, true ) );
            angle = (int)(Math.random()*360);
            dist  = (int)(Math.random()*100);
            nx = (int)(dPosX + (dist * r.getCos(angle)));
            ny = (int)(dPosY + (dist * r.getSin(angle)));
            Resources.get().getEntityManager().addEntity( new Alien2( nx, ny, true ) );
        } // if
    } // collidedWith
} // Alien2