package jaxb;

import javax.xml.bind.annotation.XmlAttribute;
import java.util.ArrayList;
import java.util.List;


public class XFileNsis {
    @XmlAttribute(required = true)
    public String nsisFilePath;
    @XmlAttribute(required = true)

    public String nsisWorkingDirectory = ".";

    public List<XExclude> exclude;

    public List<XReplace> replace;

    public XFileNsis() {
        exclude = new ArrayList<>();
        replace = new ArrayList<>();
    }
}
