package info.nukoneko.android.ho_n.controller.main.twitter.tab;

import java.util.ArrayList;
import java.util.List;

import info.nukoneko.android.ho_n.sys.util.rx.RxUtil;
import info.nukoneko.android.ho_n.sys.util.twitter.NKTwitterUtil;
import twitter4j.Status;
import twitter4j.Twitter;

/**
 * Created by atsumi on 2016/10/21.
 */

public final class NKTweetTabMainTimelineFragment extends NKTweetTabFragmentAbstract {

    @Override
    public RxUtil.RxCallable<List<Status>> getDefaultStatuses() {
        return () -> {
            Twitter twitter = NKTwitterUtil.getInstance(getContext(), getManagingUserId());
            if (twitter == null) {
                return new ArrayList<>();
            }
            return twitter.getHomeTimeline();
        };
    }

    @Override
    public NKTwitterTabListType getListType() {
        return NKTwitterTabListType.Home;
    }
}
