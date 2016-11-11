package info.nukoneko.android.ho_n.controller.main.twitter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.io.Serializable;

import info.nukoneko.android.ho_n.R;
import info.nukoneko.android.ho_n.databinding.DialogTweetBinding;
import info.nukoneko.android.ho_n.sys.base.BaseDialogFragment;
import info.nukoneko.android.ho_n.sys.util.rx.Optional;
import info.nukoneko.android.ho_n.sys.util.rx.RxUtil;
import info.nukoneko.android.ho_n.sys.util.rx.RxWrap;
import info.nukoneko.android.ho_n.sys.util.twitter.NKTwitterUtil;
import info.nukoneko.android.ho_n.sys.util.view.NKProgressUtil;
import rx.functions.Action1;
import twitter4j.Status;
import twitter4j.StatusUpdate;

/**
 * Created by atsumi on 2016/10/25.
 */

public final class NKTweetDialog extends BaseDialogFragment {
    static private final String EXTRA_REPLY_TWEET = "replay_tweet";
    static private final String EXTRA_USER_ID = "user_id";

    static public NKTweetDialog newInstance(long userId) {
        return newInstance(userId, null);
    }

    static public NKTweetDialog newInstance(long userId, @Nullable Status status) {
        NKTweetDialog dialog = new NKTweetDialog();
        Bundle bundle = new Bundle();
        bundle.putLong(EXTRA_USER_ID, userId);

        if (status != null) {
            if (status.isRetweet()) {
                bundle.putSerializable(EXTRA_REPLY_TWEET, status.getRetweetedStatus());
            } else {
                bundle.putSerializable(EXTRA_REPLY_TWEET, status);
            }
        }

        dialog.setArguments(bundle);
        return dialog;
    }

    public void onClickCancel(View view) {
        dismiss();
    }

    public void onClickSend(View view) {
        Optional.ofNullable(NKTwitterUtil.getInstance(getContext(), getUserId()))
                .subscribe(twitter -> {
                    final StatusUpdate statusUpdate = new StatusUpdate(binding.tweetActionTweetText.getText().toString());

                    if (getStatus() != null) {
                        statusUpdate.setInReplyToStatusId(getStatus().getId());
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

    private DialogTweetBinding binding;

    @Override
    public void dialogSetupParams(Bundle bundle) {
        binding = DialogTweetBinding.bind(getView());
        binding.setUserId(getUserId());
        binding.setDialog(this);

        Optional.ofNullable(getStatus()).subscribe(status -> {
            binding.setTweet(status);
            RxView.globalLayouts(binding.tweetActionTweetText).subscribe(aVoid -> {
                binding.tweetActionTweetText.setSelection(binding.tweetActionTweetText.getText().length());
            });
        });

        binding.tweetActionTweetSend.setEnabled(false);

        RxTextView.textChanges(binding.tweetActionTweetText)
                .compose(bindToLifecycle())
                .map(charSequence -> charSequence.length() > 0)
                .subscribe(enable -> {
                    binding.tweetActionTweetSend.setEnabled(enable);
                });

        RxView.clicks(binding.btnCancel).subscribe(aVoid -> {
            Optional
                    .ofParsable(getContext().getSystemService(Context.INPUT_METHOD_SERVICE), InputMethodManager.class)
                    .subscribe(imm -> {
                        imm.hideSoftInputFromWindow(binding.btnCancel.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    });
        });
    }
    ////////////////////////////////////////



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private Status getStatus() {
        Serializable serializable = getArguments().getSerializable(EXTRA_REPLY_TWEET);
        if (serializable != null && serializable instanceof Status) {
            return (Status) serializable;
        }
        return null;
    }

    private long getUserId() {
        return getArguments().getLong(EXTRA_USER_ID);
    }
}
