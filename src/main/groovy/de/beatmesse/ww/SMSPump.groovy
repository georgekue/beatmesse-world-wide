/**
 * SMS Pump. This class is called by SMS server on reception of new SMS messages 
 * and stores them in the database.
 */
package de.beatmesse.ww

import org.apache.log4j.Logger

import java.io.File;
import java.util.Properties;
import java.util.regex.Matcher


/**
 * @author Juergen
 *
 */
class SMSPump {
    static Logger logger = Logger.getLogger(SMSPump.class)
    private Database myDb = new Database()
    private String sender
    private String message
    private String msg_id
    private File theFile
    private def map = [:]
    private static final String C_DIR_OUT_NAME = "C:/cygwin/var/spool/sms/outgoing/"
	private boolean m_notify = false
	private String m_dirOutName
    
    /**
     * C'tor.
     * @param aFilename the filename to parse.
     */
    SMSPump(String aFilename) {
        assert aFilename != null
        assert aFilename.length() > 0
        
		readProps()
        parseFile(aFilename)
        storeMessage()
        sendNotification()
    }
    
    /**
     * Parse SMS incoming file.
     * @param aFilename Full path of the file.
     */
    private void parseFile(String aFilename)
    {
        theFile = new File(aFilename)
        logger.info "Parsing SMS file '${theFile.getName()}'."
        def isLastLine = false
        def lastLine = new StringBuilder()
        // Parse SMS file and store all information in map
        theFile.eachLine {
            if (!isLastLine){
                if ( it.toString().length() > 0) {
                    Matcher matcher = it =~ "^(.*): (.*)"
                    map.put matcher[0][1], matcher[0][2]
                } else {
                    isLastLine = true // SMS content is marked by an empty line before
                }
            } else {
                // all remaining lines must be content - collect it.
                lastLine.append(it.toString()).append(" ")
            }
        }
        if (lastLine.length() > 1) {
            lastLine.deleteCharAt( lastLine.length() -1 ) // delete last space
            map.put "Content", lastLine
            logger.info "Received message \'${map['Content']}\'."
        } else {
            logger.info "Received empty message."
        }
    }
    
    /**
     * Store message in database.
     */
    private void storeMessage() {
        def msg = new Message()
        msg.source = Message.C_SMS
        msg.sender = map["From"]
        msg.message = map["Content"]
        myDb.insertMessage msg
        logger.info "Stored message ${msg}."
    }
    
	/**
	 * Read properties.
	 */
	void readProps() {
		private Properties prop = new Properties()
		
        InputStream smsprops = SMSPump.class.classLoader.getResourceAsStream("smspump.properties")
        if (smsprops != null) {
            prop.load (smsprops)
        
			String notify = prop.getProperty("notify")
			if (notify != null) {
				if ("true" == notify) {
				m_notify = true
				}
			}
		
			m_dirOutName = prop.getProperty("dir.outgoing")
			if (m_dirOutName == null) {
				m_dirOutName = C_DIR_OUT_NAME
			}
		}
	}

    /**
     * Send notification message.
     */
    private void sendNotification() {
		if (m_notify == true) {
			File dirOut = new File(m_dirOutName)
			File outFile = File.createTempFile("sms", ".txt", dirOut)
			assert outFile != null
			String recipient = map["From"]
        
			outFile.withWriter('utf8') { out ->
				out.write("To: " + recipient + "\n")
				out.write("\n")
				out.write("Vielen Dank für Deinen SMS-Beitrag zur Beatmesse, und herzliche Grüße ")
				out.write("vom Beatmessen-Team. Mehr über uns unter http://beatmesse.de.")
				}
			logger.info "Wrote notification message for SMS recipient ${recipient}."
		}
    }

    /**
     * The main is called by SMS server in an event handler.
     * @param args Two parameters are passed here, one being a constant, of which only "RECEIVE"
     * is of interest here, the other is the name of a plain file containing the message.
     */
    static main(args) {
        try {
            assert args.size() == 2
        
            if (args[0] == "RECEIVED") {
                def pump = new SMSPump(args[1])
            }
        } catch (Throwable e) {
            SMSPump.logger.error("An exception occurred: ", e)
        }
        System.exit(0)
        
    }
    
}
