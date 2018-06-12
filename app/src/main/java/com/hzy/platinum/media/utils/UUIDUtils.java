package com.hzy.platinum.media.utils;

import java.util.UUID;

/**
 * Created by huzongyao on 2018/6/12.
 */

public class UUIDUtils {

    /**
     * To generate a uuid
     *
     * @return uuid
     */
    public static String getRandomUUID() {
        return UUID.randomUUID().toString();
    }
}
