package builders;

import jaxb.XFilePlatformSpecific;
import jaxb.XPkgParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;


public abstract class PkgBuilder {

    static Logger logger = LoggerFactory.getLogger(PkgBuilder.class.getName());
    public static PkgBuilder getBuilder(BuildType buildType, XPkgParams params) {
        try {
            Class<? extends PkgBuilder> builderClass = buildType.getImpl();
            PkgBuilder builder = builderClass.getConstructor().newInstance();
            builder.init(params);
            return builder;
        } catch (Exception e) {
            logger.error("Cant create builder - "+e, e);
            return null;
        }
    }

    protected abstract void init(XPkgParams params);

    public abstract void addBuildin(String pkgPath);

    public abstract boolean addDirectory(String pkgPath,
                                         File file,
                                         int dirmode,
                                         XFilePlatformSpecific specific,
                                         String owner,
                                         String group);

    public abstract boolean addFile(
            String pkgPath,
            File file,
            int filemode,
            XFilePlatformSpecific directive,
            String owner,
            String group);

    public abstract String build(String outputFolder);
}
