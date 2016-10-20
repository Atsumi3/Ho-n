package info.nukoneko.android.ho_n.common.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by atsumi on 2016/10/20.
 */

public abstract class NKMaskImageViewAbstract extends ImageView {
    private Paint mMaskedPaint;
    private Paint mCopyPaint;
    private Drawable mMaskDrawable;

    private Rect mBounds;
    private RectF mBoundsF;

    @DrawableRes
    abstract int maskDrawableId();

    public NKMaskImageViewAbstract(Context context) {
        this(context, null);
    }

    public NKMaskImageViewAbstract(Context context, AttributeSet attrs) {
        super(context, attrs);

        mMaskedPaint = new Paint();
        mMaskedPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));

        mCopyPaint = new Paint();

        loadMaskDrawable();
    }

    @SuppressWarnings("deprecation")
    private void loadMaskDrawable(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            mMaskDrawable = getContext().getDrawable(maskDrawableId());
        }
        else{
            mMaskDrawable = getContext().getResources().getDrawable(maskDrawableId());
        }
    }

    final protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mBounds = new Rect(0, 0, w, h);
        mBoundsF = new RectF(mBounds);
    }

    @Override
    final protected void onDraw(Canvas canvas) {
        final int sc = canvas.saveLayer(mBoundsF, mCopyPaint,
                Canvas.HAS_ALPHA_LAYER_SAVE_FLAG | Canvas.FULL_COLOR_LAYER_SAVE_FLAG);

        mMaskDrawable.setBounds(mBounds);
        mMaskDrawable.draw(canvas);

        canvas.saveLayer(mBoundsF, mMaskedPaint, 0);

        super.onDraw(canvas);

        canvas.restoreToCount(sc);
    }
}
