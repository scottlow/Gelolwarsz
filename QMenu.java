/////////////////////////////////////////////////////////
//
//  GEOLOLWARZ - QMenu.java
//  Dylan Johnston
//  Stephen Drake
//  Scott Low
//
//  The main menu screen

import java.util.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;

class QMenu {
    private int x; // x position of the menu
    private int y; // y position of the menu
    private int s; // spacing in between each item
    private long delta = 0;
    private InputHandler  input;
    private ImageObserver ob;
    private Highscore     hs;
    private Image         logo;
    private Image         hjalp;
    private Image         egg;
    private boolean       showegg;

    //private boolean o; // show outline
    private int pos = 0; // menu position.
    private int max = 0;
    private boolean help = false;
    private String credz = "Written by Dylan Johnston, Stephen Drake and Scott Low";
    private String[] items = { "Play", "Help", "Exit" };

    // x - x position
    // y - y position
    // s - spacing in between each line of text.
    public QMenu( int x, int y, int s, Highscore h ) {
        this.x = x;
        this.y = y;
        this.s = s;
        max   = items.length - 1;
        ob    = Resources.get().getImageObserver();
        input = Resources.get().getInput();
        hs    = h;
        logo  = ((Sprite)Resources.get().getResource("logo")).getImage();
        hjalp = ((Sprite)Resources.get().getResource("help")).getImage();
        egg   = ((Sprite)Resources.get().getResource("egg")).getImage();
    }

    // draws the menu
    public void draw( Graphics2D g ) {

        g.setColor( new Color( 50, 50, 50 ) );
        g.fillRect( 0, 0, 640, 480 );

        // draw hjalp image.
        
        if ( showegg ) {
            g.drawImage(egg, 0, 0, ob );
            return;
        }

        if ( help ) {
            g.drawImage(hjalp, 0, 0, ob );
            return;
        }

        // a logo! OR TEXT! OR BOTH!
        g.drawImage(logo, 20, 20, ob );

        // draw menu items
        for ( int i = 0; i <= max; i++ ) {
            if ( i == pos ) g.setColor( Color.green ); else g.setColor( Color.white );
            g.drawString( items[i], x, y+(s*i) );
        }

        // draw us :D
        g.setColor( Color.white );
        hs.drawHighscores( g, 0, 20 );
        g.drawString( credz, Resources.get().getWidth() - g.getFontMetrics().stringWidth( credz ) - 5, Resources.get().getHeight() - 10 );
    }

    // d  - update time
    // sm - reference of ScreenManager so we can change screens.
    // checks for user input and changes screens/closes.
    public void update( long d, ScreenManager sm ) {
        delta += d;
        if( delta > 100 ){
            if ( help && input.isAnyKeyDown() )
                help = false;

            if ( input.isKeyDown( KeyEvent.VK_UP ) ) {
                pos -= 1;
                if ( pos < 0 ) pos = max;
                delta = 0;
                ((Sound)Resources.get().getResource( "pewp" )).play();
            }
            if ( input.isKeyDown( KeyEvent.VK_DOWN ) ) {
                pos += 1;
                if ( pos > max ) pos = 0;
                delta = 0;
                ((Sound)Resources.get().getResource( "pewp" )).play();
            }
            if ( input.isKeyDown( KeyEvent.VK_R ) ) {
                hs.grabscores();
            }
            if ( input.isKeyDown( KeyEvent.VK_Q ) && input.isKeyDown( KeyEvent.VK_W ) &&  input.isKeyDown( KeyEvent.VK_E ) ) {
                showegg = true;
            } else {
                showegg = false;
            }
            if ( input.isKeyDown( KeyEvent.VK_ENTER ) ) {
                //((Sound)Resources.get().getResource( "boom" )).play();
                switch( pos ) {
                    // PLAY
                    case 0: sm.setState(1); break;
                    // OPTIONS
                    case 1: help = true; break;
                    // Credits
                    case 2: System.exit(0); break;
                }
                delta = 0;
            }

        }
    }
}