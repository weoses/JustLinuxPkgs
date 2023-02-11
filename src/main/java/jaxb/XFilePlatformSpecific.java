package jaxb;

import jaxb.attributeAdapters.DirectiveAdapter;
import org.redline_rpm.payload.Directive;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

public class XFilePlatformSpecific {
    @XmlJavaTypeAdapter(DirectiveAdapter.class)
    public Directive rpmDirective = Directive.NONE;
    public boolean debIsConfig;
}
