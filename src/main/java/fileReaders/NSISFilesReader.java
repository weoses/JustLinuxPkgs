package fileReaders;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Nsis file parser. Parses only "File (/r) (path)" clause
 */
public class NSISFilesReader {

    private final Pattern quotes = Pattern.compile("^\"(.*)\"$|(.+)");
    private final Logger logger = LoggerFactory.getLogger(NSISFilesReader.class.getName());
    private final File nsisFile;

    private final Map<String, Tokenized> handlers = new HashMap<>();

    public NSISFilesReader(File nsis){
        this.nsisFile = nsis;
        handlers.put("file", this::parseFile);
        handlers.put("setoutpath", this::parseOutPath);
    }

    public List<NsisFileEntry> parse(){
        ParserState state = new ParserState();

        try (BufferedReader reader = new BufferedReader(new FileReader(nsisFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                parseLine(line, state);
            }
        } catch (IOException e) {
            logger.error("Error reading nsis file: " + e.getMessage());
        }
        return state.entries;
    }

    private String unQuote(String quoted){
        return quotes.matcher(quoted).replaceAll("$1$2");
    }


    private void parseFile(StringTokenizer tokenizer, ParserState state){
        final String onameOption = "/oname=";
        final String recursiveOption = "/r";

        boolean recursive = false;
        String name = "";
        String file = "";
        String token = "";
        while (tokenizer.hasMoreTokens() && (token = tokenizer.nextToken()) != null) {
            if (token.startsWith(onameOption)){
                name = unQuote(token.substring(onameOption.length()));
            } else if (token.equals(recursiveOption)){
                recursive = true;
            } else {
                file = unQuote(token);
                if (StringUtils.isEmpty(name)){
                    name = FilenameUtils.getName(file);
                }
            }
        }

        state.entries.add(new NsisFileEntry(name, file, state.outPath, recursive));
    }

    private void parseOutPath(StringTokenizer tokenizer, ParserState state){
        String token = "";
        if (tokenizer.hasMoreTokens() && (token = tokenizer.nextToken()) != null) {
            state.outPath = unQuote(token);
        }
    }
    private void parseLine(String line, ParserState state){
        StringTokenizer tokenizer = new StringTokenizer(line);
        while (tokenizer.hasMoreTokens())
        {
            String nextToken  = tokenizer.nextToken();
            Tokenized handler = handlers.get(StringUtils.lowerCase(nextToken));
            if (handler != null)
                handler.parse(tokenizer, state);
        }

    }

    static class ParserState{
        List<NsisFileEntry> entries = new ArrayList<>();
        String outPath = ".";
    }

    public static class NsisFileEntry{

        public NsisFileEntry(String oname, String filePath, String outPath, boolean recursive) {
            this.oname = oname;
            this.filePath = filePath;
            this.outPath = outPath;
            this.recursive = recursive;
        }

        public String oname;
        public String filePath;
        public String outPath;
        public boolean recursive;

    }

    interface Tokenized{
        void parse(StringTokenizer tokens, ParserState state);
    }
}
