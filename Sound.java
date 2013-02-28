/***************************************************************************************
* Name:        ScreenManager
* Author:      Dylan Johnston, Stephen Drake, Scott Low
* Date:        April 13, 2009
* Purpose:     Manages sounds
****************************************************************************************/

import javax.sound.sampled.*;

class Sound {
    private Clip[] ac;
    private float  volume;
    private FloatControl[] vc;

   /* Constructor
    * input:
    *     - s: Sound clip
    *     - v: volume
    * purpose: Draw a string of text in the middle of the screen
    */
    public Sound( Clip[] s, float v ) {
        this.ac = s;
        this.vc = new FloatControl[ac.length];
        this.volume  = 2f;
        for ( int i = 0; i < ac.length; i++ ) {
             // Minimum ( mute ) = -80
             // Max     ( blow your mind mode ) = 6.0206
             // Normal  = 0;
             this.vc[i] = (FloatControl) ac[i].getControl( FloatControl.Type.MASTER_GAIN  );
             this.vc[i].setValue( -80 + ( v * 80 ) ); // scale volume from a non-retard range to a dB range.
        } // for
    } // Constructor

   /* play
    * purpose: Plays a sound
    */
    public void play() {
        for ( int i = 0; i < ac.length; i++ ) {
            if ( !ac[i].isActive() ) {
                ac[i].setFramePosition(0);
                ac[i].start();
                break;
            } // if
        } // for
    } // play
} // Sound