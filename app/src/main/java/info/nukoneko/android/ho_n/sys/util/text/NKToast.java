package info.nukoneko.android.ho_n.sys.util.text;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.Toast;

import info.nukoneko.android.ho_n.R;
import info.nukoneko.android.ho_n.sys.NKAppController;
import info.nukoneko.android.ho_n.sys.util.rx.Optional;

/**
 * Created by atsumi on 2016/10/20.
 */

public final class NKToast {

    @LayoutRes static final int toastLayoutId = R.layout.item_toast_simple;

    private static void showToast(@NonNull Context context, @NonNull String text){
        Optional.of(LayoutInflater.from(context).inflate(toastLayoutId, null)).subscribe(view -> {
            if (view instanceof TextView) {
                ((TextView) view).setGravity(Gravity.CENTER_HORIZONTAL);
                ((TextView) view).setText(text);
            } else {
                Log.d("Toast Error", "Toast is not TextView");
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
                return;
            }

            Toast toast = new Toast(context);
            toast.setView(view);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.show();
        }, throwable -> {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        });
    }

    public static void show(@Nullable Context context, @StringRes int textID){
        Context ctx = context;
        if (ctx == null) {
            ctx = NKAppController.getApp().getApplicationContext();
        }
        showToast(ctx, ctx.getString(textID));
    }

    public static void show(@Nullable Context context, @NonNull String text) {
        Context ctx = context;
        if (ctx == null) {
            ctx = NKAppController.getApp().getApplicationContext();
        }
        showToast(ctx, text);
    }
}
