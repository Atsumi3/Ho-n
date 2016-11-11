package info.nukoneko.android.ho_n.controller.main.twitter.tab;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import info.nukoneko.android.ho_n.R;
import info.nukoneko.android.ho_n.controller.common.view.NKEndlessScrollListener;
import info.nukoneko.android.ho_n.controller.main.NKTopEventListener;
import info.nukoneko.android.ho_n.databinding.FragmentTweetTabBinding;
import info.nukoneko.android.ho_n.sys.base.BaseFragment;
import info.nukoneko.android.ho_n.sys.eventbus.NKEventTwitter;
import info.nukoneko.android.ho_n.sys.eventbus.event.OnStreamDeletionNoticeStatus;
import info.nukoneko.android.ho_n.sys.eventbus.event.OnStreamFavorite;
import info.nukoneko.android.ho_n.sys.eventbus.event.OnStreamRetweetedRetweet;
import info.nukoneko.android.ho_n.sys.util.rx.Optional;
import info.nukoneko.android.ho_n.sys.util.rx.RxUtil;
import info.nukoneko.android.ho_n.sys.util.rx.RxWrap;
import info.nukoneko.android.ho_n.sys.util.twitter.NKTwitterUtil;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.User;

/**
 * Created by atsumi on 2016/10/21.
 */

public abstract class NKTweetTabFragmentAbstract extends BaseFragment
        implements NKTweetAdapter.NKTweetAdapterListener {
    final protected static String EXTRA_USER_ID = "extra_user_id";

    // Valiable
    private NKTweetAdapter adapter;
    @Nullable
    private NKTweetTabFragmentListener listener;
    // userId
    private long managingUserId = -1;
    @Nullable private User user;

    private FragmentTweetTabBinding binding;

    @Override
    final public int fragmentLayoutId() {
        return R.layout.fragment_tweet_tab;
    }

    public abstract RxUtil.RxCallable<List<Status>> getDefaultStatuses(Twitter twitter, Paging paging);
    public abstract NKTwitterTabListType getListType();
    public abstract void receiveEvent(NKEventTwitter event);

    public Bundle getBundleOption(long userId) {
        Bundle args = new Bundle();
        args.putLong(EXTRA_USER_ID, userId);
        return args;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getManagingUserId() == null) return;

        RxWrap.eventReceive(bindToLifecycle()).subscribe(nkEvent -> {
            Optional.ofParsable(nkEvent, NKEventTwitter.class)
                    .subscribe(event -> {
                        if (event.getParentUserId() != getManagingUserId()) return;
                        receiveEvent(event);

                        // 削除イベントは共通
                        Optional.ofParsable(event, OnStreamDeletionNoticeStatus.class).subscribe(onStreamDeletionNoticeStatus -> {
                            adapter.delete(onStreamDeletionNoticeStatus.getStatusDeletionNotice().getStatusId());
                        });

                        // Favイベントは共通
                        Optional.ofParsable(event, OnStreamFavorite.class).subscribe(onStreamFavorite -> {
                            adapter.update(onStreamFavorite.getFavoritedStatus());
                            Optional.ofParsable(getContext(), NKTopEventListener.class)
                                    .subscribe(nkTopEventListener -> {
                                        nkTopEventListener.showSnackBar("ふぁぼられたみたい");
                                    });
                        });

                        Optional.ofParsable(event, OnStreamRetweetedRetweet.class).subscribe(onStreamRetweetedRetweet -> {
                            Optional.ofParsable(getContext(), NKTopEventListener.class)
                                    .subscribe(nkTopEventListener -> {
                                        nkTopEventListener.showSnackBar("RTがRTされたみたい");
                                    });
                        });
                    });
        });
    }

    @Override
    public void fragmentSetup(Bundle bundle) {
        binding = FragmentTweetTabBinding.bind(getView());
        managingUserId = bundle.getLong(EXTRA_USER_ID, -1);

        if (user == null) {
            RxWrap.create(RxUtil.createObservable(() -> NKTwitterUtil
                    .getInstance(getContext(), managingUserId)
                    .showUser(managingUserId)), bindToLifecycle()).subscribe(user -> {
                this.user = user;
                Optional.ofNullable(listener).subscribe(nkTweetTAbFragmentListener -> {
                    nkTweetTAbFragmentListener.updateUser(user);
                });
            }, Throwable::printStackTrace);
        }
    }

    @Nullable
    public Long getManagingUserId() {
        return managingUserId > 0 ? managingUserId : null;
    }

    @Nullable
    public User getUser() {
        return user;
    }

    public RecyclerView getRecyclerView() {
        return binding.recyclerView;
    }

    @Override
    public void onDestroyView() {
        binding.recyclerView.setAdapter(null);
        super.onDestroyView();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NKTweetTabFragmentListener) {
            listener = (NKTweetTabFragmentListener) context;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (adapter == null) {
            adapter = new NKTweetAdapter(getContext(), this);
            loadTweet();
        }
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.addOnScrollListener(new NKEndlessScrollListener((LinearLayoutManager) binding.recyclerView.getLayoutManager()) {
            @Override
            public void onLoadMore(int current_page) {
                loadTweet();
            }
        });
    }

    public NKTweetAdapter getAdapter() {
        return adapter;
    }

    public void firstLoad() {
//        if (getAdapter().getItemCount() > 0) {
//            Optional.ofNullable(listener).subscribe(NKTweetTabFragmentListener::refreshEnd);
//            return;
//        }
        loadTweet(true);
    }

    void loadTweet() {
        loadTweet(false);
    }

    void loadTweet(boolean loadForceFirst) {
        if (getManagingUserId() == null) return;

        // ロードのためのページング取得
        Twitter twitter = NKTwitterUtil.getInstance(getContext(), getManagingUserId());
        if (twitter == null) {
            Optional.ofNullable(listener).subscribe(NKTweetTabFragmentListener::refreshEnd);
            return;
        }
        Paging paging;
        {
            Status lastItem = getAdapter().getLastItem();
            if (lastItem == null || loadForceFirst) {
                paging = new Paging(1);
            } else {
                paging = new Paging();
                paging.setMaxId(lastItem.getId());
            }
        }
        ///////////////////////////////////

        if (getView() != null && adapter.getItemCount() == 0) {
            binding.recyclerView.setVisibility(View.GONE);
            binding.progress.setVisibility(View.VISIBLE);
        }

        RxWrap.create(RxUtil.createObservable(getDefaultStatuses(twitter, paging)))
                .onErrorReturn(throwable -> new ArrayList<>())
                .subscribe(statuses -> {
                    Log.i("GetTweet", String.format("Num: %d", statuses.size()));
                    if (getView() != null) {
                        binding.recyclerView.setVisibility(View.VISIBLE);
                        binding.progress.setVisibility(View.GONE);
                    }
                    if (loadForceFirst) {
                        adapter.putAll(statuses);
                    } else {
                        adapter.addAll(statuses);
                    }
                    Optional.ofNullable(listener).subscribe(NKTweetTabFragmentListener::refreshEnd);
                }, Throwable::printStackTrace);
    }

    //*** TweetAdapterListener
    @Override
    final public void onInserted(int position, Status status) {
        if (position == 0 && 100 > binding.recyclerView.computeVerticalScrollOffset()) {
            binding.recyclerView.smoothScrollToPosition(0);
        }
    }

    @Override
    public void onUpdated(int position, Status status) {

    }

    @Override
    public void onDeleted(int position, Status status) {
        Log.d("Deleted",
                String.format("%s - %s \n %s",
                        status.getUser().getName(),
                        status.getUser().getScreenName(),
                        status.getText()));
    }
}
