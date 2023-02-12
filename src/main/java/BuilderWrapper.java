import Util.Util;
import adapters.ConfigFileAdapter;
import builders.PkgBuilder;

import jaxb.XFilePlatformSpecific;
import jaxb.XDefaults;
import jaxb.XFilePhysical;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jaxb.XScripts;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.redline_rpm.payload.Directive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BuilderWrapper {

    private final Logger logger = LoggerFactory.getLogger(BuilderWrapper.class.getName());

    private Directive DEFAULT_RPM_DIRECTIVE = Directive.NONE;
    private int DEFAULT_MODE = 0644;
    private int DEFAULT_DIRMODE = 0755;
    private String DEFAULT_OWNER = "root";
    private String DEFAULT_GROUP = "root";

    private final PkgBuilder builder;

    private FileTreeNode root;

    private BuilderWrapper(PkgBuilder builder){
        this.builder = builder;
    }

    public static BuilderWrapper createBuilderWrapper (Config config) {

        PkgBuilder builder = PkgBuilder.getBuilder(
                config.getType(),
                config.getxConfig().pkgParams);

        if (builder == null) return null;

        BuilderWrapper wrapper = new BuilderWrapper(builder);
        wrapper.loadBuildIns(config.getxConfig().buildInDir);
        wrapper.loadDefaults(config.getxConfig().defaults);
        wrapper.loadScripts(config.getxConfig().scripts);
        wrapper.loadFiles(new File(config.getxConfig().files.physicalRoot), new ConfigFileAdapter(config.getxConfig().files).process());
        return  wrapper;
    }

    private void loadScripts(XScripts scripts) {
        if (scripts == null) return;

        if (StringUtils.isNoneEmpty(scripts.preInstallScript)) builder.setPreInstallScript(scripts.preInstallScript);
        if (StringUtils.isNoneEmpty(scripts.postInstallScript)) builder.setPostInstallScript(scripts.postInstallScript);
        if (StringUtils.isNoneEmpty(scripts.preRmScript)) builder.setPreRmScript(scripts.preRmScript);
        if (StringUtils.isNoneEmpty(scripts.postRmScript)) builder.setPostRmScript(scripts.postRmScript);
    }

    public void loadBuildIns(List<String> buildIns){
        if (buildIns == null) return;
        logger.info("----   Load buildins   ----");
        for (String buildin: buildIns) {
            logger.info(String.format("Buildin directory  - %s", buildin));
            builder.addBuildin(buildin);
        }
    }

    public void loadDefaults(XDefaults defaults){
        if (defaults == null) return;

        if (defaults.defaultDirmode != null)
            DEFAULT_DIRMODE = defaults.defaultDirmode;
        if (defaults.defaultMode != null)
            DEFAULT_MODE = defaults.defaultMode;
        if (defaults.defaultOwner != null)
            DEFAULT_OWNER = defaults.defaultOwner;
        if (defaults.defaultGroup != null)
            DEFAULT_GROUP = defaults.defaultGroup;
    }

    /**
     * load files
     *  loadFiles
     *   -> create root node
     *   -> (recursive for all entries) processConfigEntry()
     *     -> addParentsForPath()
     *       -> (recursive for all upper dirs) createNodes
     *     -> addFile()
     *     -> (if dir && recursive) addChildrenForPath()
     *       -> (recursive for all physical files) addFile()
     * @param input physical root directory
     * @param xFiles files in config
     */
    public void loadFiles(File input, List<XFilePhysical> xFiles) {
        logger.info("----   Load files   ----");
        root = new FileTreeNode(
                "",
                input,
                -1,
                null,
                new XFilePlatformSpecific(),
                DEFAULT_OWNER,
                DEFAULT_GROUP,
                false,
                true);

        for (XFilePhysical fileDefinition : xFiles) {
            // if physical path is defined, use it. Or use root path + relative pkg path.
            File physical;
            if (StringUtils.isNotEmpty(fileDefinition.physicalPath )){
                physical = new File(fileDefinition.physicalPath);
            } else {
                physical = physicalPathByRelatives(fileDefinition.pkgPath);
            }

            if (!physical.exists()){
                logger.warn(String.format("File %s not exist on disk, search path - %s", fileDefinition.pkgPath, physical));
                continue;
            }

            setXFileDefaults(fileDefinition);
            processConfigEntry(fileDefinition, physical);
        }
    }

    private File physicalPathByRelatives(String pkgPath){
        FileTreeNode node = root;
        Path nearestPhysical = node.physical.toPath();
        for (Path path : Paths.get(pkgPath)) {
            if (node != null) {
                node = node.getChild(path.getFileName().toString());
            }
            if (node != null) {
                nearestPhysical = node.physical.toPath();
            } else {
                nearestPhysical = nearestPhysical.resolve(path);

            }
        }
        return nearestPhysical.toFile();
    }

    private File physicalPathByRoot(String pkgPath, File root){
        return Paths.get(root.getPath(), FilenameUtils.separatorsToSystem(pkgPath)).toFile();
    }

    public FileTreeNode addParentsForPath(Path path,
                                          XFilePhysical configTemplate,
                                          boolean isDummy) {
        FileTreeNode finder = root;
        Path pkgPath = null;
        Stack<FileTreeNode> nodesPath = new Stack<>();

        for (int i = 0; i < path.getNameCount()-1;  i++){
            Path nextNode = path.getName(i);
            String nodeName = nextNode.toString();

            // Find pkg path
            if(pkgPath != null){
                pkgPath = pkgPath.resolve(nextNode);
            } else {
                pkgPath = nextNode;
            }
//            String strPkgPath = pkgPath.toString();
//            File physical = pkgPathToFile(strPkgPath, root.physical);
//            if (!physical.exists()){
//                logger.warn(String.format("Cant create parent elements for path %s, directory %s not exist. Physical path - %s", path, strPkgPath, physical));
//                return null;
//            }

            // Get or create next node

            finder = addFile(finder, nodeName, new File("."), configTemplate, false, false, true);
        }

//        if (!isDummy) {
//            // going back from farest element in path and setting new directory rights until the manually-configured directory found
//            while (nodesPath.size() > 0) {
//                FileTreeNode current = nodesPath.pop();
//                if (current.isManuallyConfigured) {
//                    break;
//                }
//                logger.info(String.format("Edit parent %s, physical - %s", current, current.physical));
//                current.rights = dirmode;
//                current.owner = owner;
//                current.group = group;
//                current.specific = specific;
//                current.isDummy = isDummy;
//            }
//        }
        return finder;
    }


    private FileTreeNode addFile(FileTreeNode parent,
                                 String filename,
                                 File physical,
                                 XFilePhysical configTemplate,
                                 boolean manually,
                                 boolean override,
                                 boolean dummy){
        FileTreeNode current = parent.getChild(filename);
        if (current == null) {
            logger.info(String.format("Add %s, physical - %s", parent.getPath().resolve(filename),  physical));
            current = parent.addChild(new FileTreeNode(
                    filename,
                    physical,
                    physical.isFile() ? configTemplate.mode : configTemplate.dirmode,
                    null,
                    configTemplate.specific,
                    configTemplate.owner,
                    configTemplate.group,
                    manually,
                    dummy));
        } else if (override){
            if (!current.isManuallyConfigured){
                logger.info(String.format("Edit %s, physical - %s", current, physical));
                current.owner = configTemplate.owner;
                current.group = configTemplate.group;
                current.rights = physical.isFile() ? configTemplate.mode : configTemplate.dirmode;
                current.specific = configTemplate.specific;
                current.isManuallyConfigured = manually;
                current.isDummy = dummy;
            }
        }
        return current;
    }

    public void processConfigEntry(XFilePhysical file, File physical) {
        Path pkgFilePathObj = Paths.get(file.pkgPath);
        String filename = pkgFilePathObj.getFileName().toString();
        // add all parents
        FileTreeNode lastParentDirectory = addParentsForPath(
                pkgFilePathObj,
                file,
                !file.addParents);

        if (lastParentDirectory == null) {
            logger.warn(String.format("Cant create file %s - parent directory not exist!", file.pkgPath));
            return;
        }
        // add file/directory
        FileTreeNode current = addFile(lastParentDirectory, filename, physical, file, true, true, false);

        // add children, if children haven't already added
        if (physical.isDirectory() && file.recursive && !current.isAddChildrenUsed) {
            current.isAddChildrenUsed = true;
            addChildrenForPath(current, file);
        }

    }

    /**
     * Set default parameters for XFilePhysical entry
     */
    private void setXFileDefaults(XFilePhysical obj){
        if (obj.mode == null) obj.mode = DEFAULT_MODE;
        if (obj.dirmode == null) obj.dirmode = DEFAULT_DIRMODE;
        if (obj.specific == null) obj.specific = new XFilePlatformSpecific();
        if (obj.owner == null) obj.owner = DEFAULT_OWNER;
        if (obj.group == null) obj.group = DEFAULT_GROUP;
    }

    /**
     * Add recursive all physical children for startFrom node
     * @param startFrom node to start from
     * @param configTemplate XFilePhysical root record. Added children will inherit config from this record
     */
    private void addChildrenForPath(FileTreeNode startFrom,
                                    XFilePhysical configTemplate) {
        Path path = startFrom.getPath();
        File physical = startFrom.physical;
        if (physical.isFile()) return;
        Set<FileTreeNode> directories = new HashSet<>();

        //get all files in directory
        try (Stream<Path> paths = Files.list(physical.toPath()).sorted(Comparator.comparingInt(Path::getNameCount))) {
            List<Path> pathList = paths.collect(Collectors.toList());
            for (Path e : pathList) {
                File file = e.toFile();
                String filename = file.getName();
                BasicFileAttributes basicFileAttributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);

                FileTreeNode newNode = addFile(startFrom, filename, file, configTemplate, false, true, false);

                // schedule adding children of this directory, if they haven't added already
                if (basicFileAttributes.isDirectory() && !newNode.isAddChildrenUsed)
                    directories.add(newNode);
            }

        } catch (IOException e) {
            logger.warn(String.format("Cant add children for directory %s, physical - %s, %s ", path, physical, e.getMessage()));
        }

        // add all subdirectories
        //TODO non recursive search
        for (FileTreeNode directory : directories) {
            addChildrenForPath(directory, configTemplate);
        }
    }


    /**
     * Load files to builder instance
     */
    private void applyFileTreeToBackend(){
        logger.info("----   Apply file tree to backend   ----");
        Queue<FileTreeNode> queue = new LinkedList<>();
        queue.add(root);
        while(!queue.isEmpty()) {
            FileTreeNode node = queue.poll();
            if (!node.isDummy) {
                boolean result;
                String path = Util.normalizePkgPath(node.getPath().toString());
                if (node.physical.isDirectory()) {
                    result = builder.addDirectory(path, node.physical, node.rights, node.specific, node.owner, node.group);
                    logger.info(String.format("Add directory %s, physical - %s, to package, result = %s", path, node.physical, result));
                } else {
                    result = builder.addFile(path, node.physical, node.rights, node.specific, node.owner, node.group);
                    logger.info(String.format("Add file %s, physical - %s, to package, result = %s", path, node.physical, result));
                }
            }
            queue.addAll(node.children);
        }
    }

    public void build(File outputDirectory) {
        applyFileTreeToBackend();
        logger.info(String.format("----  Save result to %s  ----", outputDirectory));
        String filename = builder.build(outputDirectory.getPath());
        if (StringUtils.isEmpty(filename)) {
            logger.error("No file created!");
        } else {
            logger.info(String.format("Created %s", filename));
        }
    }

    static class FileTreeNode {
        final String name;
        final File physical;
        private FileTreeNode parent;
        final Set<FileTreeNode> children;
        int rights;
        String owner;
        String group;

        XFilePlatformSpecific specific;

        // Nodes configured directly. For example,
        // if config has entry "/opt/hello/world"
        // - /opt/hello/world      - isManuallyConfigured = true,  directly configured
        // - /opt/hello/           - isManuallyConfigured = false, auto added (maybe dummy, if addParents = 0)
        // - /opt/hello/world/any  - isManuallyConfigured = false, auto added (exists if recursive = 1)
        boolean isManuallyConfigured;
        boolean isAddChildrenUsed = false;

        // Dummy nodes - nodes just for hierarchy - will not be saved to archive
        boolean isDummy;
        public FileTreeNode(
                String name,
                File physical,
                int rights,
                FileTreeNode parent,
                XFilePlatformSpecific specific,
                String owner,
                String group,
                boolean isManuallyConfigured,
                boolean isDummy){
            this.parent = parent;
            this.physical = physical;
            this.name = name;
            this.rights = rights;
            this.isManuallyConfigured = isManuallyConfigured;
            this.isDummy = isDummy;
            this.specific = specific;
            this.owner = owner;
            this.group = group;
            children = new HashSet<>();
        }

        public FileTreeNode getChild(String name){
            Optional<FileTreeNode> node = children.stream().filter(e -> e.name.equalsIgnoreCase(name)).findFirst();
            return node.orElse(null);
        }
        public FileTreeNode addChild(FileTreeNode node){
            children.add(node);
            node.parent = this;
            return node;
        }

        public Path getPath(){
            Stack<FileTreeNode> path = new Stack<>();
            FileTreeNode node = this;
            while (node != null){
                path.push(node);
                node = node.parent;
            }
            Path construct = Paths.get("");
            while (path.size() > 0)
                construct = construct.resolve(path.pop().name);

            return construct;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            FileTreeNode that = (FileTreeNode) o;
            return name.equals(that.name) && parent.equals(that.parent);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, parent);
        }

        @Override
        public String toString() {
           StringBuilder builder1 = new StringBuilder() ;
           builder1.append("'").append(getPath().toString()).append("' [");
           if (isManuallyConfigured) builder1.append("Manually,");
           if (isDummy) builder1.append("Dummy,");
           if (isAddChildrenUsed) builder1.append("Children added");
           return builder1.append("]").toString();
        }
    }
}
