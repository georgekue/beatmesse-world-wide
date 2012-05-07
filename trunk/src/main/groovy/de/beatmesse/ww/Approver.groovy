/**
 * Approver. GUI application for approving messages.
 */
package de.beatmesse.ww

import org.apache.log4j.Logger
import groovy.swing.SwingBuilder
import java.awt.BorderLayout as BL
import javax.swing.*
import java.awt.*
import java.awt.event.*

/**
 * @author Juergen
 *
 */
class Approver {
    
    static Logger logger = Logger.getLogger(Approver.class)
    private Database myDb = new Database()
    
    Approver() {
        
    }
    
    /**
     * Closure to be executed in the Event Dispatch Thread.
     */
    def edtClosure = { 
        frame(title:'Frame', 
        size:[300,300], 
        show: true, 
        //undecorated: true, 
        extendedState: Frame.MAXIMIZED_BOTH,
        defaultCloseOperation: JFrame.DISPOSE_ON_CLOSE) {
            borderLayout()
        }
    } // edtClosure

        static main(args) {
        try {
            def swing = new SwingBuilder()
            def appr = new Approver()
            swing.edt (appr.edtClosure)
            
        } catch (Throwable e) {
            Approver.logger.error("An exception occurred: ", e)
        }
        
    }
    
}
