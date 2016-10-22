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
import info.nukoneko.android.ho_n.sys.base.BaseFragment;
import info.nukoneko.android.ho_n.sys.exeption.ArgumentsNotFindException;
import info.nukoneko.android.ho_n.sys.util.rx.Optional;
import info.nukoneko.android.ho_n.sys.util.rx.RxUtil;
import info.nukoneko.android.ho_n.sys.util.rx.RxWrap;
import info.nukoneko.android.ho_n.sys.util.twitter.NKTwitterUtil;
import twitter4j.Status;
import twitter4j.User;

/**
 * Created by atsumi on 2016/10/21.
 */

public abstract class NKTweetTabFragmentAbstract extends BaseFragment {
    final protected static String EXTRA_USER_ID = "extra_user_id";

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.progress)
    View progressView;

    // Valiable
    private NKTweetAdapter adapter;
    @Nullable
    private NKTweetTabFragmentListener listener;

    @Override
    final public int fragmentLayoutId() {
        return R.layout.fragment_tweet_tab;
    }

    public abstract RxUtil.RxCallable<List<Status>> getDefaultStatuses();
    public abstract NKTwitterTabListType getListType();

    // userId
    private long managingUserId = -1;
    @Nullable private User user;

    public Bundle getBundleOption(long userId) {
        Bundle args = new Bundle();
        args.putLong(EXTRA_USER_ID, userId);
        return args;
    }

    @Override
    public void fragmentSetup(Bundle bundle) {
        Log.d("Bundle USer Id", String.valueOf(bundle.getLong(EXTRA_USER_ID)));
        managingUserId = bundle.getLong(EXTRA_USER_ID, -1);

        RxWrap.create(RxUtil.createObservable(() -> NKTwitterUtil
                .getInstance(getContext(), managingUserId)
                .showUser(managingUserId)), bindToLifecycle()).subscribe(user -> {
            this.user = user;
            Optional.ofNullable(listener).subscribe(nkTweetTAbFragmentListener -> {
                nkTweetTAbFragmentListener.updateUser(user);
            });
        }, Throwable::printStackTrace);
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
            adapter = new NKTweetAdapter(getContext());
            loadTweet();
        }
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    public void loadTweet() {
        if (adapter.getItemCount() > 0) {
            Optional.ofNullable(listener).subscribe(NKTweetTabFragmentListener::refreshEnd);
            return;
        }
        if (getView() != null) {
            recyclerView.setVisibility(View.GONE);
            progressView.setVisibility(View.VISIBLE);
        }

        RxWrap.create(RxUtil.createObservable(getDefaultStatuses()))
                .onErrorReturn(throwable -> new ArrayList<>())
                .subscribe(statuses -> {
                    Log.i("GetTweet", String.format("Num: %d", statuses.size()));
                    if (getView() != null) {
                        recyclerView.setVisibility(View.VISIBLE);
                        progressView.setVisibility(View.GONE);
                    }
                    adapter.addAll(statuses);
                    Optional.ofNullable(listener).subscribe(NKTweetTabFragmentListener::refreshEnd);
                }, Throwable::printStackTrace);
    }
}
