package jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class ModeParserAdapter extends XmlAdapter<String, Integer> {

    @Override
    public Integer unmarshal(String v) throws Exception {
        return Integer.parseInt(v, 8);
    }

    @Override
    public String marshal(Integer v) throws Exception {
        return Integer.toOctalString(v);
    }
}
