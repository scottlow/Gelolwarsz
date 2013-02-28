/***************************************************************************************
* Name:        Highscore
* Author:      Dylan Johnston, Stephen Drake, Scott Low
* Date:        April 13, 2009
* Purpose:     Handles the highscores
****************************************************************************************/

import java.net.*;
import java.io.*;
import java.security.*;
import java.awt.*;

class Highscore {
    private String        clist; // current scores.
    private MessageDigest md5;   // md5 hash of our string.
    private StringBuffer  sb;    // copy of a sb;
    private int           sw;    // Screen width
    private int           sh;    // Screen height
    private int           nl;    // Number of lines displayed

    // Constructor
    public Highscore() {
        clist = "";
        sw = Resources.get().getWidth();
        sh = Resources.get().getHeight();
        try {
            md5 = java.security.MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            System.out.println( "ALERT! CRIPPLED JAVA! ABORT ABORT!" );
        } // catch
    } // Constructor
    
    // Draws the high scores
    public void drawHighscores( Graphics g, int x, int y ) {
        String line = "";
        int lines = 0;
        String name;
        String score;
        g.setFont( (Font)Resources.get().getResource( "silkscreen" ) );
        for ( int i = 0; i < clist.length(); i++ ) {
            if ( clist.charAt(i) == '\n' ) {
                if ( line.length() == 0 )
                    continue;
                
                score = line.split(" Ç ")[0];
                name = line.split(" Ç ")[1];
                g.drawString( (nl-lines-1)+" " + score, sw-x-200, sh - y - (lines * 11) );
                g.drawString( name, sw-x - g.getFontMetrics().stringWidth( name ) - 5, sh - y - (lines * 11) );
                line = "";
                lines++;
            } else {
                line += clist.charAt(i);
            } // else
        } // for
        g.drawString( "Highscores: (R to refresh)", sw-200, sh - y - (lines * 11) );
    } // drawHighscores

    // Grabs the scores from the internet
    public void grabscores() {
        nl = 0;
        try {
            String url = "http://dylanj.ca/geo.php";

            BufferedReader in = new BufferedReader( new InputStreamReader( new URL( url ).openStream() ) );
    
            String str = clist = "";
            while ((str = in.readLine()) != null) {
                clist += str + '\n';
                nl++;
            } // while

            in.close();
        } // try
        catch (MalformedURLException e)    {}
        catch (IOException e)              {}
    } // grabscores

    // ezmode: submits a score to a highscoretable
    // hardmode: creates a hash of the string with 1's appended to each end
    //           to avoid simple url guessing since anyone wanting to manually
    //           add scores to the db will have to decompile the source
    //           after we create a hash we convert it to a string by appending a byte
    //           to a string builder ( sure, string += works ) THEN we "set up" the url
    //           and fire off the request. We then read the result in a BufferedReader
    //           and save the contents ( which is the highscore table ) into clist.
    //           We also make pigs fly in this method as well.
    public void submit( String name, int score ) {
        String hash = '1' + Integer.toString(score) + '1';
        nl = 0;
        try {
            md5.update( hash.getBytes("iso-8859-1"), 0, hash.length() );
            byte[] b = md5.digest();

            sb = new StringBuffer();
            for ( int i = 0; i < b.length; i++ ) {
                String h = Integer.toHexString( 0xFF & b[i] ); // AFAIK 1111111 & 1010101 = 1010101. But i guess not?
                if( h.length() == 1 )
                    sb.append('0');
                sb.append(h);
            } // for
            
            hash = sb.toString();
            String url = "http://dylanj.ca/geo.php?do=sub&n=" + name + "&s=" + score + "&h=" + hash;
            url = url.replace( " ", "%20" ); //HTTP YAKNO?
            System.out.println( url ); // debuggle.

            BufferedReader in = new BufferedReader( new InputStreamReader( new URL( url ).openStream() ) );
            
            // clear clist
            String str = clist = "";
            while ((str = in.readLine()) != null) {
                clist += str + '\n';
                nl++;
            } // while
            System.out.println( clist );
            in.close();
        }
        catch (MalformedURLException e) {}
        catch (IOException e)           {}
    } // submit
} // Highscore