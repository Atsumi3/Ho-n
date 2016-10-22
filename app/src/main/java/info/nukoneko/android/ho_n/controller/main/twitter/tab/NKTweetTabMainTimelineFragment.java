package info.nukoneko.android.ho_n.controller.main.twitter.tab;

import java.util.ArrayList;
import java.util.List;

import info.nukoneko.android.ho_n.sys.eventbus.NKEvent;
import info.nukoneko.android.ho_n.sys.eventbus.NKEventBusProvider;
import info.nukoneko.android.ho_n.sys.eventbus.NKEventTwitter;
import info.nukoneko.android.ho_n.sys.eventbus.event.OnStreamDeletionNoticeStatus;
import info.nukoneko.android.ho_n.sys.eventbus.event.OnStreamStatus;
import info.nukoneko.android.ho_n.sys.util.rx.Optional;
import info.nukoneko.android.ho_n.sys.util.rx.RxUtil;
import info.nukoneko.android.ho_n.sys.util.rx.RxWrap;
import info.nukoneko.android.ho_n.sys.util.twitter.NKTwitterUtil;
import rx.functions.Action1;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;

/**
 * Created by atsumi on 2016/10/21.
 */

public final class NKTweetTabMainTimelineFragment extends NKTweetTabFragmentAbstract {

    @Override
    public RxUtil.RxCallable<List<Status>> getDefaultStatuses(Twitter twitter, Paging paging) {
        return () -> twitter.getHomeTimeline(paging);
    }

    @Override
    public NKTwitterTabListType getListType() {
        return NKTwitterTabListType.Home;
    }

    @Override
    public void receiveEvent(NKEventTwitter event) {
        Optional.ofParsable(event, OnStreamStatus.class).subscribe(onStreamStatus -> {
            getAdapter().add(onStreamStatus.getStatus());
        });
    }
}
