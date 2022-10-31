package jaxb;

import org.apache.commons.io.FilenameUtils;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class PathUnixAdapter extends XmlAdapter<String, String> {

    @Override
    public String unmarshal(String v) throws Exception {
        return FilenameUtils.separatorsToUnix(v);
    }

    @Override
    public String marshal(String v) throws Exception {
        return FilenameUtils.separatorsToUnix(v);
    }
}
