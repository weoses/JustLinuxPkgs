package Util;

import org.apache.commons.io.FilenameUtils;

public class Util {
//    public static String normalizePkgPathStartSlash(String path){
//        String result = FilenameUtils.separatorsToUnix(path);
//        if (!result.startsWith("/")) result = "/" + result;
//        return result;
//    }

    /**
     *  /opt/data/ -> ./opt/data
     */
    public static String normalizePkgPath(String path){
        String result = FilenameUtils.separatorsToUnix(path);

        while (result.startsWith("/"))
            result = result.substring(1);

        if (result.endsWith("/")){
            result = result.substring(result.length()-1);
        }

        return result;
    }


}
