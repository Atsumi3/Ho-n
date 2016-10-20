package info.nukoneko.android.ho_n.controller.common.view;

import android.content.Context;
import android.util.AttributeSet;

import info.nukoneko.android.ho_n.R;

/**
 * Created by atsumi on 2016/10/20.
 */

public final class NKRoundImageView extends NKMaskImageViewAbstract {
    public NKRoundImageView(Context context) {
        super(context);
    }

    public NKRoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    int maskDrawableId() {
        return R.drawable.mask_image_round;
    }
}
