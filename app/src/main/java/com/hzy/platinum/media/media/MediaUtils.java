package com.hzy.platinum.media.media;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;

import com.hzy.platinum.media.activity.AudioActivity;
import com.hzy.platinum.media.activity.ImageActivity;
import com.hzy.platinum.media.activity.VideoActivity;

import java.io.File;
import java.util.Formatter;
import java.util.Locale;

/**
 * Created by huzongyao on 2018/6/29.
 */

public class MediaUtils {

    public static final String EXTRA_MEDIA_INFO = "EXTRA_MEDIA_INFO";
    private static StringBuilder formatBuilder = new StringBuilder();
    private static Formatter formatter = new Formatter(formatBuilder, Locale.getDefault());

    /**
     * Start To Play media info with new Task flag
     *
     * @param context   context
     * @param mediaInfo nediaInfo
     */
    public static void startPlayMedia(Context context, MediaInfo mediaInfo) {
        if (mediaInfo == null) {
            return;
        }
        Intent intent = new Intent();
        switch (mediaInfo.mediaType) {
            case TYPE_VIDEO:
                intent.setClass(context, VideoActivity.class);
                break;
            case TYPE_AUDIO:
                intent.setClass(context, AudioActivity.class);
                break;
            case TYPE_IMAGE:
                intent.setClass(context, ImageActivity.class);
                break;
            default:
                return;
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(EXTRA_MEDIA_INFO, mediaInfo);
        context.startActivity(intent);
    }

    /**
     * Init exo media
     *
     * @param context context
     */
    public static void configureExoMedia(Context context) {
        // Registers the media sources to use the OkHttp client instead of the standard Apache one
        // Note: the OkHttpDataSourceFactory can be found in the ExoPlayer extension library `extension-okhttp`
        /*ExoMedia.setDataSourceFactoryProvider(new ExoMedia.DataSourceFactoryProvider() {

            @NonNull
            @Override
            public DataSource.Factory provide(@NonNull String userAgent, @Nullable TransferListener listener) {
                if (instance == null) {
                    // Updates the network data source to use the OKHttp implementation
                    DataSource.Factory upstreamFactory = new OkHttpDataSourceFactory(new OkHttpClient(), userAgent, listener);

                    // Adds a cache around the upstreamFactory
                    Cache cache = new SimpleCache(new File(getCacheDirectory(context), "ExoMediaCache"),
                            new LeastRecentlyUsedCacheEvictor(50 * 1024 * 1024));
                    instance = new CacheDataSourceFactory(cache, upstreamFactory, CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);
                }
                return instance;
            }

            @Nullable
            private CacheDataSourceFactory instance;

        });*/
    }

    private static File getCacheDirectory(Context context) {
        File cacheDir = context.getExternalCacheDir();
        if (cacheDir == null) {
            cacheDir = context.getCacheDir();
        }
        return cacheDir;
    }

    /**
     * Formats the specified milliseconds to a human readable format
     * in the form of (Hours : Minutes : Seconds).  If the specified
     * milliseconds is less than 0 the resulting format will be
     * "--:--" to represent an unknown time
     *
     * @param milliseconds The time in milliseconds to format
     * @return The human readable time
     */
    public static String formatMs(long milliseconds) {
        if (milliseconds < 0) {
            return "--:--";
        }

        long seconds = (milliseconds % DateUtils.MINUTE_IN_MILLIS) / DateUtils.SECOND_IN_MILLIS;
        long minutes = (milliseconds % DateUtils.HOUR_IN_MILLIS) / DateUtils.MINUTE_IN_MILLIS;
        long hours = (milliseconds % DateUtils.DAY_IN_MILLIS) / DateUtils.HOUR_IN_MILLIS;

        formatBuilder.setLength(0);
        if (hours > 0) {
            return formatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        }

        return formatter.format("%02d:%02d", minutes, seconds).toString();
    }
}
