import builders.PkgBuilder;

import jaxb.XDefaults;
import jaxb.XFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;
import org.redline_rpm.payload.Directive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BuilderWrapper {

    private final Logger logger = LoggerFactory.getLogger(BuilderWrapper.class.getName());

    private Directive DEFAULT_DIRECTIVE = Directive.NONE;
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

        BuilderWrapper wrapper = new BuilderWrapper(builder);
        wrapper.loadBuildIns(config.getxConfig().buildInDir);
        wrapper.loadDefaults(config.getxConfig().defaults);
        wrapper.loadFiles(config.getInput(), config.getxConfig().file);
        return  wrapper;
    }
    public void loadBuildIns(List<String> buildIns){
        if (buildIns == null) return;
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
    }
    public void loadFiles(String input, List<XFile> xFiles) {
        root = new FileTreeNode(
                "",
                new File(input),
                -1,
                null,
                Directive.NONE,
                DEFAULT_OWNER,
                DEFAULT_GROUP,
                false,
                true);

        for (XFile fileDefinition : xFiles) {
            File physical = pkgPathToFile(fileDefinition.pkgPath, input);
            if (!physical.exists()){
                logger.warn(String.format("File %s not exist on disk, search path - %s", fileDefinition.pkgPath, physical));
                continue;
            }

            setXFileDefaults(fileDefinition);
            processXFile(fileDefinition, physical);
        }
    }

    private File pkgPathToFile(String pkgPath, String root){
        return Paths.get(root, FilenameUtils.separatorsToSystem(pkgPath)).toFile();
    }

    public FileTreeNode addParentsForPath(Path path,
                                          int dirmode,
                                          Directive directive,
                                          String owner,
                                          String group,
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
            String strPkgPath = pkgPath.toString();
            File physical = pkgPathToFile(strPkgPath, root.physical.toString());
            if (!physical.exists()){
                logger.warn(String.format("Cant create parent elements for path %s, directory %s not exist. Physical path - %s", path, strPkgPath, physical));
                return null;
            }

            // Get or create next node
            FileTreeNode current = finder.getChild(nodeName);
            if (current == null) {
                logger.info(String.format("Add parent %s, physical - %s", finder.getPath().resolve(nodeName),  physical));
                current = new FileTreeNode(
                        nodeName,
                        physical,
                        dirmode,
                        finder,
                        directive,
                        owner,
                        group,
                        false,
                        isDummy);
                finder.addChild(current);
            } else {
                nodesPath.add(current);
            }

            finder = current;
        }

        if (!isDummy) {
            // going back from farest element in path and setting new directory rights until the manually-configured directory found
            while (nodesPath.size() > 0) {
                FileTreeNode current = nodesPath.pop();
                if (current.isManuallyConfigured) {
                    break;
                }
                logger.info(String.format("Edit parent %s, physical - %s", current, current.physical));
                current.rights = dirmode;
                current.owner = owner;
                current.group = group;
                current.directive = directive;
                current.isDummy = isDummy;
            }
        }
        return finder;
    }

    public void processXFile(XFile file, File physical) {
        Path pkgFilePathObj = Paths.get(file.pkgPath);
        String filename = pkgFilePathObj.getFileName().toString();
        // add all parents
        FileTreeNode lastParentDirectory = addParentsForPath(
                pkgFilePathObj,
                file.dirmode,
                file.directive,
                file.owner,
                file.group,
                !file.addParents);

        if (lastParentDirectory == null) {
            logger.warn(String.format("Cant create file %s - parent directory not exist!", file.pkgPath));
            return;
        }
        // add file/directory
        FileTreeNode current = lastParentDirectory.getChild(filename);
        if (current == null) {
            logger.info(String.format("Add %s, physical - %s", lastParentDirectory.getPath().resolve(filename),  physical));
            current = lastParentDirectory.addChild(new FileTreeNode(
                    filename,
                    physical,
                    physical.isFile() ? file.mode : file.dirmode,
                    null,
                    file.directive,
                    file.owner,
                    file.group,
                    true,
                    false));
        } else {
            if (!current.isManuallyConfigured){
                logger.info(String.format("Edit %s, physical - %s", current, physical));
                current.owner = file.owner;
                current.group = file.group;
                current.rights = physical.isFile() ? file.mode : file.dirmode;
                current.directive = file.directive;
                current.isManuallyConfigured = true;
            }
        }

        // add children, if children haven't already added
        if (physical.isDirectory() && file.recursive && !current.isAddChildrenUsed) {
            current.isAddChildrenUsed = true;
            addChildrenForPath(current, file.mode, file.dirmode, file.directive, file.owner, file.group);
        }

    }

    private void setXFileDefaults(XFile obj){
        if (obj.mode == null) obj.mode = DEFAULT_MODE;
        if (obj.dirmode == null) obj.dirmode = DEFAULT_DIRMODE;
        if (obj.directive == null) obj.directive = DEFAULT_DIRECTIVE;
        if (obj.owner == null) obj.owner = DEFAULT_OWNER;
        if (obj.group == null) obj.group = DEFAULT_GROUP;
        if (obj.recursive == null) obj.recursive = true;
        if (obj.addParents == null) obj.addParents = false;
    }

    private void addChildrenForPath(FileTreeNode startFrom,
                                    int mode,
                                    int dirmode,
                                    Directive directive,
                                    String owner,
                                    String group) {
        Path path = startFrom.getPath();
        File physical = pkgPathToFile(path.toString(), root.physical.toString());
        if (physical.isFile()) return;
        Set<FileTreeNode> directories = new HashSet<>();

        //get all files in directory
        try (Stream<Path> paths = Files.list(physical.toPath()).sorted(Comparator.comparingInt(Path::getNameCount))) {
            List<Path> pathList = paths.collect(Collectors.toList());
            for (Path e : pathList) {
                File file = e.toFile();
                String name = file.getName();
                BasicFileAttributes basicFileAttributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);

                FileTreeNode newNode = startFrom.getChild(name);
                //add a file/directory
                if (newNode == null) {
                    logger.info(String.format("Add %s, physical - %s", startFrom.getPath().resolve(name), file.getPath()));
                    newNode = startFrom.addChild(new FileTreeNode(
                            name,
                            file,
                            basicFileAttributes.isRegularFile() ? mode : dirmode,
                            null,
                            directive,
                            owner,
                            group,
                            false,
                            false));
                } else if (!newNode.isManuallyConfigured) {
                    logger.info(String.format("Edit %s, physical - %s", newNode, file));
                    newNode.owner = owner;
                    newNode.group = group;
                    newNode.rights = physical.isFile() ? mode : dirmode;
                    newNode.directive = directive;
                }

                // schedule adding children of this directory, if they haven't added already
                if (basicFileAttributes.isDirectory() && !newNode.isAddChildrenUsed)
                    directories.add(newNode);
            }

        } catch (IOException e) {
            logger.warn(String.format("Cant add children for directory %s, physical - %s, %s ", path, physical, e.getMessage()));
        }

        // add all subdirectories
        for (FileTreeNode directory : directories) {
            directory.isAddChildrenUsed = true;
            addChildrenForPath(directory, mode, dirmode, directive, owner, group);
        }
    }

    private void applyFileTreeToBackend(){
        logger.info("--------------------------------");
        logger.info("   Apply file tree to backend   ");
        logger.info("--------------------------------");
        Queue<FileTreeNode> queue = new LinkedList<>();
        queue.add(root);
        while(!queue.isEmpty()) {
            FileTreeNode node = queue.poll();
            if (!node.isDummy) {
                boolean result;
                String path = node.getPath().toString();
                if (node.physical.isDirectory()) {
                    result = builder.addDirectory(path, node.rights, node.directive, node.owner, node.group);
                    logger.info(String.format("Add directory %s, physical - %s, to package, result = %s", path, node.physical, result));
                } else {
                    result = builder.addFile(path, node.physical, node.rights, node.directive, node.owner, node.group);
                    logger.info(String.format("Add file %s, physical - %s, to package, result = %s", path, node.physical, result));
                }
            }
            queue.addAll(node.children);
        }
    }
    public void build(String outputDirectory){
        applyFileTreeToBackend();
        logger.info(String.format("Save result to %s", outputDirectory));
        String filename = builder.build(outputDirectory);
        logger.info(String.format("Created %s", filename));
    }

    static class FileTreeNode {
        final String name;
        final File physical;
        private FileTreeNode parent;
        final Set<FileTreeNode> children;
        int rights;
        Directive directive; // TODO no redline dependency
        String owner;
        String group;

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
                Directive directive,
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
            this.directive = directive;
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
           builder1.append("'").append(getPath().toString()).append("'(");
           if (isManuallyConfigured) builder1.append("Manually,");
           if (isDummy) builder1.append("Dummy,");
           if (isAddChildrenUsed) builder1.append("Children added");
           return builder1.append(")").toString();
        }
    }
}
