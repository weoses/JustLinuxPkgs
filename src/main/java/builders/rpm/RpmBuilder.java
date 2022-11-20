package builders.rpm;

import builders.PkgBuilder;
import jaxb.XFilePlatformSpecific;
import jaxb.XPkgParams;
import org.redline_rpm.Builder;
import org.redline_rpm.header.Os;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class RpmBuilder extends PkgBuilder {
    Logger logger = LoggerFactory.getLogger(RpmBuilder.class.getName());
    final Builder builder;

    public RpmBuilder() {
        builder = new Builder();
    }


    @Override
    protected void init(XPkgParams params) {
        builder.setPackage(params.packageName, params.packageVersion, params.rpmPackageRelease, params.rpmPackageEpoch);
        builder.setDescription(params.packageDescription);
        builder.setLicense(params.packageLicense);
        builder.setFileDigestAlg(params.rpmFileDigestsAlg);
        builder.setPlatform(params.rpmPackageArch, Os.LINUX);
        builder.setBuildHost("localhost");
    }

    @Override
    public void addBuildin(String pkgPath) {
        builder.addBuiltinDirectory(pkgPath);
    }

    @Override
    public boolean addDirectory(String pkgPath,
                                File file,
                                int dirmode,
                                XFilePlatformSpecific specific,
                                String owner,
                                String group) {
        try {
            builder.addDirectory(pkgPath, dirmode, specific.rpmDirective, owner, group, false);
            return true;
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }

        return false;
    }

    @Override
    public boolean addFile(String pkgPath, File file, int filemode, XFilePlatformSpecific specific, String owner, String group) {
        try {
            builder.addFile(pkgPath, file, filemode, -1, specific.rpmDirective, owner, group, false);
            return true;
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public void setPreInstallScript(String script) {
        builder.setPreInstallScript(script);
    }

    @Override
    public void setPostInstallScript(String script) {
        builder.setPostInstallScript(script);
    }

    @Override
    public void setPreRmScript(String script) {
        builder.setPreUninstallScript(script);
    }

    @Override
    public void setPostRmScript(String script) {
        builder.setPostUninstallScript(script);
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
