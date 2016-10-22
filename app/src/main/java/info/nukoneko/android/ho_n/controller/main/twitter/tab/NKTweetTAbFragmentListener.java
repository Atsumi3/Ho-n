package info.nukoneko.android.ho_n.controller.main.twitter.tab;

import twitter4j.User;

/**
 * Created by atsumi on 2016/10/21.
 */

public interface NKTweetTabFragmentListener {
    void refreshEnd();
    void updateUser(User user);
}
