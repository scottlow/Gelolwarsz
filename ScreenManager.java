/***************************************************************************************
* Name:        ScreenManager
* Author:      Dylan Johnston, Stephen Drake, Scott Low
* Date:        April 13, 2009
* Purpose:     Manages each screen state
****************************************************************************************/

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.*;

class ScreenManager extends Canvas {
    private Game         gm;// Our game.
    private QMenu        m; // Menu/etc.
    private Graphics2D   g; // Graphics!
    private InputHandler i; // Input class.
    private Resources    r; // Static reference to resources
    private JFrame       c; // frame we draw on
    private JPanel       p; // panel
    private Highscore    hs;// high scores!
    private boolean      fs    = false; // true if fullscreen
    private boolean      quit  = false; // true if the game is quitting
    private boolean      pause = false; // true if the game is paused (press p)

    private BufferStrategy bs;

    private long delta;       // Time between each refresh
    private long timer = 0;   // Timer used to limit number of pauses
    private int frames = 0;   // How many frames were drawn.

    private int sw     = 640; // screen width
    private int sh     = 480; // screen height
    private int state  = 0;   // screen state
    private int bw     = 1024; // board width
    private int bh     = 768;  // board height
    
    // Screen States
    static int NOTHING = 0;   // Blank Screen
    static int GAME    = 1;   // Game
    static int MENU    = 2;   // Menu Screen


   /* dscenter
    * input:
    *     - g: graphics environment
    *     - s: string to be printed
    *     - w: width
    *     - y: y position
    * purpose: Draw a string of text in the middle of the screen
    */
    public void dscenter( Graphics g, String s, int w, int y ) {
        g.drawString( s, (w/2) - (g.getFontMetrics().stringWidth( s )/2), y );
    }

    // Constructor
    public ScreenManager() {
        // create a window!
        c = new JFrame( "GEOLOLWARSZ!" );
        p = (JPanel)c.getContentPane();
        r = new Resources();

        setBounds( 0, 0, sw, sh );
        setIgnoreRepaint( true );

        p.setPreferredSize( new Dimension( sw, sh ) );
        p.setLayout( null );
        p.add( this );

        // don't draw window if we're going fullscreen.
        if ( fs ) c.setUndecorated( true ); else c.pack();

        c.setResizable( false );
        c.setVisible( true );
        c.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        requestFocus();

        createBufferStrategy( 2 );
        bs = getBufferStrategy();
        g = (Graphics2D)bs.getDrawGraphics();

        // Set display mode and tell the graphics device to give us fullscreen.
        if ( fs ) {
            GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            DisplayMode    dm = new DisplayMode( sw, sh, 32, DisplayMode.REFRESH_RATE_UNKNOWN);
            gd.setFullScreenWindow(c);
            gd.setDisplayMode(dm);
        }

        // Set up listeners and start listening!
        i = new InputHandler();
        addMouseListener( i );
        addMouseMotionListener( i );
        addKeyListener( i );

        // Initialize all resources
        r.setResolution( sw, sh );
        r.setBoardResolution(bw, bh);

        hs = new Highscore();
        r.addImage( "loading", "resources/images/loading.png" );

        g.setColor( new Color( 50, 50, 50 ) );
        g.fillRect( 0, 0, r.getWidth(), r.getHeight() );
        g.drawImage( ((Sprite)r.getResource( "loading" )).getImage(), (sw/2) - 155 , (sh/2) - 32, this );
        g.setColor( Color.white );
        bs.show();

        // load fonts
        r.addFont( "silkscreen", "resources/fonts/slkscr.ttf", 8 );
        r.addFont( "bverdana", "verdana", Font.BOLD, 11 );
        g.setFont( ((Font)r.getResource( "bverdana" )) );
        dscenter( g, "Loaded: Fonts", sw, 320 );
        bs.show();
        
        // load images
        r.addImage( "logo", "resources/images/logo.png" );
        r.addImage( "help", "resources/images/help.png" );
        r.addImage( "egg", "resources/images/egg.png" );
        r.addImage( "borderbg", "resources/images/geololwarszbg.gif" );
        r.addImage( "tmpCursor", "resources/images/cursor.png" );
        r.addImage( "tmpExplosion", "resources/images/asplode.png" );
        r.addImage( "halo", "resources/images/halo.png" );
        r.addImage( "power_blue", "resources/images/power_blue.png" );
        r.addImage( "power_green", "resources/images/power_green.png" );
        r.addImage( "power_red", "resources/images/power_red.png" );
        r.addImage( "power_black", "resources/images/power_black.png" );

        r.addImage( "blackhole", "resources/images/blackhole.png" );
        r.addImage( "life", "resources/images/life.png" );
        r.addImage( "nuke", "resources/images/nuke.png" );
        r.addImage( "tmpAlien3_big", "resources/images/a3_b.png" );
        r.addImage( "tmpAlien3_small", "resources/images/a3_s.png" );
        
        // menu images
        r.addImage( "play", "resources/images/menu/play.png" );
        r.addImage( "play_h", "resources/images/menu/play_h.png" );
        r.addImage( "help", "resources/images/menu/help.png" );
        r.addImage( "help_h", "resources/images/menu/help_h.png" );
        r.addImage( "quit", "resources/images/menu/quit_h.png" );
        r.addImage( "quit_h", "resources/images/menu/quit_h.png" );

        dscenter( g, "Loaded: Images", sw, 330 );
        bs.show();

        // load sounds
        //r.addSound( "boom", "resources/sounds/bomb.wav", 15 );
        //r.addSound( "pewp", "resources/sounds/pewp.wav", 20 );
        //r.addSound( "bg1", "resources/sounds/oldzax.wav", 1 );
        dscenter( g, "Loaded: Sounds", sw, 340 );
        bs.show();

        // load 360 images
        r.addRotated360Image( "tmpBullet1", "resources/images/bullet.png" );
        r.addRotated360Image( "tmpBullet2", "resources/images/bullet_blue.png" );
        r.addRotated360Image( "tmpBullet3", "resources/images/bullet_green.png" );
        r.addRotated360Image( "tmpChar", "resources/images/char.png" );
        r.addRotated360Image( "tmpAlien", "resources/images/a1.png" );
        r.addRotated360Image( "tmpAlien1", "resources/images/a2.png" );
        dscenter( g, "Loaded: Rotated Images", sw, 350 );
        bs.show();
        
        // load highscores
        hs.grabscores();
        dscenter( g, "Loaded: High Scores", sw, 360 );
        bs.show();

        // initialize game.
        gm = new Game( g, i, this, r, hs, sw, sh );
        m  = new QMenu( 15, r.getHeight() - 47, 12, hs );

        state = MENU;

        // Count FPS and calc delta.
        int fps = 0;
        long current = 0;
        long last = (System.nanoTime() / 1000000) + 1000;

        //((Sound)r.getResource( "bg1" )).play();

        while( !quit ) {
            delta = System.nanoTime() / 1000000 - current;
            timer += delta;
            current = System.nanoTime() / 1000000;

            // update FPS count.
            if ( current > last ) {
                frames = fps;
                fps = 0;
                last = current + 1000;
            } // if

            if ( !pause ) {
                update();
                draw();
            } // if

            fps++;

            // pause the game and show a message
            if ( timer > 200 && state != MENU && i.isKeyDown( KeyEvent.VK_P ) ) {
                g.setFont( ((Font)r.getResource( "bverdana" )) );
                dscenter( g, "PAUSED MATE!", sw, 200 );
                bs.show();
                pause = pause ? false : true;
                timer = 0;
            } // if
            
            // quit the game
            if ( i.isKeyDown( KeyEvent.VK_ESCAPE ) )
                quit = true;
        } // while

        // Sometimes if we exit we're still in fullscreen. this forces an update.
        if ( fs )
            GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().setFullScreenWindow(null);

        System.exit(0);
    } // Constructor

   /* reset
    * purpose: Reset the game
    */
    public void reset() {
        gm.reset();
        i.reset();
        state = 2;
    }

    public void setState( int x ) { state = x; }
    
    public int getState() {
        return state;
    }

    // Quits the game
    private void quit() {
        System.exit(0);
    }

    // updates objects, and entities
    private void update() {
        if ( state == MENU )
            m.update( delta, this);
        if ( state == GAME )
            gm.update( delta, frames );
    }
    
    // Picks certain fonts depending on the current game state
    private void draw() {
        if ( state == MENU ) {
            g.setFont( ((Font)r.getResource( "bverdana" )) );
            m.draw( g );
        }
        if ( state == GAME ) {
            g.setFont( ((Font)r.getResource( "silkscreen" )) );
            gm.draw( g, delta, frames );
        } // if

        bs.show();
    } // draw

    // main
    public static void main( String args[] ) {
        new ScreenManager();
    } // main
} // ScreenManager