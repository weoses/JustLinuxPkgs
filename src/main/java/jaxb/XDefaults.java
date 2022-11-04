package jaxb;

import jaxb.attributeAdapters.ModeParserAdapter;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

public class XDefaults {
    @XmlJavaTypeAdapter(jaxb.attributeAdapters.ModeParserAdapter.class)
    public Integer defaultDirmode;

    @XmlJavaTypeAdapter(ModeParserAdapter.class)
    public Integer defaultMode;
}
