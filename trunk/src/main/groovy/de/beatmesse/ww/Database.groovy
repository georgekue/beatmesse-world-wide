package de.beatmesse.ww

import org.apache.log4j.Logger;

import groovy.sql.GroovyRowResult 
import groovy.sql.Sql

class Database {
	
	/** Our logger. */
	static Logger logger = Logger.getLogger(Database.class)
	/** Default JDBC connection string. */
	static final String C_JDBC_CONN_STRING = "jdbc:hsqldb:hsql://localhost/msg"
	/** JDBC user ID. */
	static final String C_JDBC_CONN_USER_ID = "SA"
	/** JDBC user password. */
	static final String C_JDBC_CONN_USER_PW = "joki!"
	/** JDBC class name for connection. */
	static final String C_JDBC_CLASSNAME = "org.hsqldb.jdbc.JDBCDriver"
	/** Maximum input length of message, should be equal to size of message column in mesg table. */
	static final int C_MAX_LEN_IN = 2048
    /** Maximum output length of message. */
    static final int C_MAX_LEN = 8*160

	/** JDBC connection instance. */
	private Sql dbConn
	
	/**
	 * C'tor. Opens DB connection.
	 */
	public Database() {
		String jdbcConnString = null
		
		// Read JDBC string from database.properties, if available.
		def prop = new Properties()
		def inputStream = null
		
		try {
			inputStream = new FileInputStream("database.properties")
			prop.load(inputStream)
		} catch (IOException e) {
			// ignore
		} finally {
			if (null != inputStream) {
				try {
					inputStream.close()
				} catch (IOException e) {
					//ignore
				}
			}
		}
		
		jdbcConnString = prop.getProperty("jdbcConnString")
		if (jdbcConnString == null) {
			jdbcConnString = C_JDBC_CONN_STRING
		}
		
		logger.info "Opening JDBC connection to '${jdbcConnString}'."
		
		dbConn = Sql.newInstance(jdbcConnString, 
		C_JDBC_CONN_USER_ID,
		C_JDBC_CONN_USER_PW, 
		C_JDBC_CLASSNAME)
		
		// Add truncate method to String class.
		//String.metaClass.truncate = { len ->
		//	if (delegate == null) { return ''}
		//	if (delegate.length() > len) { return delegate[0..(len-4)] + '...' }
		//}
		
	}
	
	/**
	 * Print a list of all messages in the database.
	 */
	public void printAllMesgs() {
		dbConn.eachRow("select * from mesg", { println it.id + " -- ${it.cre_stamp} -- ${it.last_display_stamp} -- ${it.displaycnt} -- ${it.source} -- ${it.sender} -- ${it.message} --"} )
	}
	
	/**
	 * Insert a message into the database.
	 * @param aMessage the message object. 
	 */
	public void insertMessage (Message aMessage) {
		StringBuilder sb = new StringBuilder(aMessage.message)
		if (sb.length() > C_MAX_LEN_IN)
		{
			sb.setLength C_MAX_LEN_IN
		}
		dbConn.execute("insert into mesg (id, cre_stamp, last_display_stamp, displaycnt, source, sender, message) " +
		" values (DEFAULT, CURRENT_TIMESTAMP, null, -1, ?, ?, ?)", 
		[aMessage.source, aMessage.sender, sb.toString()])
	}
	
	/**
	 * Get message with id.
	 * @param id Id of message to retrieve.
	 * @return Message Object with contents of message.
	 */
	public Message getMessage(int id) {
		Message ret = null
		GroovyRowResult row = dbConn.firstRow("select * from mesg where id = ?", [ id ])
		if ( row != null) {
			String aSource = row.source
			StringBuilder sb = new StringBuilder (row.message)
			if (sb.length() > C_MAX_LEN)
			{
				sb.setLength C_MAX_LEN
			}
			ret = new Message(id: row.id, source: aSource, sender: row.sender, message: sb.toString())
		}
		return ret
	}
	
	/**
	 * Get next appropriate message in the current stage, i.e. the number of times a message was displayed.
	 * Upper limit of stage is C_STAGE_LIMIT, starting from 0.
	 * @param stage Current stage [0..C_STAGE_LIMIT].
	 * @return the message.
	 */
	public Message getNextMessageAndMarkDisplayed(int stage) {
		assert(stage > - 1 && stage <= Message.C_STAGE_LIMIT )
		Message ret = null
		GroovyRowResult row = dbConn.firstRow("select id from mesg t1 where t1.displaycnt = ? order by t1.last_display_stamp, t1.cre_stamp",
		[ stage ])
		if (row != null) {
			ret = getMessage (row.id)
			dbConn.execute ("Update mesg set displaycnt = ?, last_display_stamp = CURRENT_TIMESTAMP where id = ? ", 
			[ (stage >= Message.C_STAGE_LIMIT) ? Message.C_STAGE_LIMIT : stage + 1 , row.id ])
		}
		return ret
	}
	
	/**
	 * Get Oauth consumer key from config table.
	 * @return a string containing the Oauth consumer key.
	 */
	public String getOauthConsumerKey() {
		String ret = null
		GroovyRowResult row = dbConn.firstRow("select value from conf where attr = 'consumer_key'")
		if (row != null) {
			ret = row.value
		}
		return ret
	}
	
	/**
	 * Get Oauth consumer secret from config table.
	 * @return a string containing the Oauth consumer secret.
	 */
	public String getOauthConsumerSecret() {
		String ret = null
		GroovyRowResult row = dbConn.firstRow("select value from conf where attr = 'consumer_secret'")
		if (row != null) {
			ret = row.value
		}
		return ret
	}
	
	/**
	 * Get Oauth access token from config table.
	 * @return a string containing the Oauth access token.
	 */
	public String getOauthToken() {
		String ret = null
		GroovyRowResult row = dbConn.firstRow("select value from conf where attr = 'oauth_token'")
		if (row != null) {
			ret = row.value
		}
		return ret
	}
	
	/**
	 * Get Oauth access token secret from config table.
	 * @return a string containing the Oauth access token secret.
	 */
	public String getOauthTokenSecret() {
		String ret = null
		GroovyRowResult row = dbConn.firstRow("select value from conf where attr = 'oauth_token_secret'")
		if (row != null) {
			ret = row.value
		}
		return ret
	}
	
	/**
	 * Get display delay from config table. Note that the value is given in [seconds]
	 * in the databse, so we have to multiply this by 1000.
	 * @return n integer specifying the Timer delay in milliseconds, defaulting to 10000.
	 */
	public int getDisplayDelay() {
		int ret = 10000
		GroovyRowResult row = dbConn.firstRow("select value from conf where attr = 'display_delay'")
		if (row != null) {
			ret = Integer.parseInt(row.value) * 1000
		}
		return ret
	}
	
	/**
	 * Store access token in config database.
	 * @param accessTokenString The access token string.
	 */
	public void setOauthToken( String accessTokenString ) {
		dbConn.execute("update conf set value = ? where attr = 'oauth_token'", [ accessTokenString ])
	}    
	
	/**
	 * Store access token secret in config database.
	 * @param accessTokenSecretString The access token secret string.
	 */
	public void setOauthTokenSecret( String accessTokenSecretString ) {
		dbConn.execute("update conf set value = ? where attr = 'oauth_token_secret'", [ accessTokenSecretString ])
	}
	
	
	/**
	 * Main routine for testing.
	 * @param args Standard arguments.
	 */
	static main(args) {
		Database myDb = new Database()
		myDb.insertMessage new Message(source:Message.C_SMS, sender:"+4915150523868", message:"Neue Objekte.")
		myDb.insertMessage new Message(source:Message.C_TWITTER, sender:"+4915150523868", message:"Neue Objekte.")
		myDb.printAllMesgs()
		println myDb.getMessage(4)
		int stage = 0
		while (stage >= 0) {
			Message m = myDb.getNextMessageAndMarkDisplayed(stage)
			if (m != null) {
				println m
			} else {
				++stage
				if (stage >= Message.C_STAGE_LIMIT) {
					stage = 0
				}
			}
		}
	}
}
