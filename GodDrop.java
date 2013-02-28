/***************************************************************************************
* Name:        GodDrop
* Author:      Dylan Johnston, Stephen Drake, Scott Low
* Date:        April 13, 2009
* Purpose:     Gives the player god mode
****************************************************************************************/

import java.awt.*;
import javax.swing.*;
import java.awt.image.*;

class GodDrop extends Entity {
    private Graphics2D g;     // Graphics entironment
    private Player     p;     // Player
    private ImageObserver ob; // Image Observer
    private int ticks = 5000; // 5 seconds
    
    // x - x position
    // y - y position
    // Constructor
    public GodDrop( int x, int y ) {
        super( "power_black", x, y );

        this.isPowerup = true;

        g = Resources.get().getGraphics();
        p = Resources.get().getPlayer();
        ob = (Resources.get()).getImageObserver();
    } // Constructor
    
    // Draws the powerup
    public void draw() {
        calcRelativePos();
        g.drawImage( sprite.getImage(), (int)dRelativePosX, (int)dRelativePosY, ob );
    } // draw
    
    // Deletes the powerup after 5 seconds
    public void update( long delta ) {
        if ( (ticks-= delta) < 0 )
            shouldDelete = true;
    } // update

    // e - entity we collided with
    // tells the player that they get god mode
    public void collidedWith( Entity e ) {
        shouldDelete = true;
        p.setBuffGodMode();
    } // collidedWith
} // GodDrop