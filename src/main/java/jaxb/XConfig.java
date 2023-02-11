package jaxb;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name="config")
public class XConfig {
    @XmlElement(required = true)
    public XPkgParams pkgParams;
    public XDefaults defaults;
    @XmlElementWrapper(name = "buildInDirs")
    public List<String> buildInDir;

    public XFiles files;

    public XScripts scripts;

}
