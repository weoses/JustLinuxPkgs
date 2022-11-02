import jaxb.XConfig;
import jaxb.XFile;
import org.apache.commons.io.FilenameUtils;
import org.redline_rpm.Builder;
import org.redline_rpm.payload.Directive;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.stream.Collectors;

import static java.util.logging.Logger.getLogger;
import static org.redline_rpm.header.Os.LINUX;
import static org.redline_rpm.header.RpmType.BINARY;

public class BuilderWrapper {
    private final Logger logger = LoggerFactory.getLogger(BuilderWrapper.class.getName());

    private int DEFAULT_MODE = 0644;
    private int DEFAULT_DIRMODE = 0755;
    private final String DEFAULT_OWNER = "root";
    private final String DEFAULT_GROUP = "root";

    private final Builder builder;
    private final XConfig confObject;

    /**
     * Рrocess specified in config files and directories
     * @param inputDirecory root directory on disk
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    private void walkFiles(String inputDirecory) throws IOException, NoSuchAlgorithmException {
        for (XFile fileDefenition : confObject.packageFileDescription) {
            File fileOnDisk = Paths.get(inputDirecory, fileDefenition.pathInRpm).toFile();
            if (!fileOnDisk.exists()) {
                logger.warn(String.format("Not found file on disk for %s, searching path was %s",
                                fileDefenition.pathInRpm,
                                fileOnDisk.getAbsolutePath()));
                continue;
            }

            if (fileOnDisk.isFile()) {
                logger.info(String.format("Process file %s", fileOnDisk));
                processFile(fileOnDisk, fileDefenition);
            } else {
                logger.info(String.format("Process directory %s", fileOnDisk));
                processDirectory(fileOnDisk, fileDefenition);
            }

        }
    }

    /**
     * Get XFile descriptor for RPM path, or make default from directory descriptor
     * @param pathInRpm RPM path
     * @param directoryDefinition Directory - template descriptor for file
     * @return XFile
     */
    private XFile getXfileForPath(String pathInRpm, XFile directoryDefinition) {
        XFile result = null;
        for (XFile fileInConfig : confObject.packageFileDescription) {
            if (fileInConfig.pathInRpm.equalsIgnoreCase(pathInRpm)) {
                result = fileInConfig;
                break;
            }
        }

        if (result == null) {
            logger.debug(String.format("Config definition for %s not found, using inherited", pathInRpm));
            result = new XFile();
            result.pathInRpm = pathInRpm;
            result.group = directoryDefinition.group;
            result.owner = directoryDefinition.owner;
            result.directive = directoryDefinition.directive;
            result.dirmode = directoryDefinition.dirmode;
            result.mode = directoryDefinition.mode;
            result.addParents = directoryDefinition.addParents;
        } else {
            logger.debug(String.format("Config definition for %s found", pathInRpm));
        }
        return result;
    }

    /**
     * Add directory into rpm
     * @param fileOnDisk Physical directory adress
     * @param directoryDefinition Directory descriptor
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    private void processDirectory(File fileOnDisk, XFile directoryDefinition) throws NoSuchAlgorithmException, IOException {
        logger.debug(String.format("Process directory rpm рath  - %s", directoryDefinition.pathInRpm));
        logger.debug(String.format("Process directory disk рath - %s", fileOnDisk.getAbsolutePath()));

        if (!fileOnDisk.exists()) {
            logger.warn(String.format("Directory %s not exist! May be a symlink or other non-windows stuff?", fileOnDisk.getPath()));
            return;
        }

        if (directoryDefinition.processed) {
            logger.info(String.format("Directory %s has already processed", directoryDefinition.pathInRpm));
            return;
        }

        boolean addChildren =  true;
        if (directoryDefinition.addChildren != null) addChildren = directoryDefinition.addChildren == 1;

        int dirmode = DEFAULT_MODE;
        Directive directive = Directive.NONE;
        String owner = DEFAULT_OWNER;
        String group = DEFAULT_GROUP;
        boolean addParents = true;

        if (directoryDefinition.dirmode != null) dirmode = directoryDefinition.dirmode;
        if (directoryDefinition.directive != null) directive = directoryDefinition.directive;
        if (directoryDefinition.owner != null) owner = directoryDefinition.owner;
        if (directoryDefinition.group != null) group = directoryDefinition.group;
        if (directoryDefinition.addParents != null) addParents = directoryDefinition.addParents == 1;

        builder.addDirectory(directoryDefinition.pathInRpm, dirmode, directive, owner, group, addParents);

        if (addChildren) {
            // add all children in this directory
            List<Path> directories = Files.list(fileOnDisk.toPath()).collect(Collectors.toList());
            for (Path p : directories) {
                // create RPM path for child file/directory
                String strRpmPath = "/" + FilenameUtils.separatorsToUnix(Paths.get(
                                directoryDefinition.pathInRpm,
                                p.getName(p.getNameCount()-1).toString()
                        ).toString());

                if (!strRpmPath.startsWith("/")) strRpmPath = "/" + strRpmPath;

                logger.debug(String.format("Constructed rpm path for %s - %s",
                        fileOnDisk.getPath(),
                        strRpmPath));
                // get XFile descriptor for this path, if exists
                XFile configEntry = getXfileForPath(strRpmPath, directoryDefinition);
                if (p.toFile().isFile()) {
                    processFile(p.toFile(), configEntry);
                } else {
                    processDirectory(p.toFile(), configEntry);
                }
            }
        }

        directoryDefinition.processed = true;
    }

    /**
     * Add file into RPM
     * @param fileOnDisk - path on physical file
     * @param fileDefinition - path to file in RPM
     */
    private void processFile(File fileOnDisk, XFile fileDefinition) throws NoSuchAlgorithmException, IOException {
        logger.debug( String.format("Process file rpm рath  - %s", fileDefinition.pathInRpm ));
        logger.debug( String.format("Process file disk рath - %s", fileOnDisk.getAbsolutePath()));

        if (!fileOnDisk.exists()){
            logger.warn(String.format("File %s not exist! May be a symlink or other non-windows stuff?", fileOnDisk.getPath()));
            return;
        }

        if (fileDefinition.processed) {
            logger.info(String.format("File %s has already processed", fileDefinition.pathInRpm));
            return;
        }

        int mode = DEFAULT_MODE;
        int dirmode = DEFAULT_DIRMODE;
        Directive directive = Directive.NONE;
        String owner = DEFAULT_OWNER;
        String group = DEFAULT_GROUP;
        boolean addParents = true;

        if (fileDefinition.mode != null) mode = fileDefinition.mode;
        if (fileDefinition.dirmode != null) dirmode = fileDefinition.dirmode;
        if (fileDefinition.directive != null) directive = fileDefinition.directive;
        if (fileDefinition.owner != null) owner = fileDefinition.owner;
        if (fileDefinition.group != null) group = fileDefinition.group;
        if (fileDefinition.addParents != null) addParents = fileDefinition.addParents == 1;


        String strRpmPath = FilenameUtils.separatorsToUnix(fileDefinition.pathInRpm);
        if (!strRpmPath.startsWith("/")) strRpmPath = "/" + strRpmPath;

        logger.debug(String.format("Constructed rpm path for %s - %s",
                fileOnDisk.getPath(),
                strRpmPath));

        builder.addFile(
                strRpmPath,
                fileOnDisk,
                mode,
                dirmode,
                directive,
                owner,
                group,
                addParents);

        logger.info( String.format("Add file - %s", strRpmPath));

        fileDefinition.processed = true;
    }

    /**
     * Process buildins directories
     */
    private void loadBuildins(){
        for (String buildin: confObject.buildInDir) {
            logger.info(String.format("Buildin directory  - %s", buildin));
            builder.addBuiltinDirectory(buildin);
        }
    }
    public BuilderWrapper (Config config) {
        confObject = config.getxConfig();
        builder = new Builder();
        builder.setPackage( confObject.packageName, confObject.packageVersion, confObject.packageRelease, confObject.packageEpoch );
        builder.setBuildHost("localhost");
        builder.setDescription(confObject.packageDescription);
        builder.setLicense(confObject.packageLicense);
        builder.setPlatform(confObject.packageArch, LINUX ); // LINUX FOREVER
        builder.setType( BINARY );

        if (confObject.fileDigestsAlg != null)
            builder.setFileDigestAlg(confObject.fileDigestsAlg);

        if (confObject.defaultDirmode != null)
            DEFAULT_DIRMODE = config.getxConfig().defaultDirmode;

        if (confObject.defaultMode != null)
            DEFAULT_DIRMODE = config.getxConfig().defaultMode;
    }

    public void loadFiles(String input) throws IOException, NoSuchAlgorithmException {
        loadBuildins();
        walkFiles(input);
    }

    public void build(String outputDirectory) throws NoSuchAlgorithmException, IOException {
        logger.info(String.format("Save result to %s", outputDirectory));
        String filename = builder.build(new File(outputDirectory));
        logger.info(String.format("Created %s", filename));
    }

}
