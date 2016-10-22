package info.nukoneko.android.ho_n.controller.main.twitter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import butterknife.BindString;
import butterknife.ButterKnife;
import info.nukoneko.android.ho_n.R;
import info.nukoneko.android.ho_n.controller.main.NKMainActivity;
import info.nukoneko.android.ho_n.sys.base.BaseActivity;
import info.nukoneko.android.ho_n.sys.util.rx.Optional;
import info.nukoneko.android.ho_n.sys.util.rx.RxUtil;
import info.nukoneko.android.ho_n.sys.util.rx.RxWrap;
import info.nukoneko.android.ho_n.sys.util.twitter.NKTwitterUtil;
import twitter4j.Twitter;
import twitter4j.auth.RequestToken;

/**
 * Created by atsumi on 2016/10/19.
 */

public final class NKTwitterAuthActivity extends BaseActivity {

    // Twitter callback
    @BindString(R.string.twitter_callback_uri)
    String twitterAuthCallbackUri;

    @Nullable
    Twitter twitterAuthInstance;
    @Nullable
    RequestToken twitterAuthRequestToken;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        startAuth();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Optional.ofNullable(intent.getData()).subscribe(uri -> {
            if (!uri.toString().startsWith(twitterAuthCallbackUri)) return;
            RxWrap.create(RxUtil.createObservable(() -> {
                if (twitterAuthInstance == null) return null;
                return twitterAuthInstance
                        .getOAuthAccessToken(
                                twitterAuthRequestToken,
                                uri.getQueryParameter("oauth_verifier"));
            }), bindToLifecycle())
                    .onErrorReturn(throwable -> null)
                    .subscribe(accessToken -> {
                        Optional.of(accessToken).subscribe(NKTwitterUtil::addAccount);
                        startActivity(new Intent(this, NKMainActivity.class));
                        finish();
                    }, throwable -> {
                        Log.e("failed get token", throwable.getLocalizedMessage());
                    });
        });
    }

    private void startAuth() {
        twitterAuthInstance = NKTwitterUtil.getInstance(this);
        assert twitterAuthInstance != null;
        RxWrap.create(RxUtil.createObservable(() -> {
            twitterAuthInstance.setOAuthAccessToken(null);
            twitterAuthRequestToken =
                    twitterAuthInstance.getOAuthRequestToken(twitterAuthCallbackUri);
            return twitterAuthRequestToken.getAuthorizationURL();
        }), bindToLifecycle())
                .subscribe(s -> {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(s)));
                }, throwable -> {
                    Log.e("failed start auth", throwable.getLocalizedMessage());
                });
    }
}
