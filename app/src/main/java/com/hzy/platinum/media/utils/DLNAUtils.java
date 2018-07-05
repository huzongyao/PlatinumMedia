package com.hzy.platinum.media.utils;

import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.hzy.platinum.media.bean.DIDLLite;
import com.hzy.platinum.media.media.MediaInfo;
import com.hzy.platinum.media.media.MediaType;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

/**
 * Created by huzongyao on 2018/6/29.
 */

public class DLNAUtils {

    public static final String BASE_TYPE_VIDEO = "video";
    public static final String BASE_TYPE_AUDIO = "audio";
    public static final String BASE_TYPE_IMAGE = "image";

    public final static String OBJECT_CLASS_AUDIO = "object.item.audioItem";
    public final static String OBJECT_CLASS_VIDEO = "object.item.videoItem";
    public final static String OBJECT_CLASS_IMAGE = "object.item.imageItem";

    /**
     * Get media info from url and meta
     * @param url url
     * @param meta meta
     * @return MediaInfo
     */
    public static MediaInfo getMediaInfo(String url, String meta) {
        MediaInfo mediaInfo = getMediaInfoFromMeta(meta);
        if (mediaInfo.mediaType == MediaType.TYPE_UNKNOWN) {
            mediaInfo.mediaType = guessTypeFromURL(url);
        }
        mediaInfo.url = url;
        return mediaInfo;
    }

    private static MediaInfo getMediaInfoFromMeta(String meta) {
        MediaInfo mediaInfo = new MediaInfo();
        MediaType mediaType = MediaType.TYPE_UNKNOWN;
        if (!TextUtils.isEmpty(meta)) {
            DIDLLite didlLite = null;
            try {
                Serializer serializer = new Persister();
                didlLite = serializer.read(DIDLLite.class, meta);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String objectClass;
            DIDLLite.MediaItem objectItem;
            if (didlLite != null && (objectItem = didlLite.item) != null) {
                mediaInfo.title = objectItem.title;
                mediaInfo.albumArtURI = objectItem.albumArtURI;
                if ((objectClass = objectItem.objectClass) != null) {
                    if (objectClass.startsWith(OBJECT_CLASS_VIDEO)) {
                        mediaType = MediaType.TYPE_VIDEO;
                    } else if (objectClass.startsWith(OBJECT_CLASS_AUDIO)) {
                        mediaType = MediaType.TYPE_AUDIO;
                    } else if (objectClass.startsWith(OBJECT_CLASS_IMAGE)) {
                        mediaType = MediaType.TYPE_IMAGE;
                    }
                }
            }
        }
        mediaInfo.mediaType = mediaType;
        return mediaInfo;
    }

    private static MediaType guessTypeFromURL(String url) {
        MediaType mediaType = MediaType.TYPE_UNKNOWN;
        String ext = MimeTypeMap.getFileExtensionFromUrl(url);
        String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
        if (mime != null) {
            if (mime.startsWith(BASE_TYPE_VIDEO)) {
                mediaType = MediaType.TYPE_VIDEO;
            } else if (mime.startsWith(BASE_TYPE_AUDIO)) {
                mediaType = MediaType.TYPE_AUDIO;
            } else if (mime.startsWith(BASE_TYPE_IMAGE)) {
                mediaType = MediaType.TYPE_IMAGE;
            }
        }
        return mediaType;
    }
}
