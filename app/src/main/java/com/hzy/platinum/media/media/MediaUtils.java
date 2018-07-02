package com.hzy.platinum.media.media;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.devbrackets.android.exomedia.ExoMedia;
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.hzy.platinum.media.activity.AudioActivity;
import com.hzy.platinum.media.activity.ImageActivity;
import com.hzy.platinum.media.activity.VideoActivity;

import java.io.File;

import okhttp3.OkHttpClient;

/**
 * Created by huzongyao on 2018/6/29.
 */

public class MediaUtils {

    public static final String EXTRA_MEDIA_INFO = "EXTRA_MEDIA_INFO";

    public static void startPlayMedia(Context context, MediaInfo mediaInfo) {
        if (mediaInfo == null) {
            return;
        }
        switch (mediaInfo.mediaType) {
            case TYPE_VIDEO:
                Intent intent = new Intent(context, VideoActivity.class);
                intent.putExtra(EXTRA_MEDIA_INFO, mediaInfo);
                context.startActivity(intent);
                break;
            case TYPE_AUDIO:
                intent = new Intent(context, AudioActivity.class);
                intent.putExtra(EXTRA_MEDIA_INFO, mediaInfo);
                context.startActivity(intent);
                break;
            case TYPE_IMAGE:
                intent = new Intent(context, ImageActivity.class);
                intent.putExtra(EXTRA_MEDIA_INFO, mediaInfo);
                context.startActivity(intent);
                break;
        }
    }

    public static void configureExoMedia(Context context) {
        // Registers the media sources to use the OkHttp client instead of the standard Apache one
        // Note: the OkHttpDataSourceFactory can be found in the ExoPlayer extension library `extension-okhttp`
        ExoMedia.setDataSourceFactoryProvider(new ExoMedia.DataSourceFactoryProvider() {
            @Nullable
            private CacheDataSourceFactory instance;

            @NonNull
            @Override
            public DataSource.Factory provide(@NonNull String userAgent, @Nullable TransferListener<? super DataSource> listener) {
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
        });
    }

    private static File getCacheDirectory(Context context) {
        File cacheDir = context.getExternalCacheDir();
        if (cacheDir == null) {
            cacheDir = context.getCacheDir();
        }
        return cacheDir;
    }
}
