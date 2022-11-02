package jaxb;

import jaxb.attributeAdapters.ArchAdapter;
import jaxb.attributeAdapters.ModeParserAdapter;
import jaxb.attributeClasses.AttributeStringAdapter;
import org.redline_rpm.header.Architecture;
import org.redline_rpm.header.FileDigestsAlg;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name="config")
public class XConfig {
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

    @XmlJavaTypeAdapter(ArchAdapter.class)
    public Architecture packageArch;

    @XmlJavaTypeAdapter(FileDigestsAlgAdapter.class)
    public FileDigestsAlg fileDigestsAlg;

    @XmlJavaTypeAdapter(ModeParserAdapter.class)
    public Integer defaultDirmode;
    @XmlJavaTypeAdapter(ModeParserAdapter.class)
    public Integer defaultMode;

    @XmlElementWrapper(name = "buildInDirs")
    public List<String> buildInDir;
    @XmlElementWrapper(name = "filesDescription")
    public List<XFile> packageFileDescription;

    public XConfig(){
        buildInDir = new ArrayList<>();
        packageFileDescription = new ArrayList<>();
    }

}
