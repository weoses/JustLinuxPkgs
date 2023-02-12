import builders.BuildType;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Main {

    static Logger logger = LoggerFactory.getLogger(Main.class.getName());
    public static void main(String[] args) {
        ArgumentParser parser = ArgumentParsers.newFor("JustLinuxPkgs").build()
                .description("Build an RPM or DEB file");

        parser.addArgument("--config", "-c")
                .dest("config")
                .type(File.class)
                .setDefault("config.xml")
                .help("configuration file (default ./config.xml)");

        parser.addArgument("--output", "-o")
                .required(true)
                .dest("out")
                .type(File.class)
                .help("output directory");

        parser.addArgument("--format", "-f")
                .required(true)
                .dest("format")
                .type(BuildType.class)
                .choices(BuildType.values())
                .help("type of package");

        parser.addArgument("--keywords", "-k")
                .nargs("*")
                .dest("keywords");

        try {
            Namespace res = parser.parseArgs(args);

            File configFile = res.get("config");
            File out = res.get("out");
            List<String> keywords = res.getList("keywords");
            BuildType pkgType = res.get("format");

            logger.info(String.format("Config - %s", configFile));
            logger.info(String.format("Out - %s", out));
            logger.info(String.format("PkgType - %s", pkgType));
            logger.info(String.format("Keywords - %s", keywords));

            if (!configFile.exists()){
                logger.error(String.format("Config file %s not exist", configFile));
                return;
            }

            if (out.exists() && out.isFile()){
                logger.error("Output must a directory");
                return;
            }

            if (!out.exists()) {
                if (!out.mkdirs()){
                    logger.error("Cant create an output directory");
                    return;
                }
            }

            // Parse keyword arg
            Map<String,String> keywordsMap = new HashMap<>();
            for (String keyword : keywords) {
                String[] data = keyword.split("=", 2);
                if (data.length == 1) continue;
                keywordsMap.put(StringUtils.lowerCase(data[0]), data[1]);
            }

            // Read document, replace
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document document = docBuilder.parse(configFile);
            new DomReplacer(document).processReplaces(keywordsMap);

            Config config = new Config(document, pkgType, out);

            BuilderWrapper wrapper = BuilderWrapper.createBuilderWrapper(config);
            if (wrapper == null) return;
            wrapper.build(config.getOutput());

        } catch (ArgumentParserException e) {
            parser.handleError(e);
        } catch (Exception e){
            logger.error("Cant start program - " + e, e);
        }
    }


}
