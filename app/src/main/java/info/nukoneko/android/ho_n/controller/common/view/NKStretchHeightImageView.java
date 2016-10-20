package info.nukoneko.android.ho_n.controller.common.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by atsumi on 2016/10/20.
 */

public final class NKStretchHeightImageView extends ImageView {
    public NKStretchHeightImageView(Context context) {
        this(context, null, 0);
    }

    public NKStretchHeightImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NKStretchHeightImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Drawable d = getDrawable();
        if (d != null) {
            final int width = MeasureSpec.getSize(widthMeasureSpec);
            final int height = (int)Math.ceil((float)width * (float) d.getIntrinsicHeight()/ (float) d.getIntrinsicWidth());
            setMeasuredDimension(width, height);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
