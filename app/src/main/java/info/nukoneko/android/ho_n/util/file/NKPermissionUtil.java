package info.nukoneko.android.ho_n.util.file;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import rx.Observable;

/**
 * Created by atsumi on 2016/10/20.
 */

public final class NKPermissionUtil {
    private NKPermissionUtil(){}

    ///// Permission Check
    /**
     * カメラ ギャラリーの使用許可を得たい
     * @param context context
     * @param requestCode requestCode
     * @return granted
     */
    public static boolean checkCameraPermissions(Activity context, int requestCode) {
        String[] permissions = new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        return checkPermissions(context, permissions, requestCode);
    }

    /**
     * 位置情報の許可を得たい
     * @param context context
     * @param requestCode requestCode
     * @return granted
     */
    public static boolean checkLocationPermissions(Activity context, int requestCode) {
        String[] permissions = new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        };

        return checkPermissions(context, permissions, requestCode);
    }

    /**
     * Android6.0以上向けに パーミッションをチェックする
     * @param context context
     * @param permissions Manifest.permission.*
     * @param requestCode requestCode
     * @return granted
     */
    private static boolean checkPermissions(Activity context, String[] permissions, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // 許可されていない項目の数をカウント
            final int agreedCount = Observable.from(permissions).reduce(0, (value, perm) -> {
                final boolean status =
                        context.checkSelfPermission(perm) != PackageManager.PERMISSION_GRANTED;
                return value + (status ? 0 : 1);
            }).toBlocking().single();

            // 一つでも許可されていなかったら
            if (agreedCount > 0) {
                if (permissions.length != agreedCount) {
                    Toast.makeText(context, "この機能の利用には許可が必要です", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.fromParts("package", context.getPackageName(), null));
                    context.startActivityForResult(intent, requestCode);
                } else {
                    context.requestPermissions(permissions, requestCode);
                }
                return false;
            }
        }
        return true;
    }
}
