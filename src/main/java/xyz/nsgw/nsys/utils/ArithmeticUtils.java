/*
 * Copyright (c) Nicholas Williams 2021.
 */

package xyz.nsgw.nsys.utils;

import java.time.Instant;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ArithmeticUtils {

    public static long secondsSince(Date d1) {
        return TimeUnit.SECONDS.convert(Math.abs((new Date()).getTime() - d1.getTime()), TimeUnit.MILLISECONDS);
    }

    public static long millisSince(Date d1) {
        return Math.abs((new Date()).getTime() - d1.getTime());
    }

    public static Date dateFromStr(String l) {
        return Date.from(Instant.ofEpochMilli(Long.parseLong(l)));
    }

}
