package jaxb;

import jaxb.attributeAdapters.ArchAdapter;
import jaxb.attributeAdapters.ModeParserAdapter;
import jaxb.attributeClasses.AttributeStringAdapter;
import org.redline_rpm.header.Architecture;
import org.redline_rpm.header.FileDigestsAlg;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

public class XPkgParams {
    @XmlJavaTypeAdapter(AttributeStringAdapter.class)
    public String packageName;
    @XmlJavaTypeAdapter(AttributeStringAdapter.class)
    public String packageVersion;

    @XmlElement(defaultValue = "1")
    public int packageEpoch;

    @XmlJavaTypeAdapter(AttributeStringAdapter.class)
    @XmlElement(defaultValue = "1")
    public String packageRelease;

    @XmlJavaTypeAdapter(AttributeStringAdapter.class)
    @XmlElement(defaultValue = "HIDDEN")
    public String packageLicense;

    @XmlJavaTypeAdapter(AttributeStringAdapter.class)
    public String packageDescription;

    public Architecture packageArch;

    public FileDigestsAlg fileDigestsAlg;
}
