package info.nukoneko.android.ho_n.sys.util.text;

import android.support.annotation.NonNull;
import android.util.Log;

/**
 * Created by atsumi on 2016/10/20.
 */

public abstract class NKTextLinkCallback {
    public void onClickUri(@NonNull String uri){
        Log.d("onClickUri", "NotImplemented");
    }
    public void onClickTag(@NonNull String tag){
        Log.d("onClickTag", "NotImplemented");
    }
}
