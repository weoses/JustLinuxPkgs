package jaxb;

import jaxb.attributeAdapters.DirectiveAdapter;
import jaxb.attributeAdapters.ModeParserAdapter;
import jaxb.attributeAdapters.PathUnixAdapter;
import org.redline_rpm.payload.Directive;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

public class XFile {
    @XmlJavaTypeAdapter(PathUnixAdapter.class)
    @XmlElement(required = true)
    public String pathInRpm;
    @XmlJavaTypeAdapter(ModeParserAdapter.class)
    public Integer mode;
    @XmlJavaTypeAdapter(ModeParserAdapter.class)
    public Integer dirmode;
    public String owner;
    public String group;
    @XmlElement(defaultValue = "0")
    public Integer addChildren;
    @XmlElement(defaultValue = "1")
    public Integer addParents;
    @XmlJavaTypeAdapter(DirectiveAdapter.class)
    public Directive directive;

    @XmlTransient
    public boolean processed = false;
}

