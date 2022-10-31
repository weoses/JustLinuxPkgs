import jaxb.XConfig;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;

public class Config {
    private static final Unmarshaller configUnmarshaller;

    static {
        try {
            JAXBContext context = JAXBContext.newInstance(XConfig.class);
            configUnmarshaller = context.createUnmarshaller();
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    private final XConfig xConfig;

    public Config(String file) throws JAXBException {
        xConfig = (XConfig) configUnmarshaller.unmarshal(new File(file));
    }


    public XConfig getxConfig() {
        return xConfig;
    }
}
