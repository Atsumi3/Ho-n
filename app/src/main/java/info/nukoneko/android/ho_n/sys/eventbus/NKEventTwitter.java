package info.nukoneko.android.ho_n.sys.eventbus;

/**
 * Created by atsumi on 2016/10/20.
 */

public abstract class NKEventTwitter implements NKEvent {
    private final long parentUserId;

    public NKEventTwitter(long userId) {
        this.parentUserId = userId;
    }

    final public long getParentUserId() {
        return parentUserId;
    }
}
