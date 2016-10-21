package info.nukoneko.android.ho_n.controller.fragment.tweet;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.nukoneko.android.ho_n.R;
import info.nukoneko.android.ho_n.sys.base.BaseFragment;
import info.nukoneko.android.ho_n.sys.util.rx.Optional;
import info.nukoneko.android.ho_n.sys.util.rx.RxUtil;
import info.nukoneko.android.ho_n.sys.util.rx.RxWrap;
import rx.functions.Func1;
import twitter4j.Status;

/**
 * Created by atsumi on 2016/10/21.
 */

public abstract class NKTweetTabFragmentAbstract extends BaseFragment {
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.progress)
    View progressView;

    private TweetAdapter adapter;
    @Nullable
    private NKTweetTAbFragmentListener listener;

    @Override
    final public int fragmentLayoutId() {
        return R.layout.fragment_tweet_tab;
    }

    public abstract RxUtil.RxCallable<List<Status>> getStatuses();
    public abstract String getTitle();

    @Override
    public void onDestroyView() {
        recyclerView.setAdapter(null);
        super.onDestroyView();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NKTweetTAbFragmentListener) {
            listener = (NKTweetTAbFragmentListener) context;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (adapter == null) {
            adapter = new TweetAdapter();
            loadTweet();
        }
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    public void loadTweet() {
        if (getView() != null) {
            recyclerView.setVisibility(View.GONE);
            progressView.setVisibility(View.VISIBLE);
        }

//        adapter.clear();

        RxWrap.create(RxUtil.createObservable(getStatuses()))
                .onErrorReturn(throwable -> new ArrayList<>())
                .subscribe(statuses -> {
                    Log.i("GetTweet", String.format("Num: %d", statuses.size()));
                    if (getView() != null) {
                        recyclerView.setVisibility(View.VISIBLE);
                        progressView.setVisibility(View.GONE);
                    }
                    adapter.addAll(statuses);
                    Optional.ofNullable(listener).subscribe(NKTweetTAbFragmentListener::refreshEnd);
                }, Throwable::printStackTrace);
    }

    public static class TweetAdapter extends RecyclerView.Adapter<NKTweetTabFragmentAbstract.TweetViewHolder> {
        @NonNull
        private final List<Status> data = new ArrayList<>();
        private final Object lock = new Object();

        @Override
        public NKTweetTabFragmentAbstract.TweetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            final View view = inflater.inflate(R.layout.item_list_tweet, parent, false);
            return new NKTweetTabFragmentAbstract.TweetViewHolder(view);
        }

        @Override
        public void onBindViewHolder(NKTweetTabFragmentAbstract.TweetViewHolder holder, int position) {
            Optional.ofNullable(data.get(position)).subscribe(status -> {
                holder.tweetText.setText(status.getText());
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

//        public void clear() {
//            synchronized (lock) {
//                int itemCount = data.size();
//                data.clear();
//                notifyItemRangeRemoved(0, itemCount);
//            }
//        }

        public void addAll(@NonNull Collection<? extends Status> collection) {
            synchronized (lock) {
                int itemCount = collection.size();
                int startPosition = data.size();
                data.addAll(collection);
                notifyItemRangeInserted(startPosition, itemCount);
            }
        }
    }

    public static class TweetViewHolder extends RecyclerView.ViewHolder {
        final View view;

        @BindView(R.id.text)
        TextView tweetText;

        public TweetViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            ButterKnife.bind(this, itemView);
        }
    }
}
