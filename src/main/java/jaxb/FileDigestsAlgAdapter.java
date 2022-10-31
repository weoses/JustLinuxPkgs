package jaxb;

import org.redline_rpm.header.Architecture;
import org.redline_rpm.header.FileDigestsAlg;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class FileDigestsAlgAdapter extends XmlAdapter<String, FileDigestsAlg> {
    @Override
    public FileDigestsAlg unmarshal(String v) throws Exception {
        for (FileDigestsAlg a : FileDigestsAlg.values()){
            if (a.name().equalsIgnoreCase(v)) return a;
        }
        return null;
    }

    @Override
    public String marshal(FileDigestsAlg v) throws Exception {
        if (v == null) return null;
        return v.name();
    }
}
