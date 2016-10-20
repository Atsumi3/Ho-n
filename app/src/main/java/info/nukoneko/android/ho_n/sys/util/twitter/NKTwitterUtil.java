package info.nukoneko.android.ho_n.sys.util.twitter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;

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
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by atsumi on 2016/10/20.
 */

public final class NKTwitterUtil {
    private NKTwitterUtil() {
    }

    /*
    * Twitterインスタンスを取得します。アクセストークンが保存されていれば自動的にセットします。
    *
    * @param context
    * @return
    */
    public static Twitter getTwitterInstance(@NonNull Context context, long userId) {
        String consumerKey = context.getString(R.string.twitter_consumer_key);
        String consumerSecret = context.getString(R.string.twitter_consumer_secret);

        TwitterFactory factory = new TwitterFactory();
        Twitter twitter = factory.getInstance();
        twitter.setOAuthConsumer(consumerKey, consumerSecret);

        if (userId > 0 && hasAccessToken()) {
            twitter.setOAuthAccessToken(loadAccessToken(userId));
        }
        return twitter;
    }

    public static TwitterStream getTwitterStreamInstance(@NonNull Context context, long userId){
        String consumerKey = context.getString(R.string.twitter_consumer_key);
        String consumerSecret = context.getString(R.string.twitter_consumer_secret);
        ConfigurationBuilder builder = new ConfigurationBuilder();
        {
            builder.setOAuthConsumerKey(consumerKey);
            builder.setOAuthConsumerSecret(consumerSecret);
            AccessToken accessToken = loadAccessToken(userId);
            if (accessToken != null) {
                builder.setOAuthAccessToken(accessToken.getToken());
                builder.setOAuthAccessTokenSecret(accessToken.getTokenSecret());
                Log.d("LoadToken", accessToken.getToken() + " " + accessToken.getTokenSecret());
            } else {
                Log.d("LoadToken", "Access token is null");
                deleteAccount(userId);
                return null;
            }
        }
        twitter4j.conf.Configuration configuration = builder.build();
        return new TwitterStreamFactory(configuration).getInstance();
    }

    @Nullable
    public static AccessToken loadAccessToken(long userId) {
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
    public static boolean hasAccessToken() {
        Realm realm = Realm.getInstance(sharedConfigration());
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

        Realm realm = Realm.getInstance(sharedConfigration());
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(tokenObject);
        realm.commitTransaction();

        realm.close();
    }


    public static void deleteAccount(Long userId){
        Realm realm = Realm.getInstance(sharedConfigration());
        NKModelAccessTokenObject result = realm.where(NKModelAccessTokenObject.class).equalTo("userId", userId).findFirst();
        if (result == null){
            return;
        }
        realm.beginTransaction();
        result.deleteFromRealm();
        realm.commitTransaction();
    }

    public static void deleteAllAccount(){
        Realm realm = Realm.getInstance(sharedConfigration());
        RealmResults<NKModelAccessTokenObject> result = realm.where(NKModelAccessTokenObject.class).findAll();
        realm.beginTransaction();
        result.deleteAllFromRealm();
        realm.commitTransaction();
    }

    public static ArrayList<Long> getAccountIds() {
        Realm realm = Realm.getInstance(sharedConfigration());
        ArrayList<Long> results = new ArrayList<>();
        for (NKModelAccessTokenObject token : realm.where(NKModelAccessTokenObject.class).findAll()){
            try {
                results.add(token.getUserId());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return results;
    }

    public static ArrayList<NKModelAccessTokenObject> getAccounts() {
        Realm realm = Realm.getInstance(sharedConfigration());
        ArrayList<NKModelAccessTokenObject> results = new ArrayList<>();

        results.addAll(Observable.from(
                realm.where(NKModelAccessTokenObject.class).findAll()).toList().toBlocking().single());
        return results;
    }

    @Nullable
    public static NKModelAccessTokenObject getAccount(Long userId) {

        Realm realm = Realm.getInstance(sharedConfigration());
        return realm
                .where(NKModelAccessTokenObject.class)
                .equalTo("userId", userId)
                .findFirst();
    }

    private static RealmConfiguration sharedConfigration(){
        return new RealmConfiguration.Builder()
                .name("tw.account.realm")
                .schemaVersion(5)
                .modules(new NKModelAccessTokenObject())
                .build();
    }
}
