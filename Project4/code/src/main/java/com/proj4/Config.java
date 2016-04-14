package com.proj4;

/**
 * code
 */
public class Config {
    public static final String DATA_PATH = "../data";
    public static final String OUT_PATH = "../data/out";
    public static final String MAP_PATH = "../data/maze";

    public static String outPath(String ns, String fname) {
        return String.format("%s/%s/%s", OUT_PATH, ns, fname);
    }

    public static String mapPath(String mapname) {
        return String.format("%s/%s.map", MAP_PATH, mapname);
    }
}
