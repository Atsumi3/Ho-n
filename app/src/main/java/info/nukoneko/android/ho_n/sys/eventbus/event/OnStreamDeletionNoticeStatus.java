package info.nukoneko.android.ho_n.sys.eventbus.event;import info.nukoneko.android.ho_n.sys.eventbus.NKEventTwitter;import twitter4j.StatusDeletionNotice;/** * Created by TEJNEK on 2016/10/22. */public class OnStreamDeletionNoticeStatus extends NKEventTwitter {    private final StatusDeletionNotice statusDeletionNotice;    public OnStreamDeletionNoticeStatus(long managingUserId, StatusDeletionNotice statusDeletionNotice) {        super(managingUserId);        this.statusDeletionNotice = statusDeletionNotice;    }    public StatusDeletionNotice getStatusDeletionNotice() {        return statusDeletionNotice;    }}