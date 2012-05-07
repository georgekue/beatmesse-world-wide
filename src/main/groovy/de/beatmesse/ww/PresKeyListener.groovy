package de.beatmesse.ww

import java.awt.event.KeyEvent
import java.awt.event.KeyListener

import org.apache.log4j.Logger

public class PresKeyListener implements KeyListener {
    static Logger logger = Logger.getLogger(PresKeyListener.class)
    //Presenter presenter = null
    FadeableLabel label = null
    
    /** C'tor. */
    //public PresKeyListener(Presenter pres, FadeableLabel label) {
    //    aPresenter = pres
    //    aLabel = label
    //}
    
    /** Handle the key typed event from the text field. */
    public void keyTyped(KeyEvent e) {
        displayInfo(e, "KEY TYPED: ")
        aPresenter.getSetNextMessage(aLabel)
    }
    
    /** Handle the key-pressed event from the text field. */
    public void keyPressed(KeyEvent e) {
        //displayInfo(e, "KEY PRESSED: ")
    }
    
    /** Handle the key-released event from the text field. */
    public void keyReleased(KeyEvent e) {
        //displayInfo(e, "KEY RELEASED: ")
    }
    
    private void displayInfo(KeyEvent e, String keyStatus){
        
        //You should only rely on the key char if the event
        //is a key typed event.
        int id = e.getID()
        String keyString
        if (id == KeyEvent.KEY_TYPED) {
            char c = e.getKeyChar()
            keyString = "key character = '" + c + "'"
        } else {
            int keyCode = e.getKeyCode()
            keyString = "key code = " + keyCode + " (" + KeyEvent.getKeyText(keyCode) + ")"
        }
        
        int modifiersEx = e.getModifiersEx()
        String modString = "extended modifiers = " + modifiersEx
        String tmpString = KeyEvent.getModifiersExText(modifiersEx)
        if (tmpString.length() > 0) {
            modString += " (" + tmpString + ")"
        } else {
            modString += " (no extended modifiers)"
        }
        
        String actionString = "action key? "
        if (e.isActionKey()) {
            actionString += "YES"
        } else {
            actionString += "NO"
        }
        
        String locationString = "key location: "
        int location = e.getKeyLocation()
        if (location == KeyEvent.KEY_LOCATION_STANDARD) {
            locationString += "standard"
        } else if (location == KeyEvent.KEY_LOCATION_LEFT) {
            locationString += "left"
        } else if (location == KeyEvent.KEY_LOCATION_RIGHT) {
            locationString += "right"
        } else if (location == KeyEvent.KEY_LOCATION_NUMPAD) {
            locationString += "numpad"
        } else { // (location == KeyEvent.KEY_LOCATION_UNKNOWN)
            locationString += "unknown"
        }
        
        logger.info "Key info: ${keyString}, ${modString}, ${actionString}, ${locationString}"
    }
}
