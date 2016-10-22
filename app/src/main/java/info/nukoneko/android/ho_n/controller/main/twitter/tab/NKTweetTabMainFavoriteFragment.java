package info.nukoneko.android.ho_n.controller.main.twitter.tab;

import java.util.ArrayList;
import java.util.List;

import info.nukoneko.android.ho_n.sys.eventbus.NKEventTwitter;
import info.nukoneko.android.ho_n.sys.eventbus.event.OnStreamFavorite;
import info.nukoneko.android.ho_n.sys.eventbus.event.OnStreamStatus;
import info.nukoneko.android.ho_n.sys.eventbus.event.OnStreamUnfavorite;
import info.nukoneko.android.ho_n.sys.util.rx.Optional;
import info.nukoneko.android.ho_n.sys.util.rx.RxUtil;
import info.nukoneko.android.ho_n.sys.util.rx.RxWrap;
import info.nukoneko.android.ho_n.sys.util.twitter.NKTwitterUtil;
import rx.functions.Action1;
import twitter4j.Status;
import twitter4j.Twitter;

/**
 * Created by atsumi on 2016/10/21.
 */

public final class NKTweetTabMainFavoriteFragment extends NKTweetTabFragmentAbstract {

    @Override
    public RxUtil.RxCallable<List<Status>> getDefaultStatuses() {
        return () -> {
            Twitter twitter = NKTwitterUtil.getInstance(getContext(), getManagingUserId());
            if (twitter == null) {
                return new ArrayList<>();
            }
            return twitter.getFavorites();
        };
    }

    @Override
    public NKTwitterTabListType getListType() {
        return NKTwitterTabListType.Favorites;
    }

    @Override
    public void receiveEvent(NKEventTwitter event) {
        Optional.ofParsable(event, OnStreamFavorite.class).subscribe(onStreamFavorite -> {
            getAdapter().add(onStreamFavorite.getFavoritedStatus());
        });
        Optional.ofParsable(event, OnStreamUnfavorite.class).subscribe(onStreamUnfavorite -> {
            getAdapter().delete(onStreamUnfavorite.getUnfavoritedStatus().getId());
        });
    }
}
