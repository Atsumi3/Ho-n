package info.nukoneko.android.ho_n.controller.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import info.nukoneko.android.ho_n.R;
import info.nukoneko.android.ho_n.controller.common.view.NKSwipeRefreshLayout;
import info.nukoneko.android.ho_n.controller.main.twitter.NKTweetDialog;
import info.nukoneko.android.ho_n.controller.main.twitter.NKTweetDialogListener;
import info.nukoneko.android.ho_n.controller.main.twitter.NKTwitterAuthActivity;
import info.nukoneko.android.ho_n.controller.main.twitter.NKTwitterTabPagerAdapter;
import info.nukoneko.android.ho_n.controller.main.twitter.tab.NKTweetTabMainMentionsFragment;
import info.nukoneko.android.ho_n.sys.eventbus.NKEventBusProvider;
import info.nukoneko.android.ho_n.sys.eventbus.NKEventTwitter;
import info.nukoneko.android.ho_n.sys.eventbus.event.NKTwitterUserStreamListener;
import info.nukoneko.android.ho_n.controller.main.twitter.tab.NKTweetTabFragmentAbstract;
import info.nukoneko.android.ho_n.controller.main.twitter.tab.NKTweetTabFragmentListener;
import info.nukoneko.android.ho_n.controller.main.twitter.tab.NKTweetTabMainFavoriteFragment;
import info.nukoneko.android.ho_n.controller.main.twitter.tab.NKTweetTabMainTimelineFragment;
import info.nukoneko.android.ho_n.controller.main.twitter.tab.OnClickTweetListener;
import info.nukoneko.android.ho_n.sys.base.BaseActivity;
import info.nukoneko.android.ho_n.sys.eventbus.event.OnStreamFavorite;
import info.nukoneko.android.ho_n.sys.eventbus.event.OnStreamUnfavorite;
import info.nukoneko.android.ho_n.sys.util.rx.Optional;
import info.nukoneko.android.ho_n.sys.util.twitter.NKTwitterUtil;
import rx.Observable;
import twitter4j.Status;
import twitter4j.User;

/**
 * Created by atsumi on 2016/10/19.
 */

public final class NKMainActivity extends BaseActivity
        implements NKTweetTabFragmentListener, OnClickTweetListener, NKTweetDialogListener, NKTopEventListener {

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;

    @BindView(R.id.swipe_refresh_layout)
    NKSwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.pager)
    ViewPager viewPager;

    @BindView(R.id.pager_tab_strip)
    PagerTabStrip tabStrip;

    @OnClick(R.id.btn_tweet)
    void onClickTweet(View view) {
        assert fragmentAdapter != null;

        Optional.ofParsable(fragmentAdapter.getItem(viewPager.getCurrentItem()), NKTweetTabFragmentAbstract.class)
                .subscribe(nkTweetTabFragmentAbstract -> {
                    NKTweetDialog.newInstance(nkTweetTabFragmentAbstract.getManagingUserId()).show(getSupportFragmentManager(), "frag");
                });
    }

    @Nullable
    NKTwitterTabPagerAdapter fragmentAdapter;

    private Map<Long, NKTwitterUserStreamListener> currentStream = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeRefreshLayout.setOnRefreshListener(this::refresh);

        fragmentAdapter = new NKTwitterTabPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(fragmentAdapter);

        // viewSetting
        ArrayList<Long> ids = NKTwitterUtil.getAccountIds();
        if (ids.size() > 0) {
            Observable.from(ids).forEach(userID ->
                    Optional.ofNullable(NKTwitterUtil.getInstance(this, userID))
                            .subscribe(twitter -> {
                                fragmentAdapter.addFragment(userID, NKTweetTabMainTimelineFragment.class);
                                fragmentAdapter.addFragment(userID, NKTweetTabMainMentionsFragment.class);
                                fragmentAdapter.addFragment(userID, NKTweetTabMainFavoriteFragment.class);
                                makeStream(userID);
                            }));
        } else {
            // startAuth
            startActivity(new Intent(this, NKTwitterAuthActivity.class));
        }

        Optional.ofParsable(tabStrip, PagerTitleStrip.class).subscribe(pagerTitleStrip -> {
            pagerTitleStrip.setOnTouchListener((view, motionEvent) -> {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    assert fragmentAdapter != null;
                    Optional.ofParsable(fragmentAdapter.getItem(viewPager.getCurrentItem()), NKTweetTabFragmentAbstract.class)
                            .subscribe(nkTweetTabFragmentAbstract -> {
                                nkTweetTabFragmentAbstract.getRecyclerView().smoothScrollToPosition(0);
                            });
                }
                return false;
            });
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (viewPager.getAdapter() == null || fragmentAdapter == null) {
            fragmentAdapter = new NKTwitterTabPagerAdapter(getSupportFragmentManager());
            viewPager.setAdapter(fragmentAdapter);
        }
    }

    //*** Function
    private void refresh() {
        // AllRefresh
//        Optional.ofNullable(getSupportFragmentManager().getFragments()).subscribe(fragments -> {
//            Observable.from(fragments).forEach(fragment -> {
//                if (fragment instanceof NKTweetTabFragmentAbstract) {
//                    ((NKTweetTabFragmentAbstract) fragment).firstLoad();
//                }
//            });
//        });

        // SingleRefresh
        assert fragmentAdapter != null;
        Optional.ofParsable(fragmentAdapter.getItem(viewPager.getCurrentItem()), NKTweetTabFragmentAbstract.class)
                .subscribe(NKTweetTabFragmentAbstract::firstLoad);
    }

    private void makeStream(long userId) {
        if (1 > userId) return;

        if (currentStream.containsKey(userId)) return;

        NKTwitterUserStreamListener streamListener = new NKTwitterUserStreamListener(userId);

        Optional.ofNullable(NKTwitterUtil.getStreamInstance(this, userId))
                .subscribe(twitterStream -> {
                    twitterStream.addListener(streamListener);
                    twitterStream.user();
                    currentStream.put(userId, streamListener);
        });
    }

    @Override
    public void showSnackBar(String text) {
        Snackbar.make(coordinatorLayout, text, Snackbar.LENGTH_LONG).show();
    }

    //*** TabFragment Listener
    @Override
    public void refreshEnd() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void updateUser(User user) {
        Optional.ofNullable(fragmentAdapter).subscribe(PagerAdapter::notifyDataSetChanged);
    }

    //*** TweetListener
    @Override
    public void onClickUserIcon(Status status) {
        showSnackBar("未実装");
    }

    @Override
    public void onClickTweetFavorite(Status status) {
        showSnackBar("未実装");
    }

    @Override
    public void onClickTweetReTweet(Status status) {
        showSnackBar("未実装");
    }

    @Override
    public void onClickTweet(Status status) {
        assert fragmentAdapter != null;

        Optional.ofParsable(fragmentAdapter.getItem(viewPager.getCurrentItem()), NKTweetTabFragmentAbstract.class)
                .subscribe(nkTweetTabFragmentAbstract -> {
                    NKTweetDialog.newInstance(nkTweetTabFragmentAbstract.getManagingUserId(), status)
                            .show(getSupportFragmentManager(), "frag");
                });
    }

    //*** TweetDialogListener
    @Override
    public void onTweetDialogTweetSuccess(Status status) {
        showSnackBar("ツイートしました");
    }

    @Override
    public void onTweetDialogTweetFailed(Throwable throwable) {
        showSnackBar("ツイートに失敗しました");
    }
}
