package jaxb.attributeAdapters;

import org.redline_rpm.header.Architecture;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class ArchAdapter extends XmlAdapter<String, Architecture> {
    @Override
    public Architecture unmarshal(String s) throws Exception {
        for (Architecture a : Architecture.values()){
            if (a.name().equalsIgnoreCase(s)) return a;
        }
        return null;
    }

    @Override
    public String marshal(Architecture architecture) throws Exception {
        if (architecture == null) return null;
        return architecture.name();
    }
}
