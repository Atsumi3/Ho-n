package info.nukoneko.android.ho_n.common.view;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;

/**
 * Created by atsumi on 2016/10/20.
 */

public final class NKSwipeRefreshLayout extends SwipeRefreshLayout {
    public NKSwipeRefreshLayout(Context context){
        this(context, null);
    }

    public NKSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setColorSchemeResources(
                android.R.color.holo_orange_light,
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_purple);
    }
}
