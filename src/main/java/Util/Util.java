package Util;

import org.apache.commons.io.FilenameUtils;

public class Util {
    public static String normalizePkgPathStartSlash(String path){
        String result = FilenameUtils.separatorsToUnix(path);
        if (!result.startsWith("/")) result = "/" + result;
        return result;
    }

    public static String normalizePkgPath(String path){
        String result = FilenameUtils.separatorsToUnix(path);
        while (result.startsWith("/"))
            result = result.substring(1);
        return result;
    }
}
