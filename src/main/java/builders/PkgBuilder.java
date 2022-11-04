package builders;

import jaxb.XPkgParams;
import org.redline_rpm.Builder;
import org.redline_rpm.payload.Directive;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;

public abstract class PkgBuilder {

    public static PkgBuilder getBuilder(BuildType buildType, XPkgParams params)  {
        try {
            Class<? extends PkgBuilder> builderClass = buildType.getImpl();
            PkgBuilder builder =  builderClass.getConstructor().newInstance();
            builder.init(params);
            return builder;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract void init(XPkgParams params);
    public abstract void addBuildin(String pkgPath);
    public abstract boolean addDirectory(String pkgPath,
                      int dirmode,
                      Directive directive,
                      String owner,
                      String group);

    public abstract boolean addFile(
            String pkgPath,
            File file,
            int filemode,
            Directive directive,
            String owner,
            String group);

    public abstract String build(String outputFolder);
}
