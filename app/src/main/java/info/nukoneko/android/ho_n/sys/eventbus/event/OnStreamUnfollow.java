package info.nukoneko.android.ho_n.sys.eventbus.event;import info.nukoneko.android.ho_n.sys.eventbus.NKEventTwitter;import twitter4j.User;/** * Created by TEJNEK on 2016/10/22. */public class OnStreamUnfollow extends NKEventTwitter {    private final User source;    private final User unfollowedUser;    public OnStreamUnfollow(long managingUserId, User source, User unfollowedUser) {        super(managingUserId);        this.source = source;        this.unfollowedUser = unfollowedUser;    }    public User getSource() {        return source;    }    public User getUnfollowedUser() {        return unfollowedUser;    }}