package com.hzy.platinum.server.player;

/**
 * Media classification used to pick the right UI / playback path for a
 * resource pushed via DLNA (AVTransport SetAVTransportURI).
 */
public enum MediaType {
    VIDEO,
    AUDIO,
    IMAGE,
    UNKNOWN;

    /**
     * Best-effort detection from the resource URI and optional DIDL-Lite metadata.
     * The metadata (CurrentURIMetaData) often carries a {@code <res>} element whose
     * protocolInfo / mime-type is the most reliable signal, but we fall back to the
     * URI extension when metadata is missing or unhelpful.
     */
    public static MediaType detect(String uri, String metaData) {
        // 1) Try the metadata mime-type first (most accurate).
        String mime = extractMime(metaData);
        if (mime != null) {
            if (mime.startsWith("image/")) return IMAGE;
            if (mime.startsWith("audio/")) return AUDIO;
            if (mime.startsWith("video/")) return VIDEO;
        }
        // 2) Fall back to the URI extension.
        String lower = uri == null ? "" : uri.toLowerCase();
        if (lower.contains("://")) {
            int q = lower.indexOf('?');
            if (q > 0) lower = lower.substring(0, q);
            int h = lower.indexOf('#');
            if (h > 0) lower = lower.substring(0, h);
        }
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png")
                || lower.endsWith(".gif") || lower.endsWith(".bmp") || lower.endsWith(".webp")) {
            return IMAGE;
        }
        if (lower.endsWith(".mp3") || lower.endsWith(".wav") || lower.endsWith(".flac")
                || lower.endsWith(".m4a") || lower.endsWith(".aac") || lower.endsWith(".ogg")
                || lower.endsWith(".opus")) {
            return AUDIO;
        }
        if (lower.endsWith(".mp4") || lower.endsWith(".mkv") || lower.endsWith(".avi")
                || lower.endsWith(".mov") || lower.endsWith(".webm") || lower.endsWith(".3gp")
                || lower.endsWith(".ts") || lower.endsWith(".m3u8")) {
            return VIDEO;
        }
        // 3) Last resort: assume video so the player at least tries.
        return lower.isEmpty() ? UNKNOWN : VIDEO;
    }

    /**
     * Pulls a mime-type out of DIDL-Lite metadata, e.g.
     * {@code <res protocolInfo="http-get:*:image/jpeg:*" ...>} or a {@code <mime-type>} tag.
     */
    private static String extractMime(String metaData) {
        if (metaData == null || metaData.isEmpty()) return null;
        // protocolInfo form: *:image/jpeg:* or *:video/mp4:*
        int idx = metaData.indexOf("http-get:*:");
        if (idx >= 0) {
            int start = idx + "http-get:*".length() + 1;
            int end = metaData.indexOf(':', start);
            if (end > start) {
                String m = metaData.substring(start, end).trim();
                if (!m.isEmpty()) return m;
            }
        }
        int t = metaData.indexOf("<mime-type>");
        if (t >= 0) {
            int e = metaData.indexOf("</mime-type>", t);
            if (e > t) return metaData.substring(t + "<mime-type>".length(), e).trim();
        }
        return null;
    }
}
