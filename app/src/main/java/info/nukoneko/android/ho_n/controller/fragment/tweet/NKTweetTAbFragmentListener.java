package info.nukoneko.android.ho_n.controller.fragment.tweet;

import twitter4j.User;

/**
 * Created by atsumi on 2016/10/21.
 */

public interface NKTweetTAbFragmentListener {
    void refreshEnd();
    void updateUser(User user);
}
