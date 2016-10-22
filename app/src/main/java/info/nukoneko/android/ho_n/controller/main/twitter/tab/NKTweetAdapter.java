package info.nukoneko.android.ho_n.controller.main.twitter.tab;import android.content.Context;import android.content.Intent;import android.graphics.Typeface;import android.graphics.drawable.Drawable;import android.net.Uri;import android.os.Build;import android.support.annotation.ColorInt;import android.support.annotation.NonNull;import android.support.annotation.Nullable;import android.support.v7.widget.CardView;import android.support.v7.widget.RecyclerView;import android.text.method.LinkMovementMethod;import android.view.LayoutInflater;import android.view.View;import android.view.ViewGroup;import android.widget.Button;import android.widget.TextView;import java.util.ArrayList;import java.util.Collection;import java.util.List;import butterknife.BindView;import butterknife.ButterKnife;import butterknife.OnClick;import info.nukoneko.android.ho_n.R;import info.nukoneko.android.ho_n.controller.common.view.NKRoundImageView;import info.nukoneko.android.ho_n.sys.util.image.NKPicasso;import info.nukoneko.android.ho_n.sys.util.rx.Optional;import info.nukoneko.android.ho_n.sys.util.text.NKTextLinkCallback;import info.nukoneko.android.ho_n.sys.util.text.NKTextUtil;import rx.Observable;import rx.functions.Action1;import twitter4j.Status;import twitter4j.User;/** * Created by TEJNEK on 2016/10/21. */public final class NKTweetAdapter extends RecyclerView.Adapter<NKTweetAdapter.TweetViewHolder> {    @NonNull    private final List<Status> data = new ArrayList<>();    private final Object lock = new Object();    private final Context context;    private final Drawable iconFavoriteOn;    private final Drawable iconFavoriteOff;    private final Drawable iconReTweetOn;    private final Drawable iconReTweetOff;    @ColorInt    private final int colorNormal;    @ColorInt    private final int colorReTweeted;    @ColorInt    private final int colorReTweetedByMe;    private final Typeface textViewFont;    public NKTweetAdapter(Context context) {        this.context = context;        textViewFont = Typeface.createFromAsset(context.getAssets(), "yutapon.ttc");        iconFavoriteOn = context.getDrawable(R.drawable.ic_favorite_on);        iconFavoriteOff = context.getDrawable(R.drawable.ic_favorite_off);        iconReTweetOn = context.getDrawable(R.drawable.ic_retweet_on);        iconReTweetOff = context.getDrawable(R.drawable.ic_re_tweet_off);        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {            colorNormal = context.getColor(android.R.color.white);            colorReTweeted = context.getColor(R.color.colorTweetReTweet);            colorReTweetedByMe = context.getColor(R.color.colorTweetReTweetByMe);        } else {            colorNormal = context.getResources().getColor(android.R.color.white);            colorReTweeted = context.getResources().getColor(R.color.colorTweetReTweet);            colorReTweetedByMe = context.getResources().getColor(R.color.colorTweetReTweetByMe);        }    }    @Override    public NKTweetAdapter.TweetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());        final View view = inflater.inflate(R.layout.item_list_tweet, parent, false);        NKTweetAdapter.TweetViewHolder holder = new NKTweetAdapter.TweetViewHolder(view);        setFontStyle(holder.tweetUserName);        setFontStyle(holder.tweetUserScreen);        setFontStyle(holder.tweetStatusText);        return holder;    }    @Override    public void onBindViewHolder(NKTweetAdapter.TweetViewHolder holder, int position) {        Optional.ofNullable(data.get(position)).subscribe(status -> {            holder.setStatus(status);            //*** Set User info ***/            User user = status.getUser();            // Icon            NKPicasso.getInstance()                    .load(user.getProfileImageURLHttps())                    .fit().centerCrop().into(holder.tweetUserIcon);            holder.tweetUserName.setText(user.getName());            holder.tweetUserScreen.setText(String.format("@%s", user.getScreenName()));            //*** Set Tweet info ***/            holder.tweetStatusText.setText(NKTextUtil.setLinkTag(status.getText(), new TweetLinkCallback(context)));            holder.tweetStatusText.setMovementMethod(LinkMovementMethod.getInstance());            //*** Set Tweet status **/            // Favorite            if (status.isFavorited()) {                holder.tweetStatusFavorite.setBackground(iconFavoriteOn);            } else {                holder.tweetStatusFavorite.setBackground(iconFavoriteOff);            }            // ReTweet            if (status.isRetweet()) {                if (status.isRetweetedByMe()) {                    holder.tweetView.setBackgroundColor(colorReTweetedByMe);                } else {                    holder.tweetView.setBackgroundColor(colorReTweeted);                }                holder.tweetStatusReTweet.setBackground(iconReTweetOn);            } else {                holder.tweetView.setBackgroundColor(colorNormal);                holder.tweetStatusReTweet.setBackground(iconReTweetOff);            }        });    }    @Override    public int getItemCount() {        return data.size();    }    public void clear() {        synchronized (lock) {            int itemCount = data.size();            data.clear();            notifyItemRangeRemoved(0, itemCount);        }    }    void addAll(@NonNull List<? extends Status> collection) {        synchronized (lock) {            long minStatusId = Long.MAX_VALUE;            long maxStatusId = Long.MIN_VALUE;            if (data.size() > 0) {                minStatusId = data.get(data.size() - 1).getId();                maxStatusId = data.get(0).getId();            }            long finalMaxStatusId = maxStatusId;            long finalMinStatusId = minStatusId;            Collection<? extends Status> filteredList =                    Observable.from(collection)                            .filter(s -> s.getId() > finalMaxStatusId || finalMinStatusId > s.getId()).toList()                            .toBlocking().single();            int itemCount = filteredList.size();            int startPosition = data.size();            data.addAll(filteredList);            notifyItemRangeInserted(startPosition, itemCount);        }    }    private void setFontStyle(TextView textView) {        textView.setTypeface(textViewFont, Typeface.NORMAL);    }    //*** TextLinkCallback    private class TweetLinkCallback extends NKTextLinkCallback {        TweetLinkCallback(Context context) {            super(context);        }        @Override        public void onClickUri(@NonNull String uri) {            super.onClickUri(uri);            getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)));        }        @Override        public void onClickTag(@NonNull String tag) {            super.onClickTag(tag);        }    }    //*** TweetViewHolder    static class TweetViewHolder extends RecyclerView.ViewHolder {        final View view;        @BindView(R.id.tweet_view)        CardView tweetView;        @BindView(R.id.tweet_user_icon)        NKRoundImageView tweetUserIcon;        @BindView(R.id.tweet_user_name)        TextView tweetUserName;        @BindView(R.id.tweet_user_screen)        TextView tweetUserScreen;        @BindView(R.id.tweet_status_favorite)        Button tweetStatusFavorite;        @BindView(R.id.tweet_status_re_tweet)        Button tweetStatusReTweet;        @BindView(R.id.tweet_status_text)        TextView tweetStatusText;        @OnClick(R.id.tweet_user_icon)        void onClickUserIcon(View view) {            Optional.ofNullable(listener).subscribe(listener1 -> {                listener1.onClickUserIcon(status);            });        }        @OnClick(R.id.tweet_status_favorite)        void onClickStatusFavorite(View view) {            Optional.ofNullable(listener).subscribe(listener1 -> {                listener1.onClickTweetFavorite(status);            });        }        @OnClick(R.id.tweet_status_re_tweet)        void onClickStatusReTweet(View view) {            Optional.ofNullable(listener).subscribe(listener1 -> {                listener1.onClickTweetReTweet(status);            });        }        @Nullable OnClickTweetListener listener;        Status status;        TweetViewHolder(View itemView) {            super(itemView);            this.view = itemView;            if (itemView.getContext() instanceof OnClickTweetListener) {                this.listener = (OnClickTweetListener) itemView.getContext();            }            ButterKnife.bind(this, itemView);        }        void setStatus(Status status) {            this.status = status;        }    }}