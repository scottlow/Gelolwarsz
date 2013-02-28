/***************************************************************************************
* Name:        Entity
* Author:      Dylan Johnston, Stephen Drake, Scott Low
* Date:        April 13, 2009
* Purpose:     Allows bullets to pass through multiple enemies.
****************************************************************************************/

import java.awt.*;
import javax.swing.*;
import java.awt.image.*;

//use a better name - Dylan
class Powerup1 extends Entity {
    private Graphics2D g;     // Graphics environment
    private Player     p;     // Player
    private ImageObserver ob; // Image Observer
    private int ticks = 5000; // 5 seconds
    
    // x - x position
    // y - y position
    // Constructor
    public Powerup1( int x, int y ) {
        super( "power_blue", x, y );

        this.isPowerup = true;

        g = Resources.get().getGraphics();
        p = Resources.get().getPlayer();
        ob = (Resources.get()).getImageObserver();
    } // Constructor
    
    // draws the powerup
    public void draw() {
        calcRelativePos();
        g.drawImage( sprite.getImage(), (int)dRelativePosX, (int)dRelativePosY, ob );
    } // draw
    
    // Delete the powerup after 5 seconds
    public void update( long delta ) {
        if ( (ticks-= delta) < 0 )
            shouldDelete = true;
    }

    // e - entity we collided with
    // tells the player that they get to have FANTASTIC FRING WOOOOOO!
    public void collidedWith( Entity e ) {
        shouldDelete = true;
        p.setBuffSuperBullets();
    } // collidedWith
} // Powerup1