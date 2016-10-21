package info.nukoneko.android.ho_n.controller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import java.util.ArrayList;

import butterknife.BindView;
import info.nukoneko.android.ho_n.R;
import info.nukoneko.android.ho_n.controller.common.view.NKSwipeRefreshLayout;
import info.nukoneko.android.ho_n.controller.fragment.tweet.NKTweetTAbFragmentListener;
import info.nukoneko.android.ho_n.controller.fragment.tweet.NKTweetTabFragmentAbstract;
import info.nukoneko.android.ho_n.controller.fragment.tweet.NKTweetTabMainTimelineFragment;
import info.nukoneko.android.ho_n.sys.base.BaseActivity;
import info.nukoneko.android.ho_n.sys.util.rx.Optional;
import info.nukoneko.android.ho_n.sys.util.rx.RxUtil;
import info.nukoneko.android.ho_n.sys.util.rx.RxWrap;
import info.nukoneko.android.ho_n.sys.util.twitter.NKTwitterUtil;
import rx.Observable;
import rx.functions.Action1;
import twitter4j.User;

/**
 * Created by atsumi on 2016/10/19.
 */

public final class NKMainActivity extends BaseActivity implements NKTweetTAbFragmentListener {

    @BindView(R.id.swipe_refresh_layout)
    NKSwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.pager)
    ViewPager viewPager;

    @Nullable
    MyPagerAdapter fragmentAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeRefreshLayout.setOnRefreshListener(this::refresh);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (viewPager.getAdapter() == null || fragmentAdapter == null) {
            fragmentAdapter = new MyPagerAdapter(getSupportFragmentManager());
            viewPager.setAdapter(fragmentAdapter);
        }

        // viewSetting
        ArrayList<Long> ids = NKTwitterUtil.getAccountIds();
        if (ids.size() > 0) {
            Observable.from(ids)
                    .map(aLong -> NKTwitterUtil.getTwitterInstance(this, aLong))
                    .filter(twitter -> twitter != null)
                    .forEach(twitter ->
                            RxWrap.create(
                                    RxUtil.createObservable(() -> twitter.showUser(twitter.getId())),
                                    bindToLifecycle())
                                    .subscribe(user -> {
                                        Log.i("User", String.format("%s", user.getName()));
                                        fragmentAdapter.addFragment(user.getId(),
                                                NKTweetTabMainTimelineFragment.newInstance(user.getId()));
                                    }));
        } else {
            // startAuth
            startActivity(new Intent(this, NKTwitterAuthActivity.class));
        }
    }

    private void refresh() {
        Optional.ofNullable(getSupportFragmentManager().getFragments()).subscribe(fragments -> {
            Observable.from(fragments).forEach(fragment -> {
                if (fragment instanceof NKTweetTabFragmentAbstract) {
                    ((NKTweetTabFragmentAbstract) fragment).loadTweet();
                }
            });
        });
    }

    @Override
    public void refreshEnd() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void updateUser(User user) {
        Optional.ofNullable(fragmentAdapter).subscribe(PagerAdapter::notifyDataSetChanged);
    }

    static class MyPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<NKTweetTabFragmentAbstract> fragments = new ArrayList<>();

        MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragments.get(position).getTitle();
        }

        public <T extends NKTweetTabFragmentAbstract> void addFragment(long userId, T fragment) {
            //TODO 良くない...
            final boolean isExistFragment = RxUtil.createObservable(() -> Observable
                    .from(fragments)
                    .filter(f -> f.getTitle().equals(fragment.getTitle()))
                    .reduce(0, (before, f) -> before + (f.getManagingUserId() == userId ? 1 : 0))
                    .toBlocking().single() > 0).toBlocking().single();

            if (isExistFragment) return;

            fragments.add(fragment);
            this.notifyDataSetChanged();
        }
    }
}
