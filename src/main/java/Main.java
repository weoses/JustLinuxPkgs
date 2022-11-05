import builders.BuildType;
import builders.deb.DebBuilder;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;


public class Main {

    static Logger logger = LoggerFactory.getLogger(Main.class.getName());
    public static void main(String[] args) {
        parseArgs(args);
    }
    private static void parseArgs(String[] args){
        ArgumentParser parser = ArgumentParsers.newFor("rpmBuilder").build()
                .description("Build an RPM file");

        parser.addArgument("--conf", "-c")
                .required(true)
                .dest("conf")
                .type(String.class)
                .setDefault("config.xml")
                .help("configuration file (default ./config.xml)");

        parser.addArgument("--input", "-i")
                .required(true)
                .dest("in")
                .help("input directory");

        parser.addArgument("--output", "-o")
                .required(true)
                .dest("out")
                .help("output directory");

        parser.addArgument("--format", "-f")
                .required(true)
                .dest("format")
                .type(BuildType.class)
                .choices(BuildType.values())
                .help("type of package");

        try {
            Namespace res = parser.parseArgs(args);

            String configFile = res.getString("conf");
            String out = res.getString("out");
            String in = res.getString("in");
            BuildType pkgType = res.get("format");

            logger.info(String.format("Config - %s", configFile));
            logger.info(String.format("Out - %s", out));
            logger.info(String.format("In - %s", in));
            logger.info(String.format("pkgType - %s", pkgType));

            Config conf = new Config(configFile, pkgType, in, out);
            createAndRunBuilder(conf);

        } catch (ArgumentParserException e) {
            parser.handleError(e);
        } catch (Exception e){
            logger.error("Cant start program - " + e, e);
        }
    }

    private static void createAndRunBuilder(Config config){
        File inputDir = new File(config.getInput());
        File outputDir = new File(config.getOutput());

        if (inputDir.isFile() || !inputDir.exists()){
            logger.error("Input directory not exist!");
            return;
        }

        if (outputDir.exists() && outputDir.isFile()){
            logger.error("Output must a directory");
            return;
        }

        if (!outputDir.exists()) {
            if (!outputDir.mkdirs()){
                logger.error("Cant create an output directory");
                return;
            }
        }

        BuilderWrapper wrapper = BuilderWrapper.createBuilderWrapper(config);
        if (wrapper == null) return;
        wrapper.build(config.getOutput());
    }
}
