package jaxb;

import java.util.ArrayList;
import java.util.List;

public class XFiles {
    public String physicalRoot;
    public List<XFilePhysical> file;
    public List<XFileNsis> fileNsis;

    public XFiles() {
        file = new ArrayList<>();
        fileNsis = new ArrayList<>();
        physicalRoot = ".";
    }
}
