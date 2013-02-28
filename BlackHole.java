/***************************************************************************************
* Name:        BlackHole
* Author:      Dylan Johnston, Stephen Drake, Scott Low
* Date:        April 13, 2009
* Purpose:     A black hole that sucks all enemies on the screen towards it
****************************************************************************************/
//  A BLACK HOLE! That sucks enemies around it towards it
//  TODO: SUCK THE PLAYER LOL
//        use a circular collision detection. rectangles aint cutting it.

import java.awt.*;
import javax.swing.*;
import java.awt.image.*;
import java.util.*;

class BlackHole extends Entity {
    private int           health;
    private float         power;
    private Graphics2D    g;
    private Player        p;
    private ImageObserver ob;
    private Resources     r;
    private EntityManager bm;
    private ArrayList     entities;
    private Entity        cent;
    private int           dist;
    private int           cx;
    private int           cy;

    // x - x position of black hole.
    // y - y position of black hole.
    // Constructor
    public BlackHole( int x, int y ) {
        super( "blackhole", x, y );
        this.isBlackHole = true;
        health = 100;
        power  = 0f;
        r      = Resources.get();
        g      = r.getGraphics();
        p      = r.getPlayer();
        ob     = r.getImageObserver();
        bm     = r.getEntityManager();
    } // Constructor

    // Draws the black hole
    public void draw() {
        calcRelativePos();
        dWidth  = (int)(24 + power > 76 ? 76 : 24 + power);
        dHeight = (int)(24 + power > 76 ? 76 : 24 + power);
        g.drawImage( sprite.getImage(), (int)dRelativePosX, (int)dRelativePosY, (int)dWidth, (int)dHeight, ob );
        g.drawString( "Health: " + health, (int)dRelativePosX + 20, (int)dRelativePosY );
        g.drawString( "Power: " + power, (int)dRelativePosX + 20, (int)dRelativePosY - 10 );
    } // draw
    
    // Uses the distance formula to find the distance in between two points (proximity)
    private int distance( int x, int y ) {
        int dx = (int) dPosX - x;
        int dy = (int) dPosY - y;
        return (int)Math.sqrt( ( dx * dx ) + ( dy * dy ) );
    } // distance

    // delta - time between updates.
    // things within 100px get "pulled" towards this.
    // things within 10px are destroyed
    public void update( long delta ) {
        if ( health <= 0 ) {
            shouldDelete = true;
            return;
        } // if
        
        entities = bm.getEntities();
        
        // set "our" position to compare with
        cx = (int)(dPosX + (dWidth/2));
        cy = (int)(dPosY + (dHeight/2));

        for ( int i = 0, n = bm.size(); i < n; i++ ) {
            cent = (Entity)entities.get( i );
            dist = distance( cent.getPosX(), cent.getPosY() );
            
            if ( dist <= 100 ) {
                if ( dist <= 10 ) {
                    if ( cent.isAlien )
                        cent.collidedWith( this );
                } else {
                    cent.forceMove( (int)(dPosX+(dWidth/2)), (int)(dPosY+(dHeight/2)) );
                } // else
            } // if
        } // for
    } // update

    // e - entity we collided with
    // If an alien hits this then we get more powerful
    // If we're hit by a bullet it hurts :(
    public void collidedWith( Entity e ) {
        if ( e.isBullet ) {
            if ( health-- <= 0 ) shouldDelete = true;
            if ( power-- <= 0 ) power = 0;
            e.shouldDelete = true;
        } else
            power += 0.05;
    } // collidedWith
} // BlackHole