package info.nukoneko.android.ho_n.controller.fragment.tweet;

import android.os.Bundle;

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
    public RxUtil.RxCallable<List<Status>> getStatuses() {
        return () -> {
            Twitter twitter = NKTwitterUtil.getTwitterInstance(getContext(), 0);
            return twitter.getHomeTimeline();
        };
    }

    @Override
    public String getTitle() {
        return "HomeTimeline";
    }

    @Override
    public void fragmentSetup(Bundle bundle) {

    }
}
