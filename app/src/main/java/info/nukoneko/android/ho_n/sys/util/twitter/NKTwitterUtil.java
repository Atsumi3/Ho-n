package info.nukoneko.android.ho_n.sys.util.twitter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;

import butterknife.BindString;
import info.nukoneko.android.ho_n.R;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import rx.Observable;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by atsumi on 2016/10/20.
 */

public final class NKTwitterUtil {
    private NKTwitterUtil() {
    }

    /*
    * Twitterインスタンスを取得します。
    *
    * @param context
    * @return
    */
    @Nullable
    public static Twitter getInstance(@NonNull Context context) {
        return getInstance(context, 0);
    }

    @Nullable
    public static Twitter getInstance(@NonNull Context context, long userId) {
        Configuration configuration = getConfiguration(context, userId);

        // 何か間違いがあったらnullを返す
        if (configuration == null) return null;

        return new TwitterFactory(configuration).getInstance();
    }

    /*
    * TwitterStreamインスタンスを取得します。
    *
    * @param context
    * @return
    */
    @Nullable
    public static TwitterStream getStreamInstance(@NonNull Context context){
        return getStreamInstance(context, 0);
    }

    @Nullable
    public static TwitterStream getStreamInstance(@NonNull Context context, long userId){
        Configuration configuration = getConfiguration(context, userId);

        // 何か間違いがあったらnullを返す
        if (configuration == null) return null;

        return new TwitterStreamFactory(configuration).getInstance();
    }

    /***
     * Twitterインスタンス生成用のコンフィギュレーションを生成します
     * @param context context
     * @param userId userId
     * @return Config
     */
    @Nullable
    private static Configuration getConfiguration(Context context, long userId) {
        String consumerKey = context.getString(R.string.twitter_consumer_key);
        String consumerSecret = context.getString(R.string.twitter_consumer_secret);
        ConfigurationBuilder builder = new ConfigurationBuilder();

        builder.setOAuthConsumerKey(consumerKey);
        builder.setOAuthConsumerSecret(consumerSecret);

        // ユーザーIDが0かそれ以下ならそのまま
        if (1 > userId) return builder.build();

        // ユーザーIDがあるのにアクセストークンなかったらnull
        if (!hasAccessToken()) return null;

        // 指定IDのアクセストークンを取得
        AccessToken accessToken = loadAccessToken(userId);

        if (accessToken == null) {
            // 指定IDのトークンが存在しなかったらnull
            return null;
        }

        // 指定IDが存在したらセットして返す
        builder.setOAuthAccessToken(accessToken.getToken());
        builder.setOAuthAccessTokenSecret(accessToken.getTokenSecret());
        return builder.build();
    }

    @Nullable
    private static AccessToken loadAccessToken(long userId) {
        NKModelAccessTokenObject tokenObject = getAccount(userId);
        if (tokenObject == null){
            return null;
        }
        return new AccessToken(tokenObject.getUserToken(), tokenObject.getUserTokenSecret(), tokenObject.getUserId());
    }

    /***
     * アクセストークンが存在する場合はtrueを返します。
     * @return false or true
     */
    private static boolean hasAccessToken() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<NKModelAccessTokenObject> s = realm.where(NKModelAccessTokenObject.class).findAll();
        return s.size() > 0;
    }

    /* ---  database 操作 --- */

    public static void addAccount(AccessToken accessToken){
        NKModelAccessTokenObject tokenObject = new NKModelAccessTokenObject();
        tokenObject.setUserId(accessToken.getUserId());
        tokenObject.setUserName(accessToken.getScreenName());
        tokenObject.setUserScreenName(accessToken.getScreenName());
        tokenObject.setUserToken(accessToken.getToken());
        tokenObject.setUserTokenSecret(accessToken.getTokenSecret());

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(tokenObject);
        realm.commitTransaction();

        realm.close();
    }


    private static void deleteAccount(Long userId){
        Realm realm = Realm.getDefaultInstance();
        NKModelAccessTokenObject result = realm.where(NKModelAccessTokenObject.class).equalTo("userId", userId).findFirst();
        if (result == null){
            return;
        }
        realm.beginTransaction();
        result.deleteFromRealm();
        realm.commitTransaction();
    }

    public static void deleteAllAccount(){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<NKModelAccessTokenObject> result = realm.where(NKModelAccessTokenObject.class).findAll();
        realm.beginTransaction();
        result.deleteAllFromRealm();
        realm.commitTransaction();
    }

    public static ArrayList<Long> getAccountIds() {
        Realm realm = Realm.getDefaultInstance();
        ArrayList<Long> results = new ArrayList<>();
        results.addAll(Observable.from(
                realm.where(NKModelAccessTokenObject.class).findAll())
                .map(NKModelAccessTokenObject::getUserId).toList().toBlocking().single()
        );
        return results;
    }

    public static ArrayList<NKModelAccessTokenObject> getAccounts() {
        Realm realm = Realm.getDefaultInstance();
        ArrayList<NKModelAccessTokenObject> results = new ArrayList<>();

        results.addAll(Observable.from(
                realm.where(NKModelAccessTokenObject.class).findAll()).toList().toBlocking().single());
        return results;
    }

    @Nullable
    private static NKModelAccessTokenObject getAccount(Long userId) {
        Realm realm = Realm.getDefaultInstance();
        return realm
                .where(NKModelAccessTokenObject.class)
                .equalTo("userId", userId)
                .findFirst();
    }
}
