package info.nukoneko.android.ho_n.sys.util.text;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

/**
 * Created by atsumi on 2016/10/20.
 */

public abstract class NKTextLinkCallback {
    private final Context context;
    public NKTextLinkCallback(Context context) {
        this.context = context;
    }

    public void onClickUri(@NonNull String uri){
        Log.d("onClickUri", uri);
    }
    public void onClickTag(@NonNull String tag){
        Log.d("onClickTag", tag);
    }
    public void onClickScreenName(@NonNull String screenName){
        Log.d("onClickTag", screenName);
    }

    public Context getContext() {
        return context;
    }
}
