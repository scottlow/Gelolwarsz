/***************************************************************************************
* Name:        Entity
* Author:      Dylan Johnston, Stephen Drake, Scott Low
* Date:        April 13, 2009
* Purpose:     The generic entity class
****************************************************************************************/
//  The Entity class, all entites use this. It stores position, velocity,
//  angle, type, health, sprites, etc.

import java.awt.*;
public abstract class Entity {
    protected boolean isBullet;    // returns true if an instance of this class is a bullet
    protected boolean isExplosion; // returns true if an instance of this class is a explosion
    protected boolean isAlien;     // returns true if an instance of this class is an alien
    protected boolean isPlayer;    // returns true if an instance of this class is a player.
    protected boolean isPowerup;   // returns true if an instance of this class is a powerup
    protected boolean isBlackHole; // returns true if an instance of this class is a black hole
    protected float dPosX;         //current x location
    protected float dPosY;         //current y location
    protected float dRelativePosX; //relative x location
    protected float dRelativePosY; //relativet y location
    protected int dWidth;          //current x location
    protected int dHeight;         //current x location
    protected boolean hasCollided; // Have we hit something yet?
    protected boolean shouldDelete;// should we delete this?
    protected Sprite sprite;       //this entity's sprite
    protected double dVelocityX;   //horizontal speed (px/s) + -> right
    protected double dVelocityY;   //vertical speed (px/s) + -> down
    protected int angle;           // current angle of movement
    protected int damage;          // damage entity inflicts upon collision
    protected int forceMoveX;      // X position to force a move towards
    protected int forceMoveY;      // Y position to force a move towards
    private Resources     r;       // Resources

    public Rectangle getRect() { return new Rectangle( (int)this.dPosX, (int)this.dPosY, this.dWidth, this.dHeight ); }
    public Sprite getSprite(){ return this.sprite; }

    public void setPosX( int x ) { this.dPosX = x; }
    public void setPosY( int y ) { this.dPosY = y; }
    
    public int getAngle() { return this.angle; }
    public int getPosX()  { return (int)this.dPosX; }
    public int getPosY()  { return (int)this.dPosY; }
    public int getWidth() { return this.dWidth; }
    public int getHeight(){ return this.dHeight; }
    public int getRange() { return dWidth/2; }
    public int getCenterPosX() { return (int)(dPosX + (dWidth / 2)); }
    public int getCenterPosY() { return (int)(dPosY + (dHeight / 2)); }

    //creates a new entity with a sprite, position and acceleration
    // Constructor
    public Entity (String url, int newX, int newY){
        r     = Resources.get();
        this.dPosX = newX;
        this.dPosY = newY;
        this.sprite = (Sprite)(Resources.get()).getResource(url);
        this.dWidth = sprite.getWidth();
        this.dHeight = sprite.getHeight();
    } //Entity

    public Entity (int newX, int newY){
        r     = Resources.get();
        this.dPosX = newX;
        this.dPosY = newY;
    } //Entity

    public Entity (String url, int newX, int newY, int width, int height){
        r     = Resources.get();
        this.dPosX = newX;
        this.dPosY = newY;
        this.dWidth = width;
        this.dHeight = height;
        this.sprite = (Sprite)(Resources.get()).getResource(url);
        this.dWidth = sprite.getWidth();
        this.dHeight = sprite.getHeight();
    } //Entity
    
    public Entity (int newX, int newY, int width, int height){
        r     = Resources.get();
        this.dPosX = newX;
        this.dPosY = newY;
        this.dWidth = width;
        this.dHeight = height;
        this.dWidth = sprite.getWidth();
        this.dHeight = sprite.getHeight();
    } //Entity

    public boolean isAlien() {
        return isAlien;
    } // isAlien

    public abstract void draw();
    public abstract void update( long delta );
    public abstract void collidedWith( Entity o );

    // Force this entity to move
    public void forceMove( int x, int y ) {
        forceMoveX = x;
        forceMoveY = y;
    } // forceMove

    // Distance between the centers of two objects.
    public int getDistance( Entity e ) {
        int dx = e.getCenterPosX() - getCenterPosX();
        int dy = e.getCenterPosY() - getCenterPosY();
        return (int)Math.sqrt( ( dx * dx ) + ( dy * dy ) );
    } // getDistance
    
    // Calculates where on the screen the entity must be drawn
    public void calcRelativePos() {
        dRelativePosX = dPosX - r.getCameraPositionX() + r.getWidth() / 2;
        dRelativePosY = dPosY - r.getCameraPositionY() + r.getHeight() / 2;
    } // calcRelativePos
} //Entity