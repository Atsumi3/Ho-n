package info.nukoneko.android.ho_n.sys.util.image;

import android.app.Application;
import android.content.Context;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

/**
 * Created by atsumi on 2016/10/20.
 */

public final class NKPicasso {
    private final static int DISK_CACHE_SIZE_MB = 100;
    private final static int MEMORY_CACHE_SIZE_MB = 5;

    private final Picasso picasso;
    private static NKPicasso instance;

    /**
     * callable only AppController
     *
     * @param applicationContext appContext
     */
    static public synchronized void setup(Application applicationContext) {
        instance = new NKPicasso(applicationContext.getApplicationContext());
    }

    // singleton
    private NKPicasso(Context appContext) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.cache(new Cache(appContext.getCacheDir(), DISK_CACHE_SIZE_MB * 1024 * 1024));

        picasso = new Picasso.Builder(appContext)
                .downloader(new OkHttp3Downloader(builder.build()))
                .memoryCache(new LruCache(MEMORY_CACHE_SIZE_MB * 1024 * 1024))
                .build();
    }

    static public synchronized Picasso getInstance() {
        return instance.picasso;
    }
}
