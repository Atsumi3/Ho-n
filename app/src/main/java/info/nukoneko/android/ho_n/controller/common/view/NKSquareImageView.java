package info.nukoneko.android.ho_n.controller.common.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by atsumi on 2016/10/20.
 */

public final class NKSquareImageView extends ImageView {
    public NKSquareImageView(Context context) {
        this(context, null);
    }

    public NKSquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
