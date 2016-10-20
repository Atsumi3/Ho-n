package info.nukoneko.android.ho_n.controller.common.anim;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by atsumi on 2016/10/20.
 */

/**
 * View を上下に動かしたいときのアニメーション
 */
public final class NKStretchHeightAnimation extends Animation {
    private int targetHeight;
    private int startHeight;

    private View view;

    public NKStretchHeightAnimation(View view, int startHeight, int targetHeight) {
        this.view = view;
        this.targetHeight = targetHeight;
        this.startHeight = startHeight;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        view.getLayoutParams().height = (int)(startHeight + (targetHeight - startHeight)*interpolatedTime);
        view.requestLayout();
    }

    @Override
    public void initialize(int width, int height, int parentWidth,
                           int parentHeight) {
        super.initialize(width, height, ((View)view.getParent()).getWidth(), parentHeight);
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}
