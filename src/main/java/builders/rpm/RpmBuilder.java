package builders.rpm;

import builders.PkgBuilder;
import jaxb.XPkgParams;
import org.redline_rpm.Builder;
import org.redline_rpm.payload.Directive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class RpmBuilder extends PkgBuilder {
    Logger logger = LoggerFactory.getLogger(RpmBuilder.class.getName());
    final Builder builder;

    public RpmBuilder(){
        builder = new Builder();
    }


    @Override
    protected void init(XPkgParams params) {
        builder.setPackage(params.packageName, params.packageVersion, params.packageRelease, params.packageEpoch);
        builder.setDescription(params.packageDescription);
        builder.setLicense(params.packageLicense);
        builder.setFileDigestAlg(params.fileDigestsAlg);
        builder.setBuildHost("localhost");
    }

    @Override
    public void addBuildin(String pkgPath) {
        builder.addBuiltinDirectory(pkgPath);
    }

    @Override
    public boolean addDirectory(String pkgPath,
                             int dirmode,
                             Directive directive,
                             String owner,
                             String group){
        try {
            builder.addDirectory(pkgPath, dirmode, directive, owner, group, false);
            return true;
        } catch (Exception e){
            logger.warn(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public boolean addFile(String pkgPath, File file, int filemode, Directive directive, String owner, String group) {
        try {
            builder.addFile(pkgPath, file, filemode, -1, directive, owner, group, false);
            return true;
        } catch (Exception e){
            logger.warn(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public String build(String outputFolder) {
        try {
            return builder.build(new File(outputFolder));
        } catch (NoSuchAlgorithmException | IOException e) {
            logger.warn(e.getMessage(), e);
        }
        return null;
    }
}
