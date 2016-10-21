package info.nukoneko.android.ho_n.controller.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;

import butterknife.BindView;
import info.nukoneko.android.ho_n.R;
import info.nukoneko.android.ho_n.controller.common.view.NKSwipeRefreshLayout;
import info.nukoneko.android.ho_n.controller.fragment.tweet.NKTweetTAbFragmentListener;
import info.nukoneko.android.ho_n.controller.fragment.tweet.NKTweetTabFragmentAbstract;
import info.nukoneko.android.ho_n.controller.fragment.tweet.NKTweetTabMainTimelineFragment;
import info.nukoneko.android.ho_n.sys.base.BaseActivity;
import info.nukoneko.android.ho_n.sys.util.rx.Optional;
import rx.Observable;

/**
 * Created by atsumi on 2016/10/19.
 */

public final class NKMainActivity extends BaseActivity implements NKTweetTAbFragmentListener {

    @BindView(R.id.swipe_refresh_layout)
    NKSwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.pager)
    ViewPager viewPager;

    @Nullable MyPagerAdapter fragmentAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeRefreshLayout.setOnRefreshListener(this::refresh);

        fragmentAdapter = new MyPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(fragmentAdapter);

        fragmentAdapter.getFragments().add(new NKTweetTabMainTimelineFragment());
        fragmentAdapter.getFragments().add(new NKTweetTabMainTimelineFragment());
        fragmentAdapter.notifyDataSetChanged();

    }

    private void refresh(){
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

    static class MyPagerAdapter extends FragmentPagerAdapter  {

        ArrayList<NKTweetTabFragmentAbstract> fragments = new ArrayList<>();

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

        public ArrayList<NKTweetTabFragmentAbstract> getFragments() {
            return fragments;
        }
    }
}
