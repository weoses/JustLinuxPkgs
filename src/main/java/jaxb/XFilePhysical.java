package jaxb;

import jaxb.attributeAdapters.ModeParserAdapter;
import jaxb.attributeAdapters.PathUnixAdapter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

public class XFilePhysical {
    @XmlJavaTypeAdapter(PathUnixAdapter.class)
    @XmlAttribute(required = true)
    public String pkgPath;
    @XmlAttribute
    public String physicalPath;
    @XmlJavaTypeAdapter(ModeParserAdapter.class)
    public Integer mode;
    @XmlJavaTypeAdapter(ModeParserAdapter.class)
    public Integer dirmode;
    public String owner;
    public String group;
    public boolean recursive = false;
    public boolean addParents = false;//Not working

    public XFilePlatformSpecific specific;

}

