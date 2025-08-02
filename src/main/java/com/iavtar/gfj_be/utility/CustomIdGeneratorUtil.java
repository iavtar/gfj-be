package com.iavtar.gfj_be.utility;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomIdGeneratorUtil {

    private static final String PREFIX = "GFJ";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
    private static final AtomicInteger sequence = new AtomicInteger(1);

    public static String generateId() {
        String datePart = DATE_FORMAT.format(new Date());
        int seqNum = sequence.getAndIncrement();
        String formattedSeq = String.format("%03d", seqNum);
        return PREFIX + "-" + datePart + "-" + formattedSeq;
    }

}
