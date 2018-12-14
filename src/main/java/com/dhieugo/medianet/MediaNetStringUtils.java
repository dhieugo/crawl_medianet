package com.dhieugo.medianet;

public class MediaNetStringUtils {

    public static String getEventTarget(String s) {
        s = s.replaceAll("\'", "")
                .replaceAll(",", "")
                .replaceAll("\\(", "")
                .replaceAll("\\)", "")
                .replaceAll("javascript:__doPostBack", "");
        return s;
    }
}
