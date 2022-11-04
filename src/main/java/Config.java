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
    private final String input;
    private final String output;

    public Config(String configFile,
                  BuildType type,
                  String input,
                  String output) throws JAXBException {
        xConfig = (XConfig) configUnmarshaller.unmarshal(new File(configFile));
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

    public String getOutput() {
        return output;
    }

    public String getInput() {
        return input;
    }
}
