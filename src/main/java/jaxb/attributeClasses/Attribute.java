package jaxb.attributeClasses;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;
import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * Used for define params with "fromFile" function.
 */
public class Attribute {
    static Logger logger = LoggerFactory.getLogger(Attribute.class.getName());
    private String value;
    private boolean initialized = false;

    @XmlAttribute(name = "fromFile")
    private String fromFile;
    @XmlValue
    private String valueXml;

    public String getValue() {
        if (!initialized) {
            if (StringUtils.isEmpty(fromFile)) {
                value = valueXml;
                initialized = true;
            } else {
                try {
                    value = FileUtils.readFileToString(new File(fromFile), StandardCharsets.UTF_8);
                } catch (Exception e){
                    logger.warn(String.format("Cant load value - from file was %s", fromFile));
                    logger.warn(e.toString());
                }
            }
        }
        return value;
    }
}
