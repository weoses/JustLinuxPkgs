package builders.deb;

import builders.PkgBuilder;
import jaxb.XPkgParams;
import org.redline_rpm.payload.Directive;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class DebBuilder extends PkgBuilder {

    @Override
    protected void init(XPkgParams params) {

    }

    @Override
    public void addBuildin(String pkgPath) {

    }

    @Override
    public boolean addDirectory(String pkgPath, int dirmode, Directive directive, String owner, String group) {
        return false;
    }

    @Override
    public boolean addFile(String pkgPath, File file, int filemode, Directive directive, String owner, String group) {
        return false;
    }

    @Override
    public String build(String outputFolder) {
        return null;
    }
}
