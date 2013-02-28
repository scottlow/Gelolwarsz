/***************************************************************************************
* Name:        Alien
* Author:      Dylan Johnston, Stephen Drake, Scott Low
* Date:        April 13, 2009
* Purpose:     An alien that follows the player around the screen
****************************************************************************************/
import java.awt.*;
import javax.swing.*;
import java.awt.image.*;

class Alien extends Entity {
    private ImageObserver ob;          // ref to image observer
    private Sprite[]      frames;      // rotated frames.
    private Player        p;           // ref to player.
    private Graphics2D    g;           // ref to graphics.
    private int           value = 100; // point value.
    private Resources     r;           // ref to resources
    private double        mX;          // x position the alien moves towards
    private double        mY;          // y position the alien moves towards
    private double        speed = 100; // move speed
    private double        rand = 0;    // random power up drop ("dice roll")

    // x - x pos
    // y - y pos
    // Constructor
    public Alien( int x, int y ) {
        super( x, y );
        this.r       = Resources.get();
        this.g       = r.getGraphics();
        this.p       = r.getPlayer();
        this.ob      = r.getImageObserver();

        this.frames  = (Sprite[])r.getResource( "tmpAlien" );
        this.dWidth  = ((Sprite)frames[0]).getWidth();
        this.dHeight = ((Sprite)frames[0]).getHeight();
        
        this.isAlien = true;
    } // Constructor

    // Moves the alien towards the player
    public void update( long delta ) {
        this.mX     = forceMoveX != 0 ? forceMoveX : p.getPosX() + ( p.getWidth() / 2 );
        this.mY     = forceMoveY != 0 ? forceMoveY : p.getPosY() + ( p.getHeight() / 2 );

        this.angle  = (int)(Math.atan(( mY - (dPosY+(dHeight/2)) )/( mX - (dPosX+(dWidth/2)) )) * 57.2957795);
        
        if ( mX < dPosX+(dWidth/2) ) 
            this.angle += 180; 
        else if ( mY < dPosY+(dHeight/2) )
            this.angle += 360;

        this.dPosX += speed * delta/1000 * r.getCos((int)angle);
        this.dPosY += speed * delta/1000 * r.getSin((int)angle);

        this.angle += 90;
        this.angle %= 360;
    } // update

    public void draw() {
        calcRelativePos();
        g.drawImage( ((Sprite)frames[(int)angle]).getImage(), (int)dRelativePosX, (int)dRelativePosY, ob );
    } // draws the alien

    // e - entity we collided with.
    // Increment players score, create an explosion and delete this.
    public void collidedWith(Entity e){
        this.shouldDelete = true;
        this.hasCollided = true;
        r.incrementScore( value );
        r.getEntityManager().addEntity( new Explosion( (int)(dPosX+(dWidth/2)), (int)(dPosY+(dHeight/2)), Resources.get().getPlayer().getMultiScore( value ) ) );
        
        rand = Math.random();

        if ( rand > 0.97 )
            r.getEntityManager().addEntity( new LifeDrop( (int)(dPosX+(dWidth/2)), (int)(dPosY+(dHeight/2)) ) );
        else if ( rand > 0.96 )
            r.getEntityManager().addEntity( new BombDrop( (int)(dPosX+(dWidth/2)), (int)(dPosY+(dHeight/2)) ) );
        else if ( rand > 0.95 )
            r.getEntityManager().addEntity( new Powerup1( (int)(dPosX+(dWidth/2)), (int)(dPosY+(dHeight/2)) ) );
     } // collidedWith
} // Alien