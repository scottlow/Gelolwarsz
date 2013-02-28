/***************************************************************************************
* Name:        Explosion
* Author:      Dylan Johnston, Stephen Drake, Scott Low
* Date:        April 13, 2009
* Purpose:     Makes an explosion
****************************************************************************************/

class Explosion extends Entity {
    private int ticks = 10;
    private String value;

    // x - x position
    // y - y position
    // v - "value" to display
    // Constructor
    public Explosion( int x, int y, int v ) {
        super( "tmpExplosion", x, y );
        this.isExplosion = true;
        dPosX -= sprite.getWidth() / 2;
        dPosY -= sprite.getHeight() / 2;
        value = Integer.toString(v);
        //((Sound)Resources.get().getResource( "boom" )).play();
    } // Constructor
    
    // Count down the number of ticks before the explosion disappears.
    public void update( long delta ) {
        if ( ticks-- == 0 )
            shouldDelete = true;
    } // update
    
    // Draws the explosion
    public void draw() {
        calcRelativePos();
        Resources.get().getGraphics().drawString( value, (int)dRelativePosX + 20, (int)dRelativePosY );
        Resources.get().getGraphics().drawImage( sprite.getImage(), (int)dRelativePosX, (int)dRelativePosY, Resources.get().getImageObserver() );
    } // draw
    
    // Should never run
    public void collidedWith( Entity e ) { System.out.println( "WE'RE BEING FIRED?" ); }
}