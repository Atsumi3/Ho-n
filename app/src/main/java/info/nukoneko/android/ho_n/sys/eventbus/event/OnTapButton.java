package info.nukoneko.android.ho_n.sys.eventbus.event;

import android.view.View;

import info.nukoneko.android.ho_n.sys.eventbus.NKEvent;

/**
 * Created by atsumi on 2016/10/20.
 */

public final class OnTapButton implements NKEvent {
    final View view;
    public OnTapButton(View button) {
        this.view = button;
    }
}
