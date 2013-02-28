/***************************************************************************************
* Name:        Entitymanager
* Author:      Dylan Johnston, Stephen Drake, Scott Low
* Date:        April 13, 2009
* Purpose:     Manages each enity on screen
****************************************************************************************/

import java.awt.image.*;
import java.awt.*;
import java.util.*;

public class EntityManager {

    // Creates a bullet
    class Bullet extends Entity {
        private double speed = 0.6; // move speed
        private int    doi = 0;  // delete on impact
        private double vx = 0; // x velocity
        private double vy = 0; // y velocity
        private int    frame = 0; // current frame

        // x     - x pos
        // y     - y pos
        // angle - dir
        // s     - sprite
        // doi   - delete on impact
        // Constructor
        public Bullet( int x, int y, double angle, Sprite s, int doi, double speed ) {
            super( x-8, y-8 );
            this.doi      = doi;
            this.speed    = speed;
            this.sprite   = s;
            this.dWidth   = 16;
            this.dHeight  = 16;
            this.isBullet = true;
            this.vx       = Resources.get().getCos((int)angle); // precision, who needs it?
            this.vy       = Resources.get().getSin((int)angle);
        } // Constructor
        
        // delta - update time
        // updates the bullet's position
        public void update( long delta ) {
            dPosX += vx * delta * speed;
            dPosY += vy * delta * speed;

            if ( dPosX < 0 || dPosY < 0 || dPosX > r.getBoardWidth()+16 || dPosY > r.getBoardHeight()+16 )
                shouldDelete = true;
        } // update

        // Draws our bullet
        public void draw() {
            calcRelativePos();
            (Resources.get()).getGraphics().drawImage( sprite.getImage(), (int)dRelativePosX, (int)dRelativePosY, (Resources.get()).getImageObserver() );
        } // draw
        
        // e - entity that we collided with
        // Deletes the bullet when it collides with an alien
        public void collidedWith(Entity e){
            if ( doi == 0 ) {
                shouldDelete = true;
                hasCollided  = true;
            } // if
        } // collidedWith
    } // Bullet

    private Sprite[]      bullet1; //
    private Sprite[]      bullet2; // Powerup bullets
    private Sprite[]      bullet3; //
    private ArrayList     entities;  // List of entities
    private ArrayList     toRemove;  // List of entities to be removed from the screen
    private QuadTreeNode  tree;      // Quad Tree
    private Resources     r;         // Resources
    private Graphics2D    g;         // Graphics environment
    private ImageObserver ob;        // Image observer
    private Waves         w;         // waves

    // Manages all entities
    public EntityManager( QuadTreeNode q ) {
        
        tree     = q;
        bullet1  = new Sprite[36];
        bullet2  = new Sprite[36];
        bullet3  = new Sprite[36];
        entities = new ArrayList();
        toRemove = new ArrayList();

        bullet1 = (Sprite[])(Resources.get()).getResource( "tmpBullet1" );
        bullet2 = (Sprite[])(Resources.get()).getResource( "tmpBullet2" );
        bullet3 = (Sprite[])(Resources.get()).getResource( "tmpBullet3" );

        g  = (Resources.get()).getGraphics();
        ob = (Resources.get()).getImageObserver();
    }
    
    // return how many objects we know of.
    public int size() {
        return entities.size();
    }

    // return our entities.
    public ArrayList getEntities() {
        return entities;
    }

    // call entities update(), do collition detection.
    public void update( long delta ) {
        Entity b;
        for( int i = 0, n = entities.size(); i < n; i++) {
            b = (Entity)entities.get(i);
            tree.remove( b );
            b.update( delta  );
    
            if ( b.isBullet || b.isPlayer || b.isBlackHole) tree.checkCollisions( b );
            if ( b.shouldDelete ) toRemove.add( b ); else tree.addobject( b );
        }
    
        // remove objects.
        for( int i = 0, n = toRemove.size(); i < n; i++) {
            b = (Entity)toRemove.get(i);
            entities.remove( b );
            tree.remove( b );
            if( b.isAlien ) {
                w.alienRemoval();
            }
        }
    
        toRemove.clear();
    }

    // initialize things that are initated later.
    public void init(Waves waves){
        w = waves;
        r = Resources.get();
    }

    // call draw() for all our entities.
    public void draw( Graphics2D g ) {
        for( int i = entities.size() - 1; i >= 0; i--) {
            ((Entity)entities.get(i)).draw();
        }
    }
    
    // adds an entity to the quadtree and to our local list.
    public void addEntity( Entity e ) {
        entities.add( e );
        tree.addobject( e );
    }
    
    // adds an entity to our local list.
    public void addEntityNoQ( Entity e ) {
        entities.add( e );
    }

    // x   - x position
    // y   - y position
    // a   - angle of bullet
    // doi - do we die on collision?
    // s   - speed.
    // Constructor
    public void addBullet( int x, int y, double a, int doi, double s ) {
        Bullet b = null;
        if ( doi == 0 ) {
            b = new Bullet( x, y, a, bullet1[(int)((a+90)%360)], doi, s );
        } else if ( doi == 1 ) {
            b = new Bullet( x, y, a, bullet2[(int)((a+90)%360)], doi, s );
        } else if ( doi == 2 ) {
            b = new Bullet( x, y, a, bullet3[(int)((a+90)%360)], doi, s );
        } // else
        if ( b == null )
            return;
        entities.add( b );
        //((Sound)r.getResource( "pewp" )).play();
        tree.addobject( b );
    } // addBullet

    // Clears the screen then re-adds the player.
    public void clearScreen() {
        for(int i = 0; i < entities.size(); i++) {
            toRemove.add((Entity)entities.get(i));
        } // for
        addEntity( Resources.get().getPlayer() );
    } // clearScreen
} // EntityManager