package de.beatmesse.ww

import org.apache.log4j.Logger
import twitter4j.*

class TwitterStatusListener implements UserStreamListener {
    
    static Logger logger = Logger.getLogger(TwitterStatusListener.class)
    static Database myDb = new Database()    
    @Override
    public void onDeletionNotice(StatusDeletionNotice arg0) {
        logger.info "onDeletionNotice()"
   }
    @Override
    public void onScrubGeo(long arg0, long arg1) {
        logger.info "onScrubGeo()"
    }
    @Override
    public void onStatus(Status status) {
        logger.info "Received a tweet: ${status.user.screenName}: ${status.text}" 
        def msg = new Message(source:Message.C_TWITTER, sender:status.user.screenName, message:status.text)
        myDb.insertMessage(msg)
    }
    @Override
    public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
        logger.warn "onTrackLimitationNotice(${numberOfLimitedStatuses})"
    }
    @Override
    public void onException(Exception ex) {
        logger.info ex.printStackTrace()
    }
    @Override
    public void onBlock(User arg0, User arg1) {
        logger.info "onBlock()"
    }
    @Override
    public void onDeletionNotice(long arg0, long arg1) {
        logger.info "onDeletionNotice()"
    }
    @Override
    public void onDirectMessage(DirectMessage msg) {
        logger.info "Received direct message ${msg.senderScreenName}: ${msg.text}"
    }
    @Override
    public void onFavorite(User arg0, User arg1, Status arg2) {
        logger.info "onFavorite()"
    }
    @Override
    public void onFollow(User arg0, User arg1) {
        logger.info "onFollow()"
    }
    @Override 
    public void onFriendList(long[] arg0) {
        logger.info "onFriendList()"
    }
    @Override
    public void onRetweet(User arg0, User arg1, Status arg2) {
        logger.info "onRetweet()"
    }
    @Override
    public void onUnblock(User arg0, User arg1) {
        logger.info "onUnblock()"
    }
    @Override
    public void onUnfavorite(User arg0, User arg1, Status arg2) {
        logger.info "onUnfavorite()"
    }
    @Override
    public void onUserListCreation(User arg0, UserList arg1) {
        logger.info "onUserListCreation()"
    }
    @Override
    public void onUserListDeletion(User arg0, UserList arg1) {
        logger.info "onUserListDeletion()"
    }
    @Override
    public void onUserListMemberAddition(User arg0, User arg1, UserList arg2) {
        logger.info "onUserListMemberAddition()"
    }
    @Override
    public void onUserListMemberDeletion(User arg0, User arg1, UserList arg2) {
        logger.info "onUserListMemberDeletion()"
    }
    @Override
    public void onUserListSubscription(User arg0, User arg1, UserList arg2) {
        logger.info "onUserListMemberDeletion()"
    }
    @Override
    public void onUserListUnsubscription(User arg0, User arg1, UserList arg2) {
        logger.info "onUserListUnsubscription()"
    }
    @Override
    public void onUserListUpdate(User arg0, UserList arg1) {
        logger.info "onUserListUpdate()"
    }
    @Override
    public void onUserProfileUpdate(User arg0) {
        logger.info "onUserProfileUpdate()"
    }
    
    
}
