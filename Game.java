/***************************************************************************************
* Name:        Game
* Author:      Dylan Johnston, Stephen Drake, Scott Low
* Date:        April 13, 2009
* Purpose:     Handles the game itself
****************************************************************************************/

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.*;

public class Game {

    private Graphics2D     g;        // Graphics Enrironment
    private EntityManager  bm;       // Entity Manager
    private Player         player;   // Player
    private Resources      r;        // Resources
    private QuadTreeNode   grid;     // Quad Tree
    private ScreenManager  sm;       // Screen Manager
    private StarScape      ss;       // stars
    private Waves          w;        // Waves
    private int            camera_x; // X position of camera
    private int            camera_y; // Y position of camera
    private Image          bg;       // Background image

    // Various performance graphcs (debugging)
    private Graph graph;   // delta
    private Graph graph1;  // objects ob the screen
    private Graph graph2;  // FPS

    private boolean showgraph = false; // True if showing the performance graph

    // Constructor
    public Game( Graphics2D g, InputHandler i, ImageObserver ob, Resources r, Highscore hs, int sw, int sh ) {
        this.r = r;
        bg = ((Sprite)Resources.get().getResource( "borderbg" )).getImage();

        // Only initialize the graph if we're showing it
        if ( showgraph ) {
            graph = new Graph( sw, sh );  // delta
            graph1 = new Graph( sw, sh ); // entites
            graph2 = new Graph( sw, sh ); // entites
        }  // if

        // Set up qtree, subdivide a few times.
        grid = new QuadTreeNode( 0, 0, r.getBoardWidth(), r.getBoardHeight() );
        grid.subdivide();
        grid.subdivide();
        grid.subdivide();
        grid.subdivide();

        // Initialize resources
        player = new Player( 384, 285 );
        bm     = new EntityManager( grid );
        w      = new Waves();
        sm     = (ScreenManager)ob;

        // Set up static objects that all classes will be able to access them.
        r.setImageObserver( ob );
        r.setGraphics( g );
        r.setEntityManager( bm );
        r.setInput( i );
        r.setQuadTree( grid );
        r.setHighscore( hs );
        r.setPlayer( player );

        // Init objects here.
        bm.addEntity( player );
        bm.init(w);
        player.init();
        w.init();
    } // Constructor
    
    // Resets the game
    public void reset() {
        player = new Player( 384, 285 );
        player.init();
        
        r.setPlayer( player );
        bm.clearScreen();
    }

    // Update the game and ask for high scores
    public void update( long delta, int fps ) {
        //player.update( delta );
        camera_x = player.getPosX() - 400;
        camera_y = player.getPosY() - 300;
        bm.update( delta );
        w.update( delta );
        

        if ( player.getLives() == 0 ) {
            String name = JOptionPane.showInputDialog( "Your name?" );
            ((Highscore)Resources.get().getHighscore()).submit( name, player.getScore() );
            sm.reset();
            return;
        } // if

        if ( showgraph ) {
            graph.update( delta );
            graph1.update( bm.size() );
            graph2.update( fps );
        } // if
    } // update

    public void draw( Graphics2D g, long delta, int fps ) {
        r.setGraphics( g );

        // Background.
        g.setColor( Color.black );
        g.fillRect( 0, 0, r.getWidth(), r.getHeight() );
        
        g.setColor( new Color( 50, 50, 50 ) );
        g.fillRect( -r.getCameraPositionX() + r.getWidth()/2, -r.getCameraPositionY() + r.getHeight()/2, r.getBoardWidth() + 32, r.getBoardHeight() + 32 );
        g.setColor( new Color( 150, 150, 150 ) );
        g.drawRect( -r.getCameraPositionX() + r.getWidth()/2, -r.getCameraPositionY() + r.getHeight()/2, r.getBoardWidth() + 32, r.getBoardHeight() + 32 );

    	//grid.draw( g, 0, this ); // QuadTree (debugging)
        g.setColor( Color.white );

        w.draw();
    	bm.draw( g ); // Entities

    	// Angle debugging & other debugging strings
    	g.translate( 0, 0 );
    	g.setColor( Color.white );
        g.drawString( "FPS     : " + Long.toString( fps ), 20, 15 );
        g.drawString( "WAVE    : " + w.getCurrentWave(), 20, 25 );
        g.drawString( "SCORE   : " + player.getScore(), 275, 15 );
        g.drawString( "STREAK  : " + player.getStreak(), 275, 25);
        g.drawString( player.getMulti() + "x", 295, 35 );

        if ( showgraph ) {
            g.drawString( "PERFORMANCE: ", 490, 407 );
        	g.setColor( Color.green );
        	graph.draw( g );
        	g.setColor( Color.blue );
        	graph1.draw( g );
        	g.setColor( Color.red );
        	graph2.draw( g );
        } // if
    } // draw
} // Game