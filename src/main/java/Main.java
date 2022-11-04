import builders.BuildType;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;


public class Main {
    public static void main(String[] args) {
        parseArgs(args);
    }
    private static void parseArgs(String[] args){
        ArgumentParser parser = ArgumentParsers.newFor("rpmBuilder").build()
                .description("Build an RPM file");

        parser.addArgument("--conf", "-c")
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

        try {
            Namespace res = parser.parseArgs(args);

            String configFile = res.getString("conf");
            String out = res.getString("out");
            String in = res.getString("in");

            System.out.printf("Config - %s%n", configFile);
            System.out.printf("Out - %s%n", out);
            System.out.printf("In - %s%n", in);

            Config conf = new Config(configFile, BuildType.RPM, in, out);
            createAndRunBuilder(conf);

        } catch (ArgumentParserException e) {
            parser.handleError(e);
        } catch (JAXBException e){
            System.err.println("Cant read config!");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("IO error!");
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Cant compress RPM!");
            e.printStackTrace();
        }
    }

    private static void createAndRunBuilder(Config config) throws IOException, NoSuchAlgorithmException {
        BuilderWrapper wrapper = BuilderWrapper.createBuilderWrapper(config);
        wrapper.build(config.getOutput());
    }
}
