package jaxb;

import javax.xml.bind.annotation.XmlAttribute;

public class XReplace {
    @XmlAttribute
    public boolean regexp;
    @XmlAttribute
    public String first;

    @XmlAttribute
    public String second;
}
