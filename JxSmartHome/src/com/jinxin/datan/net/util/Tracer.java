package com.jinxin.datan.net.util;

import java.util.Calendar;

public class Tracer {

    private static boolean TRACE = true; //NOSONAR

    public Tracer() {
    }

    public static final void trace(String msg) {
        Calendar calendar = Calendar.getInstance();
//        System.out.println(msg);
//        System.out.println(Integer.toString(calendar.get(10)) + ":" + Integer.toString(calendar.get(12)) + ":"
//                + Integer.toString(calendar.get(13)) + ":" + Integer.toString(calendar.get(14)));
//        System.out.println("Free MEM:" + (Runtime.getRuntime().freeMemory() >> 10) + "k");
//        System.out.flush();
    }

    public static final void debug(String msg) {
        if (!TRACE) {
            return;
        } else {
//            System.out.println(msg);
//            System.out.flush();
            return;
        }
    }

}
