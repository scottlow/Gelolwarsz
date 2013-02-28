/***************************************************************************************
* Name:        Sprite
* Author:      Dylan Johnston, Stephen Drake, Scott Low
* Date:        April 13, 2009
* Purpose:     Manages each sprite
****************************************************************************************/

import java.awt.Graphics;
import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;

public class Sprite {
    public Image image;

    //constructor
    public Sprite(Image i)  { image = i; }
    public Image getImage() { return image; }
    public int getWidth()   { return image.getWidth(null); }
    public int getHeight()  { return image.getHeight(null); }

    // a - angle we rotate to.
    // rotates an image to a specified angle.
    // IMPORTANT - slow, only call at the beginning.
    public Sprite rotate( int a ) {
        BufferedImage bi     = (BufferedImage)image;

        BufferedImage dest   = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage( bi.getWidth(null), bi.getHeight(null), Transparency.TRANSLUCENT );
        Graphics2D g2D       = dest.createGraphics();
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); //
        g2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY ); //

        AffineTransform orig = g2D.getTransform();
    	AffineTransform rot  = new AffineTransform();

        rot.setToIdentity();
    	rot.rotate( Math.toRadians( a ), bi.getWidth(null)/2 , bi.getHeight(null)/2 );

        g2D.transform( rot );
        g2D.setPaint( new TexturePaint( bi, new Rectangle2D.Float( 0, 0, bi.getWidth(null), bi.getHeight(null) ) ) ); //
        g2D.fillRect(0, 0, bi.getWidth(null), bi.getHeight(null)); //

        g2D.transform( orig );
        g2D.dispose();

        return new Sprite( dest );
    } // rotate
} // Sprite