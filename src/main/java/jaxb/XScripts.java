package jaxb;

import jaxb.attributeAdapters.AttributeStringAdapter;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

public class XScripts {
    @XmlJavaTypeAdapter(AttributeStringAdapter.class)
    public String preInstallScript;

    @XmlJavaTypeAdapter(AttributeStringAdapter.class)
    public String postInstallScript;

    @XmlJavaTypeAdapter(AttributeStringAdapter.class)
    public String preRmScript;

    @XmlJavaTypeAdapter(AttributeStringAdapter.class)
    public String postRmScript;
}
