package info.nukoneko.android.ho_n.controller.common.view;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

/**a
 * Created by atsumi on 2016/10/20.
 */

/**
 * TextViewの大きさに合わせて文字の大きさを変えるView
 * 調整中に付き非推奨
 */
@Deprecated
public final class NKFontFitTextView extends TextView {
    private static final float MIN_TEXT_SIZE = 3.0f;
    private Paint mPaint = new Paint();

    public NKFontFitTextView(Context context) {
        this(context, null);
    }

    public NKFontFitTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NKFontFitTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getMeasuredWidth() > 0) {
            resize();
        }
    }
    private void resize() {
        float tempTextSize = getTextSize();
        while (getMeasuredWidth() < getTextWidth(tempTextSize)) {
            tempTextSize--;
            if (tempTextSize <= MIN_TEXT_SIZE) {
                tempTextSize = MIN_TEXT_SIZE;
                break;
            }
        }

        setTextSize(TypedValue.COMPLEX_UNIT_PX, tempTextSize);
    }
    float getTextWidth(float textSize) {
        mPaint.setTextSize(textSize);
        return mPaint.measureText(getText().toString());
    }
}
