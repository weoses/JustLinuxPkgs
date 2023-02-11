package adapters;

import Util.Util;
import fileReaders.NSISFilesReader;
import jaxb.*;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ConfigFileAdapter {

    private final Logger logger = LoggerFactory.getLogger(ConfigFileAdapter.class.getName());

    private final XFiles files;

    public ConfigFileAdapter(XFiles files){
        this.files  = files;
    }

    public List<XFilePhysical> process(){
        List<XFilePhysical> processed = new ArrayList<>(files.file);

        for (XFileNsis xFileNsis : files.fileNsis) {
            processed.addAll(processNSIS(xFileNsis));
        }

        return processed;

    }

    private String replaceNsisPath(XFileNsis nsis, String path){
        String retpath = path;
        for (XReplace pair : nsis.replace) {
            if (pair.regexp) {
                retpath = retpath.replaceAll(pair.first, pair.second);
            } else {
                retpath = retpath.replace(pair.first, pair.second);
            }
        }
        return  retpath;
    }

    private List<XFilePhysical> processNSIS(XFileNsis nsis){
        logger.info("Loading NSIS file '{}'", nsis.nsisFilePath);
        Path wd = Paths.get(nsis.nsisWorkingDirectory);

        List<XFilePhysical> physicals = new ArrayList<>();
        NSISFilesReader reader = new NSISFilesReader(new File(nsis.nsisFilePath));
        List<NSISFilesReader.NsisFileEntry> entries = reader.parse();

        for (NSISFilesReader.NsisFileEntry entry : entries) {
            logger.debug("NSIS entry - '{}', outPath = {}, recursive - '{}'", entry.oname, entry.outPath, entry.recursive);

            // Process path replacements
            String nsisOutFilename = FilenameUtils.normalize(replaceNsisPath(nsis, entry.oname));
            String nsisOutPath = FilenameUtils.normalize(replaceNsisPath(nsis, entry.outPath));
            String nsisPhysicalName = wd.resolve(entry.filePath).normalize().toString();

            logger.debug("NSIS entry - '{}', outPath = {}, replaced to '{}', '{}'", entry.oname, entry.outPath,  nsisOutFilename, nsisOutPath);

            String pkgPath = FilenameUtils.separatorsToUnix(Paths.get(nsisOutPath, nsisOutFilename).normalize().toString());

            logger.debug("NSIS entry - '{}', pkgPath - {}", entry.oname, pkgPath);

            // Process excluded paths
            boolean exclude = false;
            for (XExclude excRule : nsis.exclude) {
                if (excRule.regexp) {
                    if (!Pattern.compile(excRule.text).matcher(entry.filePath).matches()) {
                        exclude = true;
                        break;
                    }
                } else {
                    if (entry.filePath.contains(excRule.text)){
                        exclude = true;
                        break;
                    }
                }
            }
            if (exclude) {
                logger.info("NSIS entry - '{}', ('{}' -> '{}') : excluded", entry.oname, nsisPhysicalName, pkgPath);
                continue;
            }

            logger.info("NSIS entry - '{}', ('{}' -> '{}') : added", entry.oname, nsisPhysicalName, pkgPath);
            XFilePhysical physical = new XFilePhysical();
            physical.recursive = entry.recursive;
            physical.pkgPath = pkgPath;
            physical.physicalPath = nsisPhysicalName;
            physicals.add(physical);
        }

        return physicals;
    }

}
