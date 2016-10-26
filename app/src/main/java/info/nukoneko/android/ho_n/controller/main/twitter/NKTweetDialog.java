package info.nukoneko.android.ho_n.controller.main.twitter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;

import butterknife.BindView;
import butterknife.OnClick;
import info.nukoneko.android.ho_n.R;
import info.nukoneko.android.ho_n.sys.base.BaseDialogFragment;
import info.nukoneko.android.ho_n.sys.util.image.NKPicasso;
import info.nukoneko.android.ho_n.sys.util.rx.Optional;
import info.nukoneko.android.ho_n.sys.util.rx.RxUtil;
import info.nukoneko.android.ho_n.sys.util.rx.RxWrap;
import info.nukoneko.android.ho_n.sys.util.text.NKToast;
import info.nukoneko.android.ho_n.sys.util.twitter.NKTwitterUtil;
import info.nukoneko.android.ho_n.sys.util.view.NKProgressUtil;
import rx.Observable;
import rx.android.plugins.RxAndroidPlugins;
import rx.functions.Action1;
import rx.functions.Func1;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.User;

/**
 * Created by atsumi on 2016/10/25.
 */

public final class NKTweetDialog extends BaseDialogFragment {
    static private final String EXTRA_REPLY_TWEET = "replay_tweet";
    static private final String EXTRA_USER_ID = "user_id";

    @BindView(R.id.tweet_action_tweet_view)
    RelativeLayout tweetBackView;

    @BindView(R.id.tweet_action_tweet_target_view)
    RelativeLayout tweetTargetView;

    @BindView(R.id.tweet_action_tweet_target_text)
    TextView tweetTargetTextView;

    @BindView(R.id.tweet_action_tweet_target_user_icon)
    ImageView tweetTargetUserIconImageView;

    @BindView(R.id.tweet_action_tweet_target_user_name)
    TextView tweetTargetUserNameTextView;

    @BindView(R.id.tweet_action_tweet_text)
    EditText tweetEditText;

    @BindView(R.id.tweet_action_tweet_send)
    ImageButton tweetSendButton;

    @OnClick(R.id.btn_cancel)
    void onClickCancel(View view) {
        dismiss();
    }

    @OnClick(R.id.tweet_action_tweet_send)
    void onClickSend(View view) {
        Optional.ofNullable(NKTwitterUtil.getInstance(getContext(), currentUserId))
                .subscribe(twitter -> {
                    final StatusUpdate statusUpdate = new StatusUpdate(tweetEditText.getText().toString());

                    if (targetStatus != null) {
                        statusUpdate.setInReplyToStatusId(targetStatus.getId());
                    }

                    RxWrap.create(RxUtil.createObservable(() -> twitter.updateStatus(statusUpdate)),
                            NKProgressUtil.createProgress(getContext(), R.string.sending),
                            bindToLifecycle())
                            .subscribe(status -> {
                                Optional.ofParsable(getContext(), NKTweetDialogListener.class)
                                        .subscribe(nkTweetDialogListener -> {
                                            nkTweetDialogListener.onTweetDialogTweetSuccess(status);
                                        });
                                dismiss();
                            }, throwable -> {
                                Optional.ofParsable(getContext(), NKTweetDialogListener.class)
                                        .subscribe(nkTweetDialogListener -> {
                                            nkTweetDialogListener.onTweetDialogTweetFailed(throwable);
                                        });
                            });
                });
    }

    /**
     * リプライの時用のツイート
     */
    @Nullable
    Status targetStatus = null;

    long currentUserId = -1;

    ////////// Abstract Function ///////////
    @Override
    public int dialogLayoutId() {
        return R.layout.dialog_tweet;
    }

    @NonNull
    @Override
    public Dialog dialogSetup(Dialog dialog) {
        return dialog;
    }

    @Override
    public void dialogSetupParams(Bundle bundle) {
        Optional.ofNullable(bundle).subscribe(bundle1 -> {
            Optional.ofParsable(bundle1.getSerializable(EXTRA_REPLY_TWEET), Status.class)
                    .subscribe(status -> {
                        targetStatus = status;
                    });

            Optional.ofParsable(bundle1.getSerializable(EXTRA_USER_ID), Long.class)
                    .subscribe(userId -> {
                        currentUserId = userId;
                    });
        });

        if (currentUserId == -1) {
            dismiss();
        }

        if (targetStatus == null) {
            // new Tweet
            tweetTargetView.setVisibility(View.GONE);
        } else {
            tweetTargetView.setVisibility(View.VISIBLE);
            tweetTargetTextView.setText(targetStatus.getText());

            User user;
            if (targetStatus.isRetweet()) {
                user = targetStatus.getRetweetedStatus().getUser();
            } else {
                user = targetStatus.getUser();
            }
            NKPicasso.getInstance()
                    .load(user.getProfileImageURLHttps()).into(tweetTargetUserIconImageView);

            tweetTargetUserNameTextView.setText(user.getName());

            tweetEditText.setText(String.format("@%s ", user.getScreenName()));
            tweetEditText.setSelection(tweetEditText.getText().length());
        }

        tweetSendButton.setEnabled(false);

        RxTextView.textChanges(tweetEditText)
                .compose(bindToLifecycle())
                .map(charSequence -> charSequence.length() > 0)
                .subscribe(enable -> {
                    tweetSendButton.setEnabled(enable);
                });

        RxView.clicks(tweetBackView).subscribe(aVoid -> {
            Optional
                    .ofParsable(getContext().getSystemService(Context.INPUT_METHOD_SERVICE), InputMethodManager.class)
                    .subscribe(imm -> {
                        imm.hideSoftInputFromWindow(tweetBackView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    });
        });
    }
    ////////////////////////////////////////

    static public NKTweetDialog newInstance(long userId) {
        NKTweetDialog dialog = new NKTweetDialog();
        Bundle bundle = new Bundle();
        bundle.putLong(EXTRA_USER_ID, userId);
        dialog.setArguments(bundle);
        return dialog;
    }

    static public NKTweetDialog newInstance(long userId, Status status) {
        NKTweetDialog dialog = new NKTweetDialog();
        Bundle bundle = new Bundle();
        bundle.putLong(EXTRA_USER_ID, userId);

        if (status.isRetweet()) {
            bundle.putSerializable(EXTRA_REPLY_TWEET, status.getRetweetedStatus());
        } else {
            bundle.putSerializable(EXTRA_REPLY_TWEET, status);
        }

        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    }

    @Override
    public void onStart() {
        super.onStart();

//        Dialog dialog = getDialog();
//        if (dialog != null)
//        {
//            int width = ViewGroup.LayoutParams.MATCH_PARENT;
//            int height = ViewGroup.LayoutParams.MATCH_PARENT;
//            dialog.getWindow().setLayout(width, height);
//        }
    }
}
