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

import butterknife.BindView;
import info.nukoneko.android.ho_n.R;
import info.nukoneko.android.ho_n.controller.common.view.NKEndlessScrollListener;
import info.nukoneko.android.ho_n.controller.main.NKTopEventListener;
import info.nukoneko.android.ho_n.sys.base.BaseFragment;
import info.nukoneko.android.ho_n.sys.eventbus.NKEventTwitter;
import info.nukoneko.android.ho_n.sys.eventbus.event.OnStreamDeletionNoticeStatus;
import info.nukoneko.android.ho_n.sys.eventbus.event.OnStreamFavorite;
import info.nukoneko.android.ho_n.sys.eventbus.event.OnStreamRetweetedRetweet;
import info.nukoneko.android.ho_n.sys.exeption.ArgumentsNotFindException;
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

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.progress)
    View progressView;

    // Valiable
    private NKTweetAdapter adapter;
    @Nullable
    private NKTweetTabFragmentListener listener;
    // userId
    private long managingUserId = -1;
    @Nullable private User user;

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

    public long getManagingUserId() {
        if (managingUserId == -1) {
            try {
                throw new ArgumentsNotFindException();
            } catch (ArgumentsNotFindException e) {
                e.printStackTrace();
                return -1;
            }
        }
        return managingUserId;
    }

    @Nullable
    public User getUser() {
        return user;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    @Override
    public void onDestroyView() {
        recyclerView.setAdapter(null);
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
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addOnScrollListener(new NKEndlessScrollListener((LinearLayoutManager) recyclerView.getLayoutManager()) {
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
            recyclerView.setVisibility(View.GONE);
            progressView.setVisibility(View.VISIBLE);
        }

        RxWrap.create(RxUtil.createObservable(getDefaultStatuses(twitter, paging)))
                .onErrorReturn(throwable -> new ArrayList<>())
                .subscribe(statuses -> {
                    Log.i("GetTweet", String.format("Num: %d", statuses.size()));
                    if (getView() != null) {
                        recyclerView.setVisibility(View.VISIBLE);
                        progressView.setVisibility(View.GONE);
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
        if (position == 0 && 100 > recyclerView.computeVerticalScrollOffset()) {
            recyclerView.smoothScrollToPosition(0);
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
