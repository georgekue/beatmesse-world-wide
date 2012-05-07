package de.beatmesse.ww

import org.apache.log4j.Logger
import groovy.swing.SwingBuilder
import java.awt.BorderLayout as BL
import javax.swing.*
import java.awt.*
import java.awt.event.*

/**
 * Class for presenting BeaMe message son the screen.
 * 
 * @author Juergen
 */
class Presenter {
	
	static Logger logger = Logger.getLogger(Presenter.class)
	static Database myDb = new Database()
	
	static final String C_TEXT_HTML_PRE  = "<html><body style=\"font-family:Tahoma,Arial,Helvetica; text-align:center\">" 
	static final String C_TEXT_HTML_POST = "</body></html>"
	
	static ArrayList order = new ArrayList()
	static Iterator order_it 
	
	def mainFrame
	FadeableLabel textlabel
	javax.swing.Timer mainTimer
	ImageIcon logo = new ImageIcon(getClass().getResource("images/beatmesse.jpg"))
	
	// create the listener
	def closureMap = [
	mousePressed:     {  },
	keyTyped:       { },
	keyPressed:       { doKeyPress(it) },
	keyReleased:       {  },
	focusLost:        { },
	windowIconified:  {  },
	actionPerformed:  { getSetNextMessage() }
	]
	def interfaces = [KeyListener, ActionListener]
	def listener = ProxyGenerator.instantiateAggregate(closureMap, interfaces)
	
	/**
	 * Static initializer.
	 */
	static {
		for (int i = Message.C_STAGE_LIMIT; i > 0; i--) {
			for (int n = 0; n < Message.C_STAGE_LIMIT - i + 1; n++) {
				order.add(i)
			}
		}
		order_it = order.iterator()
	}   
	
	/**
	 * Evaluate a keypress. 
	 * @param e A key event.
	 */
	void doKeyPress(KeyEvent e) {
		assert e.id != KeyEvent.KEY_TYPED
		switch (e.keyCode) {
			case 32: // space key: show next message, stop timer
				getSetNextMessage()
				mainTimer.stop()
				break
			case 84: // 't'; start main timer
			// Update timer delay - this could have changed in the meantime.
				mainTimer.setDelay(myDb.getDisplayDelay())
				mainTimer.setInitialDelay(myDb.getDisplayDelay())
				mainTimer.start()
				break
			case 83: // 's'; stop main timer
				mainTimer.stop()
				break
			case 76: // 'l'; show logo; stop main timer
				setIcon()
				mainTimer.stop()
				break
			case 27: // ESC; terminate application
				System.exit 0
				break
			case 122: // F11: toggle full screen
			default:
				logger.info "Ignoring keypress with code '${e.keyCode}'."
		}
	}
	
	/**
	 * Display beatmesse logo, remove text.
	 */
	void setIcon() {
		textlabel.setIcon(logo)
		textlabel.text = ""
	}
	
	/**
	 * Set font size for a label.
	 * @param aLabel Label to change its font size.
	 */
	void setFontSize(JLabel aLabel, String plainText)
	{
		//Font labelFont = aLabel.getFont();
		Font aFont = new Font("SansSerif", Font.PLAIN, 10)
		String labelText = aLabel.getText()
		
		JLabel tempLabel = new JLabel()
		int stringWidth = tempLabel.getFontMetrics(aFont).stringWidth(plainText)
		int fontHeigth = aFont.getSize()
		int componentWidth = aLabel.getWidth()
		int componentHeight = aLabel.getHeight()
		
		// Find out how much the font can grow in width.
		double ratio = Math.sqrt (((double) componentWidth * (double) componentHeight) / ((double) stringWidth * (double) fontHeigth))
		
		int newFontSize = (int)(fontHeigth * ratio/1.5)
		
		// Pick a new font size so it will not be larger than the height of label.
		int fontSizeToUse = Math.min(newFontSize, componentHeight)
		
		// Set the label's font size to the newly determined size.
		aLabel.setFont(new Font("SansSerif", Font.PLAIN, fontSizeToUse))
	}
	
	/**
	 * Get next message from database and set text in label. Remove icon.
	 * @param aLabel Label to update with the message.
	 */
	void getSetNextMessage () {
		def found = false
		def iteratorResetted = false
		def aMsg = myDb.getNextMessageAndMarkDisplayed(0)
		textlabel.icon = null
		if (aMsg != null) {
			fadeLabel(textlabel)
			textlabel.text = C_TEXT_HTML_PRE + aMsg.message + C_TEXT_HTML_POST
			setFontSize(textlabel, aMsg.message)
		} else {
			found = false
			while (!found) {
				if (order_it.hasNext()) {
					int stage = order_it.next()
					aMsg = myDb.getNextMessageAndMarkDisplayed(stage)
					if (aMsg != null) {
						fadeLabel(textlabel)
						textlabel.text = C_TEXT_HTML_PRE + aMsg.message + C_TEXT_HTML_POST
						setFontSize(textlabel, aMsg.message)
						found = true
					} // if
				} else {
					if (iteratorResetted) {
						// Quit loop and wait for next timer event, because obviously, there's nothing to 
						// display right now.
						break
					} else {
						order_it = order.iterator()
						iteratorResetted = true
					}
				} // if
			} // while
		} // if
		
	} // getSetNextMessage()
	
	/**
	 * Get message from database and show it on the screen. Do this every this and that seconds,
	 * as specified in the database config table.
	 * @param aLabel Label to update with the message.
	 */
	private void setupTimer() {
		
		mainTimer = new Timer(myDb.getDisplayDelay(), null)
		mainTimer.addActionListener listener
		
	} // updateMessage
	
	/**
	 * Fade in a Fadeable Label.
	 * @param aLabel The label to fade in.
	 */
	private void fadeLabel (FadeableLabel aLabel) {
		Timer timer = new Timer(100, null)
		
		int steps = 10
		aLabel.intensity = 0.0f
		
		timer.addActionListener(new ActionListener() {
					int count = 0
					
					public void actionPerformed(ActionEvent e) {
						if (count <= steps) {
							float intensity = count / (float) steps
							aLabel.intensity = intensity
							count++
						} else {
							timer.stop()
						}
					}
				})
		timer.start()
		
	} // fadeLabel
	
	/**
	 * Get graphics device with maximum x offset (i.e. second monitor).
	 * @return offset of 2nd screen.
	 */
	int getMaxGraphicsDevice() {
		int xmax = 0
		
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment()
		GraphicsDevice[] gs = ge.getScreenDevices()
		for (int j = 0; j < gs.length; j++) { 
			GraphicsDevice gd = gs[j]
			GraphicsConfiguration[] gc = gd.getConfigurations()
			for (int i=0; i < gc.length; i++) {
				Rectangle gcBounds = gc[i].getBounds()
				if (xmax != Math.max(gcBounds.x, xmax)) {
					xmax = Math.max(gcBounds.x, xmax)
				}
				//logger.info "Screen ${i}, x bound ${gcBounds.x}, y bound ${gcBounds.y}, GC ${gc[i]}."
			}
		}
		logger.info "Using x offset ${xmax}."
		return xmax
	}
	
	/**
	 * Closure to be executed in the Event Dispatch Thread.
	 */
	def edtClosure = { 
		frame(
		title:'Frame', 
		location: [getMaxGraphicsDevice(),0],
		size:[800,600], 
		show: true, 
		undecorated: true, 
		extendedState: Frame.MAXIMIZED_BOTH,
		defaultCloseOperation: JFrame.DISPOSE_ON_CLOSE) {
			borderLayout()
			//String labeltext = C_TEXT_HTML_PRE + "Great! Thanks! @Griff_Graff: that's 447 NEIA freshmen walking around knowing how to present EXCELLENTLY, no more \"Death by PowerPoint!\" laughw laughw laughw w" + C_TEXT_HTML_POST
			//String labeltext = C_TEXT_HTML_PRE + "Ihre Fürbitte in der Beatmesse: per SMS an 01638888065 oder per Twitter an @beatmesse." + C_TEXT_HTML_POST
			textlabel = widget(new FadeableLabel(),/*text: labeltext*/, 
			constraints: BL.CENTER, 
			verticalAlignment: SwingConstants.CENTER, 
			horizontalAlignment: SwingConstants.CENTER,
			foreground: Color.white,
			background: Color.black,
			font: new Font("SansSerif", Font.PLAIN, 72),
			opaque: true)
			setupTimer()
		} .addKeyListener listener
	} // edtClosure
	
	static main(args) {
		def swing = new SwingBuilder()
		def pres = new Presenter()
		swing.edt (pres.edtClosure)
	}
}


