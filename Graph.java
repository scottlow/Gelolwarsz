/////////////////////////////////////////////////////////
//
//  GEOLOLWARZ - Graph.java
//  Dylan Johnston
//  Stephen Drake
//  Scott Low
//
//  A real time graph.

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;

class Graph {
    ArrayList points;
    ArrayList toRemove;
    int x, y, w, h;
    int pos = 0;

    public Graph( int x, int y ) {
        this.w = 150;
        this.h = 70;
        this.x = x - w;
        this.y = y - h;

        points = new ArrayList();
        toRemove = new ArrayList();
    }
    
    public void draw( Graphics2D g ) {
        Object[] ps = points.toArray();
        
        for ( int i = 1, n = ps.length; i < n; i++ ) {
            if ( ((Point)ps[i]).x >= pos-w ) {
                //g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); //
                //g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY ); //
                //g.fillRect(pos - ((Point)p[i]).x, 600-((Point)p[i]).y, 1, 1);
                g.drawLine( 
                    x + pos - ((Point)ps[i-1]).x,
                    y + h - (((Point)ps[i-1]).y/2),
                    x + pos - ((Point)ps[i]).x,
                    y + h - (((Point)ps[i]).y/2)
                );
            } else {
                toRemove.add( ps[i] );
            }
        }
        
        g.setColor( Color.white );
        g.drawRect( x, y, w, h );

        ps = toRemove.toArray();
        for ( int i = 0, n = ps.length; i < n; i++ )
            points.remove( ps[i] );

        toRemove.clear();
    }
    
    public void update( long y ) {
        points.add( new Point(pos++, (int)y) );
    }
}