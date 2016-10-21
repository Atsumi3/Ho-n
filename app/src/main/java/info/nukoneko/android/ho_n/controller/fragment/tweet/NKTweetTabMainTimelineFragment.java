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

    public static NKTweetTabMainTimelineFragment newInstance(long userId){
        NKTweetTabMainTimelineFragment fragment = new NKTweetTabMainTimelineFragment();
        Bundle args = new Bundle();
        args.putLong(EXTRA_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public RxUtil.RxCallable<List<Status>> getStatuses() {
        return () -> {
            Twitter twitter = NKTwitterUtil.getTwitterInstance(getContext(), getManagingUserId());
            return twitter.getHomeTimeline();
        };
    }

    @Override
    public String getTitle() {
        if (getUser() != null) {
            return String.format("%s - HomeTimeline", getUser().getName());
        } else {
            return "HomeTimeline";
        }
    }
}
