package info.nukoneko.android.ho_n.controller.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import info.nukoneko.android.ho_n.R;
import info.nukoneko.android.ho_n.controller.common.view.NKSwipeRefreshLayout;
import info.nukoneko.android.ho_n.controller.main.twitter.NKTwitterAuthActivity;
import info.nukoneko.android.ho_n.controller.main.twitter.NKTwitterTabPagerAdapter;
import info.nukoneko.android.ho_n.sys.eventbus.event.NKTwitterUserStreamListener;
import info.nukoneko.android.ho_n.controller.main.twitter.tab.NKTweetTabFragmentAbstract;
import info.nukoneko.android.ho_n.controller.main.twitter.tab.NKTweetTabFragmentListener;
import info.nukoneko.android.ho_n.controller.main.twitter.tab.NKTweetTabMainFavoriteFragment;
import info.nukoneko.android.ho_n.controller.main.twitter.tab.NKTweetTabMainTimelineFragment;
import info.nukoneko.android.ho_n.controller.main.twitter.tab.NKTweetTabMainUserFragment;
import info.nukoneko.android.ho_n.controller.main.twitter.tab.OnClickTweetListener;
import info.nukoneko.android.ho_n.sys.base.BaseActivity;
import info.nukoneko.android.ho_n.sys.util.rx.Optional;
import info.nukoneko.android.ho_n.sys.util.twitter.NKTwitterUtil;
import rx.Observable;
import twitter4j.Status;
import twitter4j.User;

/**
 * Created by atsumi on 2016/10/19.
 */

public final class NKMainActivity extends BaseActivity
        implements NKTweetTabFragmentListener, OnClickTweetListener {

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;

    @BindView(R.id.swipe_refresh_layout)
    NKSwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.pager)
    ViewPager viewPager;

    @OnClick(R.id.btn_tweet)
    void onClickTweet(View view) {

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
                                fragmentAdapter.addFragment(userID, NKTweetTabMainFavoriteFragment.class);
                                makeStream(userID);
                            }));
        } else {
            // startAuth
            startActivity(new Intent(this, NKTwitterAuthActivity.class));
        }
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
        Optional.ofNullable(getSupportFragmentManager().getFragments()).subscribe(fragments -> {
            Observable.from(fragments).forEach(fragment -> {
                if (fragment instanceof NKTweetTabFragmentAbstract) {
                    ((NKTweetTabFragmentAbstract) fragment).loadTweet();
                }
            });
        });
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
        Snackbar.make(coordinatorLayout, status.getUser().getName(), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onClickTweetFavorite(Status status) {
        Snackbar.make(coordinatorLayout, status.getText(), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onClickTweetReTweet(Status status) {
        Snackbar.make(coordinatorLayout, status.getText(), Snackbar.LENGTH_LONG).show();
    }
}
