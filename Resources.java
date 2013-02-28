/***************************************************************************************
* Name:        Entity
* Author:      Dylan Johnston, Stephen Drake, Scott Low
* Date:        April 13, 2009
* Purpose:     Stores all resources
****************************************************************************************/

import java.util.*;
import java.awt.*;
import java.io.*;
import java.awt.image.*;
import javax.imageio.*;
import java.net.URL;
import java.applet.*;
import javax.sound.sampled.*;

class Resources {
    
    // Static members that we can get any time.
    private static Resources     s = new Resources(); // Resrouces
    private static Player        p;                   // Player
    private static InputHandler  ih;                  // Input Handler
    private static ImageObserver ob;                  // Image Observer
    private static Highscore     hs;                  // Highscores
    private static Graphics2D    g;                   // Graphics Environment
    private static QuadTreeNode  q;                   // Quad Tree
    private static EntityManager bm;                  // Entity Manager
    private static Hashtable     t;                   // Hashtable

    private static double[] costable;                 // Cosine table
    private static double[] sintable;                 // Sine table
    private static double[] slopetable;               // Slope table

    // Things not everything needs to know about.
    private static int sw;                            // Screen Width
    private static int sh;                            // Screen Height
    private static int bw;                            // Board Width
    private static int bh;                            // Board Height
    private static double cx = 0;                     // Camera x position
    private static double cy = 0;                     // Camera y position
    
    // Set properties.
    public void setPlayer( Player p )                { this.p = p; }
    public void setResolution( int w, int h )        { this.sw = w; this.sh = h; }
    public void setQuadTree( QuadTreeNode q )        { this.q = q; }
    public void setGraphics( Graphics2D g )          { this.g = g; }
    public void setInput( InputHandler ih )          { this.ih = ih; }
    public void setEntityManager( EntityManager bm ) { this.bm = bm; }
    public void setImageObserver( ImageObserver ob ) { this.ob = ob; }
    public void setHighscore( Highscore hs )         { this.hs = hs; }

    public void incrementScore( int s ) { this.p.incrementScore( s ); }
    public void setBoardResolution(int w, int h)     { this.bw = w; this.bh = h; }
    public void setCameraPosition (double x, double y)     { this.cx = x; this.cy = y; }

    // Get properties.
    public Player getPlayer()               { return p; }
    public QuadTreeNode getQuadTree()       { return q; }
    public Graphics2D getGraphics()         { return g; }
    public InputHandler getInput()          { return ih; }
    public Highscore getHighscore()         { return hs; }
    public EntityManager getEntityManager() { return bm; }
    public ImageObserver getImageObserver() { return ob; }
    public static Resources get()           { return s; }
    public int getScore()                   { return p.getScore(); }
    public int getWidth()                   { return sw; }
    public int getHeight()                  { return sh; }
    public int getBoardWidth()              { return bw; }
    public int getBoardHeight()             { return bh; }
    public int getCameraPositionX()         { return (int)cx; }
    public int getCameraPositionY()         { return (int)cy; }
    
    // Why I did this:
    // Java's trig functions are crazy ammounts of slow when they're > 45 deg.
    // The other thing is that from what I've read they don't properly check
    // if they can use the SSE2 instruction when doing fp operations. I might be wrong
    // but who cares, we don't really need decimal precision anyways.
    public double getCos( int angle )       { return costable[angle]; }
    public double getSin( int angle )       { return sintable[angle]; }

    // Constructor
    public Resources() { 
        t = new Hashtable();
        costable = new double[361];
        sintable = new double[361];

        for ( int i = 0; i < 361; i++ ) {
            costable[i] = Math.cos( Math.toRadians(i) );
            sintable[i] = Math.sin( Math.toRadians(i) );
        } // for
    } // Constructor
    
    // key      -  name of key in the table
    // location - location of audio
    // buffers  - how many copies do we want? ( java mixers = too much work :D )
    // purpose: adds a sound to our "resources"
    public void addSound( String key, String location, int buffers ) {
        try {
            URL u = Resources.class.getResource((String)location);

            AudioInputStream[] ais = new AudioInputStream[buffers];
            Clip[] s = new Clip[buffers];

            for ( int i = 0; i < buffers; i++ ) {
                ais[i] = AudioSystem.getAudioInputStream( u );
                s[i]   = AudioSystem.getClip();
                s[i].open( ais[i] );
            } // for

            t.put( key, new Sound( s, 0.7f ) );
        } catch ( Exception e ) {
            e.printStackTrace();
        } // catch
    } // addSound


    // key   - name of image to rotate.
    // value - location of file.
    // rotates an image 360 times. ( sounds stupid but it looks awful
    // otherwise ) then we add it to the hash table.
    public void addRotated360Image( String key, Object value ) {
        Sprite   qq;
        Sprite[] images = new Sprite[360];
        BufferedImage simg = null;
        try {
            simg = ImageIO.read( Resources.class.getResource((String)value) );
        } catch ( IOException ioe ) {
            System.out.println( "GOOD JOB: " + simg + " doesn't exist." );
        } // catch
        
        // Give this image the same bit depth as the screen.
        GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        Image image = gc.createCompatibleImage( simg.getWidth(), simg.getHeight(), Transparency.TRANSLUCENT );
        image.getGraphics().drawImage(simg, 0, 0, null);
        qq = new Sprite(simg);

        for ( int i = 0; i < 360; i++ )
            images[i] = qq.rotate(i);

        t.put( key, (Object[])images );
    } // addRotated360Image

    // key   - name of font
    // value - location of font.
    // adds a font with a specified size to the table.
    public void addFont( String key, Object value, float size ) {
        if ( t.containsKey( key ) )
            return;
            
        Font tfont = null;

        try {
            tfont = Font.createFont( Font.TRUETYPE_FONT, Resources.class.getResource((String)value).openStream() );
        } catch ( IOException ioe ) {
            System.out.println( "GOOD JOB: " + key + " doesn't exist." );
        } catch ( FontFormatException ffe ) {
            System.out.println( "GOOD JOB: " + key + " doesn't exist." );
        } // catch

        t.put( key, tfont.deriveFont( size ) );
    } // addFont

    // key  - name of resource
    // name - name of font the OS knows of.
    // type - bold, italic, AWESOME?
    // size - size
    public void addFont( String key, String name, int type, int size ) {
        if ( t.containsKey( key ) )
            return;

        t.put( key, new Font( name, type, size ) );
    } // addFont

    // Add an image to the table
    public void addImage( String key, Object value ) {
        if ( t.containsKey( key ) )
            return;

        BufferedImage simg = null;
        try {
            //simg = ImageIO.read( new File( (String)value ) );
            simg = ImageIO.read( Resources.class.getResource((String)value) );
        } catch ( IOException ioe ) {
            System.out.println( "GOOD JOB: " + key + " doesn't exist." );
        } // catch
        
        // Give this image the same bit depth as the screen.
        GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        Image image = gc.createCompatibleImage( simg.getWidth(), simg.getHeight(), Transparency.TRANSLUCENT );
        image.getGraphics().drawImage(simg, 0, 0, null);
        t.put( key, new Sprite(simg) );
    } // addImage
    
    // return an object.
    public Object getResource( String key ) {
        if ( t.containsKey( key ) )
            return t.get(key);
        System.out.println( key + ": does not exist!" );
        return null;
    } // getResource
} // Resources