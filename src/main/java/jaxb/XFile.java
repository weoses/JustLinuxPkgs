package jaxb;

import jaxb.attributeAdapters.ModeParserAdapter;
import jaxb.attributeAdapters.PathUnixAdapter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

public class XFile {
    @XmlJavaTypeAdapter(PathUnixAdapter.class)
    @XmlElement(required = true)
    public String pkgPath;
    @XmlJavaTypeAdapter(ModeParserAdapter.class)
    public Integer mode;
    @XmlJavaTypeAdapter(ModeParserAdapter.class)
    public Integer dirmode;
    public String owner;
    public String group;
    public Boolean recursive;
    public Boolean addParents;

    public XFilePlatformSpecific specific;

}

