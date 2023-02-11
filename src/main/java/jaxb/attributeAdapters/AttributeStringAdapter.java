package jaxb.attributeAdapters;

import jaxb.attributeClasses.Attribute;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class AttributeStringAdapter extends XmlAdapter<Attribute, String> {


    @Override
    public String unmarshal(Attribute v) throws Exception {
        return v.getValue();
    }

    @Override
    public Attribute marshal(String v) throws Exception {
        return null;
    }
}
