/***************************************************************************************
* Name:        Alien
* Author:      Dylan Johnston, Stephen Drake, Scott Low
* Date:        April 13, 2009
* Purpose:     A power up that gives the player another bomb
****************************************************************************************/

import java.awt.*;
import javax.swing.*;
import java.awt.image.*;

class BombDrop extends Entity {
    private Graphics2D g; // Graphics
    private Player     p; // Player
    private ImageObserver ob; // Image Observer
    private int ticks = 5000; // 5 second duration after dropped
    
    // x - x position
    // y - y position
    // Constructor
    public BombDrop( int x, int y ) {
        super( "power_red", x, y );

        this.isPowerup = true;

        g = Resources.get().getGraphics();
        p = Resources.get().getPlayer();
        ob = (Resources.get()).getImageObserver();
    } // Constructor
    
    // draws the image
    public void draw() {
        calcRelativePos();
        g.drawImage( sprite.getImage(), (int)dRelativePosX, (int)dRelativePosY, ob );
    } // draw
    
    // Delete the player
    public void update( long delta ) {
        if ( (ticks-= delta) < 0 )
            shouldDelete = true;
    } // update

    // e - entity we collided with
    // Give the player another bomb
    public void collidedWith( Entity e ) {
        shouldDelete = true;
        p.giveBomb();
    } // collidedWith
} // BombDrop