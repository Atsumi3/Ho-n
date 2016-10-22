package info.nukoneko.android.ho_n.controller.main.twitter.tab;

import java.util.List;

import info.nukoneko.android.ho_n.sys.util.rx.RxUtil;
import info.nukoneko.android.ho_n.sys.util.twitter.NKTwitterUtil;
import twitter4j.Status;
import twitter4j.Twitter;

/**
 * Created by atsumi on 2016/10/21.
 */

public final class NKTweetTabMainTimelineFragment extends NKTweetTabFragmentAbstract {

//    public static NKTweetTabMainTimelineFragment newInstance(long userId){
//        NKTweetTabMainTimelineFragment fragment = new NKTweetTabMainTimelineFragment();
//        Bundle args = new Bundle();
//        args.putLong(EXTRA_USER_ID, userId);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public RxUtil.RxCallable<List<Status>> getDefaultStatuses() {
        return () -> {
            Twitter twitter = NKTwitterUtil.getInstance(getContext(), getManagingUserId());
            return twitter.getHomeTimeline();
        };
    }

    @Override
    public NKTwitterTabListType getListType() {
        return NKTwitterTabListType.Home;
    }
}
