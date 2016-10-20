package info.nukoneko.android.ho_n.util.file;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import info.nukoneko.android.ho_n.R;
import info.nukoneko.android.ho_n.util.rx.Optional;

/**
 * Created by atsumi on 2016/10/20.
 */

public final class NKFileUtil {
    private NKFileUtil() {
    }

    /**
     * 撮影のためにカメラを起動します
     *
     * @param activity    activity
     * @param requestCode requestCode
     * @return file uri
     */
    @Nullable
    public static Uri openCamera(@NonNull Activity activity, int requestCode) {

        File dir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                activity.getString(R.string.app_name_system));
        if (!dir.exists() && !dir.mkdirs()) {
            dir = null;
        } else {
            dir = new File(dir, "" + new SimpleDateFormat("yyyMMddHHmmss", Locale.getDefault()).format(new Date()) + ".jpg");
        }

        if (dir == null) {
            return null;
        }

        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri bitmapUri;
        if (Build.VERSION_CODES.N > Build.VERSION.SDK_INT) {
            bitmapUri = Uri.fromFile(dir);
        } else {
            bitmapUri = FileProvider.getUriForFile(activity, activity.getString(R.string.app_package), dir);
        }

        i.putExtra(MediaStore.EXTRA_OUTPUT, bitmapUri);
        activity.startActivityForResult(i, requestCode);

        return bitmapUri;
    }

    /**
     * 画像取得のためにギャラリーを起動します
     *
     * @param activity    activity
     * @param requestCode requestCode
     */
    public static void openGallery(@NonNull Activity activity, int requestCode) {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        intent.setType("image/*");
        activity.startActivityForResult(Intent.createChooser(intent, "Pick"), requestCode);
    }

    /**
     * インテント後のURIから実際のパスを取得する
     *
     * @param context context
     * @param data    data
     * @return uri
     */
    @Nullable
    public static Uri getPathFromIntent(@NonNull Context context, Intent data) {
        Uri returnUri = null;
        if (Build.VERSION.SDK_INT >= 19) {
            returnUri = NKFilePathUtil.getPath(context, data.getData());
            if (returnUri == null) {
                Optional.ofNullable(data.getData()).subscribe(uri -> {
                    String errorText =
                            String.format("URI\t\t: %s\nID\t\t: %s\nAUTHORITY\t: %s",
                                    uri.toString(),
                                    DocumentsContract.getDocumentId(uri),
                                    uri.getAuthority());
                    Log.i("failed load file path", errorText);
                });
            }
        } else {
            Cursor cursor = context.getContentResolver().query(
                    data.getData(),
                    new String[]{MediaStore.Images.Media.DATA
                    }, null, null, null);

            if (cursor != null) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                returnUri = Uri.parse(cursor.getString(column_index));
                cursor.close();
            }
        }
        return returnUri;
    }
}
