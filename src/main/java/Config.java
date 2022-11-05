import builders.BuildType;
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
    private final BuildType type;
    private final File input;
    private final File output;

    public Config(File configFile,
                  BuildType type,
                  File input,
                  File output) throws JAXBException {
        xConfig = (XConfig) configUnmarshaller.unmarshal(configFile);
        this.type = type;
        this.input = input;
        this.output = output;
    }


    public XConfig getxConfig() {
        return xConfig;
    }

    public BuildType getType() {
        return type;
    }

    public File getOutput() {
        return output;
    }

    public File getInput() {
        return input;
    }
}
