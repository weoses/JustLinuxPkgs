package jaxb;

import jaxb.attributeAdapters.ModeParserAdapter;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

public class XDefaults {
    @XmlJavaTypeAdapter(jaxb.attributeAdapters.ModeParserAdapter.class)
    public Integer defaultDirmode = 0755;

    @XmlJavaTypeAdapter(ModeParserAdapter.class)
    public Integer defaultMode = 0644;

    public String defaultOwner = "root";
    public String defaultGroup = "root";
}
