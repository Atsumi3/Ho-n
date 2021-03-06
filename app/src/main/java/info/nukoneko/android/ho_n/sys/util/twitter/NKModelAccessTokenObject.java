package info.nukoneko.android.ho_n.sys.util.twitter;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by atsumi on 2016/10/20.
 */

public class NKModelAccessTokenObject extends RealmObject {
    @PrimaryKey
    private Long userId;

    private String userName;
    private String userScreenName;
    private String userToken;
    private String userTokenSecret;

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserScreenName(String userScreenName) {
        this.userScreenName = userScreenName;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public void setUserTokenSecret(String userTokenSecret) {
        this.userTokenSecret = userTokenSecret;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserScreenName() {
        return userScreenName;
    }

    public String getUserToken() {
        return userToken;
    }

    public String getUserTokenSecret() {
        return userTokenSecret;
    }
}
