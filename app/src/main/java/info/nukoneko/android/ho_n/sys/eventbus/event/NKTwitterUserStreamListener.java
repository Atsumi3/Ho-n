package info.nukoneko.android.ho_n.sys.eventbus.event;import android.util.Log;import info.nukoneko.android.ho_n.sys.eventbus.NKEventBusProvider;import twitter4j.DirectMessage;import twitter4j.StallWarning;import twitter4j.Status;import twitter4j.StatusDeletionNotice;import twitter4j.User;import twitter4j.UserList;import twitter4j.UserStreamListener;/** * Created by TEJNEK on 2016/10/22. */public final class NKTwitterUserStreamListener implements UserStreamListener {    private final long managingUserId;    public NKTwitterUserStreamListener(long userId) {        this.managingUserId = userId;    }    @Override    public void onDeletionNotice(long directMessageId, long userId) {        NKEventBusProvider.getInstance().sendTwi(                new OnStreamDeletionNoticeDirectMessage(managingUserId, directMessageId, userId)        );    }    @Override    public void onFriendList(long[] friendIds) {        NKEventBusProvider.getInstance().sendTwi(                new OnStreamFriendList(managingUserId, friendIds)        );    }    @Override    public void onFavorite(User source, User target, Status favoritedStatus) {        NKEventBusProvider.getInstance().sendTwi(                new OnStreamFavorite(managingUserId, source, target, favoritedStatus)        );    }    @Override    public void onUnfavorite(User source, User target, Status unfavoritedStatus) {        NKEventBusProvider.getInstance().sendTwi(                new OnStreamUnfavorite(managingUserId, source, target, unfavoritedStatus)        );    }    @Override    public void onFollow(User source, User followedUser) {        NKEventBusProvider.getInstance().sendTwi(                new OnStreamFollow(managingUserId, source, followedUser)        );    }    @Override    public void onUnfollow(User source, User unfollowedUser) {        NKEventBusProvider.getInstance().sendTwi(                new OnStreamUnfollow(managingUserId, source, unfollowedUser)        );    }    @Override    public void onDirectMessage(DirectMessage directMessage) {        NKEventBusProvider.getInstance().sendTwi(                new OnStreamDirectMessage(managingUserId, directMessage)        );    }    @Override    public void onUserListMemberAddition(User addedMember, User listOwner, UserList list) {        NKEventBusProvider.getInstance().sendTwi(                new OnStreamUserListMemberAddition(managingUserId, addedMember, listOwner, list)        );    }    @Override    public void onUserListMemberDeletion(User deletedMember, User listOwner, UserList list) {        NKEventBusProvider.getInstance().sendTwi(                new OnStreamUserListMemberDeletion(managingUserId, deletedMember, listOwner, list)        );    }    @Override    public void onUserListSubscription(User subscriber, User listOwner, UserList list) {        NKEventBusProvider.getInstance().sendTwi(                new OnStreamUserListSubscription(managingUserId, subscriber, listOwner, list)        );    }    @Override    public void onUserListUnsubscription(User subscriber, User listOwner, UserList list) {        NKEventBusProvider.getInstance().sendTwi(                new OnStreamUserListUnsubscription(managingUserId, subscriber, listOwner, list)        );    }    @Override    public void onUserListCreation(User listOwner, UserList list) {        NKEventBusProvider.getInstance().sendTwi(                new OnStreamUserListCreation(managingUserId, listOwner, list)        );    }    @Override    public void onUserListUpdate(User listOwner, UserList list) {        NKEventBusProvider.getInstance().sendTwi(                new OnStreamUserListUpdate(managingUserId, listOwner, list)        );    }    @Override    public void onUserListDeletion(User listOwner, UserList list) {        NKEventBusProvider.getInstance().sendTwi(                new OnStreamUserListDeletion(managingUserId, listOwner, list)        );    }    @Override    public void onUserProfileUpdate(User updatedUser) {        NKEventBusProvider.getInstance().sendTwi(                new OnStreamUserProfileUpdate(managingUserId, updatedUser)        );    }    @Override    public void onUserSuspension(long suspendedUser) {        NKEventBusProvider.getInstance().sendTwi(                new OnStreamUserSuspension(managingUserId, suspendedUser)        );    }    @Override    public void onUserDeletion(long deletedUser) {        NKEventBusProvider.getInstance().sendTwi(                new OnStreamUserDeletion(managingUserId, deletedUser)        );    }    @Override    public void onBlock(User source, User blockedUser) {        NKEventBusProvider.getInstance().sendTwi(                new OnStreamBlock(managingUserId, source, blockedUser)        );    }    @Override    public void onUnblock(User source, User unblockedUser) {        NKEventBusProvider.getInstance().sendTwi(                new OnStreamUnblock(managingUserId, source, unblockedUser)        );    }    @Override    public void onRetweetedRetweet(User source, User target, Status retweetedStatus) {        NKEventBusProvider.getInstance().sendTwi(                new OnStreamRetweetedRetweet(managingUserId, source, target, retweetedStatus)        );    }    @Override    public void onFavoritedRetweet(User source, User target, Status favoritedRetweeet) {        NKEventBusProvider.getInstance().sendTwi(                new OnStreamFavoritedRetweet(managingUserId, source, target, favoritedRetweeet)        );    }    @Override    public void onQuotedTweet(User source, User target, Status quotingTweet) {        NKEventBusProvider.getInstance().sendTwi(                new OnStreamQuotedTweet(managingUserId, source, target, quotingTweet)        );    }    @Override    public void onStatus(Status status) {        NKEventBusProvider.getInstance().sendTwi(                new OnStreamStatus(managingUserId, status)        );    }    @Override    public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {        NKEventBusProvider.getInstance().sendTwi(                new OnStreamDeletionNoticeStatus(managingUserId, statusDeletionNotice)        );    }    @Override    public void onTrackLimitationNotice(int numberOfLimitedStatuses) {        NKEventBusProvider.getInstance().sendTwi(                new OnStreamTrackLimitationNotice(managingUserId, numberOfLimitedStatuses)        );    }    @Override    public void onScrubGeo(long userId, long upToStatusId) {        NKEventBusProvider.getInstance().sendTwi(                new OnStreamScrubGeo(managingUserId, userId, upToStatusId)        );    }    @Override    public void onStallWarning(StallWarning warning) {        NKEventBusProvider.getInstance().sendTwi(                new OnStreamStallWarning(managingUserId, warning)        );    }    @Override    public void onException(Exception ex) {        Log.e("StreamError", ex.getLocalizedMessage());        NKEventBusProvider.getInstance().sendTwi(                new OnStreamException(managingUserId, ex)        );    }}