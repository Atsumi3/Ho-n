package info.nukoneko.android.ho_n.sys.eventbus.event;import info.nukoneko.android.ho_n.sys.eventbus.NKEventTwitter;import twitter4j.Status;import twitter4j.User;/** * Created by atsumi on 2016/10/22. */public class OnStreamQuotedTweet extends NKEventTwitter {    private final User source;    private final User target;    private final Status quotingTweet;    public OnStreamQuotedTweet(long managingUserId, User source, User target, Status quotingTweet) {        super(managingUserId);        this.source = source;        this.target = target;        this.quotingTweet = quotingTweet;    }    public User getSource() {        return source;    }    public User getTarget() {        return target;    }    public Status getQuotingTweet() {        return quotingTweet;    }}