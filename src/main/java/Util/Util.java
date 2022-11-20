package Util;

import org.apache.commons.io.FilenameUtils;

public class Util {
//    public static String normalizePkgPathStartSlash(String path){
//        String result = FilenameUtils.separatorsToUnix(path);
//        if (!result.startsWith("/")) result = "/" + result;
//        return result;
//    }

    public static String normalizePkgPathNoSlash(String path){
        String result = FilenameUtils.separatorsToUnix(path);
        while (result.startsWith("/"))
            result = result.substring(1);
        return result;
    }
    public static String normalizeDirectoryPath(String path){
        String s = normalizePkgPathNoSlash(path);
        if (!s.endsWith("/")) s += "/";
        return s;
    }
}
