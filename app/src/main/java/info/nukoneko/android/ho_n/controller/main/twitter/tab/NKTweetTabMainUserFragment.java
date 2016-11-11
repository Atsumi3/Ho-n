package info.nukoneko.android.ho_n.controller.main.twitter.tab;

import java.util.List;

import info.nukoneko.android.ho_n.sys.eventbus.NKEventTwitter;
import info.nukoneko.android.ho_n.sys.util.rx.RxUtil;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;

/**
 * Created by atsumi on 2016/10/21.
 */

public final class NKTweetTabMainUserFragment extends NKTweetTabFragmentAbstract {

    @Override
    public RxUtil.RxCallable<List<Status>> getDefaultStatuses(Twitter twitter, Paging paging) {
        return () -> twitter.getUserTimeline(paging);
    }

    @Override
    public NKTwitterTabListType getListType() {
        return NKTwitterTabListType.User;
    }

    @Override
    public void receiveEvent(NKEventTwitter event) {

    }
}
