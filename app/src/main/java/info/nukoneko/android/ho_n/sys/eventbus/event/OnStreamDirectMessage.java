package info.nukoneko.android.ho_n.sys.eventbus.event;import info.nukoneko.android.ho_n.sys.eventbus.NKEventTwitter;import twitter4j.DirectMessage;/** * Created by atsumi on 2016/10/22. */public class OnStreamDirectMessage extends NKEventTwitter {    private final DirectMessage directMessage;    public OnStreamDirectMessage(long managingUserId, DirectMessage directMessage) {        super(managingUserId);        this.directMessage = directMessage;    }    public DirectMessage getDirectMessage() {        return directMessage;    }}