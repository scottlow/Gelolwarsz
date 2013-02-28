/////////////////////////////////////////////////////////
//
//  GEOLOLWARZ - StarScape.java
//  Dylan Johnston
//  Stephen Drake
//  Scott Low
//
//  Draws a "star" background. Seems retardedly slow...

import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.util.*;

class StarScape {
    private ArrayList points1;
    private ArrayList points2;
    private ArrayList points3;
    private BufferedImage stars1;
    private BufferedImage stars2;
    private BufferedImage stars3;
    private int ox; // y offset
    private int oy; // y offset

    private int lightest;
    private int lighter;
    private int light;

    // "creates" a background.
    public StarScape( int sw, int sh, GraphicsConfiguration gc ) {
    
        // various shades of grey
        lightest = new Color( 150, 150, 150 ).getRGB();
        lighter = new Color( 100, 100, 100 ).getRGB();
        light = new Color( 75, 75, 75 ).getRGB();

        //lightest = Color.red.getRGB();
        //lighter = Color.blue.getRGB();
        //light = Color.green.getRGB();
        
        // different sizes cause we scoll @ different speeds.
        stars1 = gc.createCompatibleImage( sw + (sw/2),  sh + (sh/2),  Transparency.OPAQUE );
        stars2 = gc.createCompatibleImage( sw + (sw/5),  sh + (sh/5),  Transparency.OPAQUE );
        stars3 = gc.createCompatibleImage( sw + (sw/10), sh + (sh/10), Transparency.OPAQUE  );
        
        // draw.
        for ( int i = 0; i < 200; i ++ ) {
            stars1.setRGB( (int)(Math.random() * stars1.getWidth()), (int)(Math.random() * stars1.getHeight()), light );
            stars2.setRGB( (int)(Math.random() * stars2.getWidth()), (int)(Math.random() * stars2.getHeight()), lighter );
            stars3.setRGB( (int)(Math.random() * stars3.getWidth()), (int)(Math.random() * stars3.getHeight()), lightest );
        }

    }
    
    // changes the offset.
    public void update( int x, int y ) {
        ox = -x/5;
        oy = -y/5;
    }
    
    // draws some stars!
    public void draw( Graphics2D g, ImageObserver ob ) {

            //g.setColor( new Color( 150, 150, 150 ) );
            g.drawImage( stars3, (ox/2), (oy/2), ob );
            g.drawImage( stars2, (ox/5), (oy/5), ob );
            g.drawImage( stars3, (ox/10), (oy/10), ob );
            //g.drawRect( (ox/2)+((Point)points1.get(i)).x, (oy/2)+((Point)points1.get(i)).y, 1, 1 );
            //g.setColor( new Color( 100, 100, 100 ) );
           // g.drawRect( (ox/5)+((Point)points2.get(i)).x, (oy/5)+((Point)points2.get(i)).y, 1, 1 );
            //g.setColor( new Color( 75, 75, 75 ) );
            //g.drawRect( (ox/10)+((Point)points3.get(i)).x, (oy/10)+((Point)points3.get(i)).y, 1, 1 );
    }
}