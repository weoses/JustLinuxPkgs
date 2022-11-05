package jaxb;

import jaxb.attributeClasses.AttributeStringAdapter;
import org.redline_rpm.header.Architecture;
import org.redline_rpm.header.FileDigestsAlg;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

public class XPkgParams {
    @XmlJavaTypeAdapter(AttributeStringAdapter.class)
    public String packageName;
    @XmlJavaTypeAdapter(AttributeStringAdapter.class)
    public String packageVersion;
    @XmlJavaTypeAdapter(AttributeStringAdapter.class)
    public String packageLicense;

    @XmlJavaTypeAdapter(AttributeStringAdapter.class)
    public String packageDescription;





    /* specific */
    /* DEB      */
    public DebPackageArch debPackageArch;

    @XmlJavaTypeAdapter(AttributeStringAdapter.class)
    public String debMaintainer;

    /* RPM     */
    @XmlElement(defaultValue = "1")
    public int rpmPackageEpoch;

    @XmlJavaTypeAdapter(AttributeStringAdapter.class)
    @XmlElement(defaultValue = "1")
    public String rpmPackageRelease;
    public FileDigestsAlg rpmFileDigestsAlg;

    public Architecture rpmPackageArch;
}
