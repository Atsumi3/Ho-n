package info.nukoneko.android.ho_n.sys.util;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.crashlytics.android.Crashlytics;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.zip.ZipFile;

import info.nukoneko.android.ho_n.BuildConfig;
import info.nukoneko.android.ho_n.sys.util.image.NKPicasso;
import io.fabric.sdk.android.Fabric;
import io.realm.Realm;

/**
 * Created by atsumi on 2016/10/19.
 */

public final class NKAppController extends Application {

    public AppInfo APP_INFO;

    private static NKAppController mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);

        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }

        mInstance = this;

        APP_INFO = new AppInfo(this);

        // Initialize
        NKPicasso.setup(this);
    }

    public static synchronized NKAppController getApp() {
        return mInstance;
    }

    public static class AppInfo {
        public final String APP_VERSION;
        public final String APP_BUILD_DATE;
        public final String ANDROID_VERSION;

        AppInfo(Application application) {

            final Context context = application.getApplicationContext();

            // APP_VERSION
            {
                String versionName = "";
                try {
                    PackageManager pm = context.getPackageManager();
                    PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
                    versionName = packageInfo.versionName;
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                APP_VERSION = versionName;
            }

            // APP_BUILD_DATE
            {
                String buildDate = "";
                try {
                    ApplicationInfo ai = context.getPackageManager()
                            .getApplicationInfo(context.getPackageName(), 0);
                    buildDate = new SimpleDateFormat("yyyyMMdd", Locale.getDefault())
                            .format(new ZipFile(ai.sourceDir).getEntry("classes.dex").getTime());
                } catch (PackageManager.NameNotFoundException | IOException e) {
                    e.printStackTrace();
                }
                APP_BUILD_DATE = buildDate;
            }

            // ANDROID_VERSION
            {
                ANDROID_VERSION = Build.VERSION.RELEASE;
            }
        }
    }
}
