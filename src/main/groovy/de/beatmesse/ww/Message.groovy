package de.beatmesse.ww
/**
 * Class Message
 */

/**
 * @author Juergen
 *
 */
class Message {
	/** Constant for upper stage limit. */
	static final int C_STAGE_LIMIT = 1
    /** Constant for SMS source. */
    static final String C_SMS = "SMS"
    /** Constant for TWITTER source. */
    static final String C_TWITTER = "TWITTER"

	int id
	String source
	String sender
	String message
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return super.toString() + "[ id:${id}, source:${source}, sender:\"${sender}\", message:\"${message}\" ]";
	}
    
    public void setSource(String aSource) {
        assert aSource == C_SMS || aSource == C_TWITTER, "Invalid source string ${aSource} given"
        source = aSource
    }
	

}
