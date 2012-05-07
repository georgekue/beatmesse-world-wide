package de.beatmesse.ww
/**
 * Twitter Pump. This class retrieves data from Twitter and stores them in the database.
 */

import org.apache.log4j.Logger
import twitter4j.auth.*
import twitter4j.*

/**
 * @author Juergen
 *
 */
class TwitterPump {
	
	static Logger logger = Logger.getLogger(TwitterPump.class)
	static Database myDb = new Database()
	
	
	
	/**
	 * C'tor.
	 */
	public TwitterPump() {
		
		boolean useStreaming = true
		
		logger.info "Opening Twitter connection."
		AccessToken accessToken = new AccessToken(myDb.getOauthToken(), myDb.getOauthTokenSecret())
		
		if (useStreaming) {
			TwitterStream twitterStream = new TwitterStreamFactory().getInstance(accessToken)
			twitterStream.addListener (new TwitterStatusListener())
			twitterStream.user("@beatmesse")
		} else {
			TwitterFactory factory = new TwitterFactory()
			Twitter twitter = factory.getInstance()
			twitter.setOAuthAccessToken(accessToken)
			ResponseList rl = twitter.getMentions()
			for (Status status : rl) {
				logger.info "Received a tweet: ${status.user.screenName}: ${status.text}" 
				def msg = new Message(source:Message.C_TWITTER, sender:status.user.screenName, message:status.text)
				myDb.insertMessage(msg)
			}
		}
	}
	
	
	
	/**
	 * The main program.
	 * @param args the standard arguments.
	 */
	static main(args) {
		
		TwitterPump tw = new TwitterPump()
		//System.exit(0)
		
	}
	
}

