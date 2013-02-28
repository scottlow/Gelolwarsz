/***************************************************************************************
* Name:        Entity
* Author:      Dylan Johnston, Stephen Drake, Scott Low
* Date:        April 13, 2009
* Purpose:     Divides the screen into a grid where we can acheive better
*              collision detection performance.
****************************************************************************************/

import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

class QuadTreeNode {

    private int x; //
    private int y;
    private int w;
    private int h;
    private int c; // not sure if List traverses through its contents to get the length. if it does, this is faster.

    public int getx() { return x; }
    public int gety() { return y; }
    public int getw() { return w; }
    public int geth() { return h; }
    public int getc() { return c; }

    QuadTreeNode[] subnodes;
    ArrayList list; // objects within this leaf.

    // w - width
    // h - height
    public QuadTreeNode( int x, int y, int w, int h ) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.c = 0;
        subnodes = null;
        list = new ArrayList();
    }

    // Check for collisions between entities
    public void checkCollisions( Entity e ) {
        double dist = 0;
        if ( subnodes == null ) {
            // we are a leaf, let's add the object here.
            if ( this.c < 0 )
                return;

            Entity c; // entity we compare against.
            for( int i = 0, n = list.size(); i < n; i++) {
                c = (Entity)list.get(i);


                if ( c == null || e.hasCollided || c.hasCollided || c.shouldDelete || c.isBullet || c.isExplosion )
                    continue;

                if ( e.isBullet && c.isPlayer )
                    continue;
                    
                if ( e.isBullet && c.isPowerup )
                    continue;

                dist = e.getDistance( c ); 

                if ( dist < e.getRange() || dist < c.getRange() ) {
                    e.collidedWith( c );
                    c.collidedWith( e );
                }
            }

        } else {
            // figure out which child node this belongs to.

            int[] c_array = { -1, -1, -1, -1 };
            int c_id = 0;
    
            for ( int i = 0; i < 4; i++ ) {
                int c_x = subnodes[i].getx();
                int c_y = subnodes[i].gety();
                int c_w = subnodes[i].getw();
                int c_h = subnodes[i].geth();
    
                Rectangle r = new Rectangle( c_x, c_y, c_w, c_h );
    
                if ( !r.intersects ( new Rectangle( e.getPosX(), e.getPosY(), e.getWidth(), e.getHeight() ) ) )
                   continue;
                else
                    c_array[c_id++] = i;
            }

            for ( int i = 0; i < c_id; i++ ) {
                subnodes[c_array[i]].checkCollisions( e );
            }
        }
    }

    public void addobject( Entity e ) {
        if ( subnodes == null ) {
            // we are a leaf, let's add the object here.
            if ( !list.contains( e ) ) {
                list.add( e );
                this.c++;
            }
        } else {
            // figure out which child node this belongs to.

            int[] c_array = { -1, -1, -1, -1 };
            int c_id = 0;
    
            for ( int i = 0; i < 4; i++ ) {
                int c_x = subnodes[i].getx();
                int c_y = subnodes[i].gety();
                int c_w = subnodes[i].getw();
                int c_h = subnodes[i].geth();
    
                Rectangle r = new Rectangle( c_x, c_y, c_w, c_h );
    
                if ( !r.intersects ( new Rectangle( e.getPosX(), e.getPosY(), e.getWidth(), e.getHeight() ) ) )
                   continue;
                else
                    c_array[c_id++] = i;
            }

            for ( int i = 0; i < c_id; i++ ) {
                subnodes[c_array[i]].addobject( e );
            }
        }
    }

    public void subdivide() {
        if ( subnodes == null ) {
            // no subnodes means we're a leaf.
            // time to subdivide!
            subnodes = new QuadTreeNode[4];
            int sw = this.w / 2;
            int sh = this.h / 2;
            int sx = this.x;
            int sy = this.y;
    
            subnodes[0] = new QuadTreeNode( sx, sy, sw, sh );
            subnodes[1] = new QuadTreeNode( sx + sw, sy, sw, sh );
            subnodes[2] = new QuadTreeNode( sx, sy + sh, sw, sh );
            subnodes[3] = new QuadTreeNode( sx + sw, sy + sh, sw, sh );
        } else {
            // already has branches, let's subdivide those.
            for ( int i = 0; i < 4; i++ ) {
                subnodes[i].subdivide();
            }
        }
    }

    public void remove( Entity e ) {
        if ( subnodes == null ) {
            // we are a leaf, an object may exist here.
            if ( list.contains( e ) ) {
                list.remove( e );
                this.c--;
            }
        } else {
            // figure out which child node this belongs to.

            int[] c_array = { -1, -1, -1, -1 };
            int c_id = 0;
    
            for ( int i = 0; i < 4; i++ ) {
                int c_x = subnodes[i].getx();
                int c_y = subnodes[i].gety();
                int c_w = subnodes[i].getw();
                int c_h = subnodes[i].geth();
    
                Rectangle r = new Rectangle( c_x, c_y, c_w, c_h );
    
                if ( !r.intersects ( new Rectangle( e.getPosX(), e.getPosY(), e.getWidth(), e.getHeight() ) ) )
                   continue;
                else
                    c_array[c_id++] = i;
            }

            for ( int i = 0; i < c_id; i++ ) {
                subnodes[c_array[i]].remove( e );
            }
        }
    }

    public void draw( Graphics2D g, int count, Object ob ) {
        if ( subnodes == null ) {
            g.setColor(Color.blue);
            //g.drawString( Integer.toString(count), x + (w / 2), y + (h / 2 + 12) );

            //draw all objects
            if ( this.c > 0 ) {
                g.fillRect( x, y, w, h );
                g.setColor(Color.red);
                Object[] objects = list.toArray();
                for ( int i = 0; i < c; i++ ) {
                    //if ( list.contains( i );
                    Entity e = (Entity)objects[i];
                    //g.drawImage( e.getSprite().getImage(), e.getPosX(), e.getPosY(), (ImageObserver)ob );
                    g.drawRect( (int)e.getRect().getX(), (int)e.getRect().getY(), (int)e.getRect().getWidth(), (int)e.getRect().getHeight() );
                }
                g.setColor(Color.blue);
            } else {
                //g.drawRect( x, y, w, h);
            }
        } else {
            for ( int i = 0; i < 4; i++ ) {
                subnodes[i].draw( g, i, ob );
            }
        }
    }
}