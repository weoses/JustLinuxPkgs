package jaxb;

import org.redline_rpm.header.Architecture;
import org.redline_rpm.header.FileDigestsAlg;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name="config")
public class XConfig {
    public String packageName;

    public String packageVersion;

    @XmlElement(defaultValue = "1")
    public int packageEpoch;

    @XmlElement(defaultValue = "1")
    public String packageRelease;

    @XmlElement(defaultValue = "HIDDEN")
    public String packageLicense;

    public String packageDescription;

    @XmlJavaTypeAdapter(ArchAdapter.class)
    public Architecture packageArch;

    @XmlJavaTypeAdapter(FileDigestsAlgAdapter.class)
    public FileDigestsAlg fileDigestsAlg;

    @XmlJavaTypeAdapter(ModeParserAdapter.class)
    public Integer defaultDirmode;
    @XmlJavaTypeAdapter(ModeParserAdapter.class)
    public Integer defaultMode;
    @XmlElementWrapper(name = "filesDescription")
    public List<XFile> packageFileDescription;
    @XmlElementWrapper(name = "buildInDirs")
    public List<String> buildInDir;

    public XConfig(){
        buildInDir = new ArrayList<>();
        packageFileDescription = new ArrayList<>();
    }

}
