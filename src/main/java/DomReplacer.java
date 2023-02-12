import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.*;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DomReplacer {
    private final Document document;
    public DomReplacer(Document document){
        this.document = document;
    }

    public void processReplaces(Map<String, String> params){
        NodeList nodeList = document.getElementsByTagName("*");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);

            replaceContent(node, params);

            int attrsLen = node.getAttributes().getLength();
            for (int j = 0; j < attrsLen; j++){
                Node item = node.getAttributes().item(j);
                replaceContent(item, params);
            }
        }
    }

    private void replaceContent(Node node, Map<String, String> params){

        NodeList childs = node.getChildNodes();
        for (int i = 0; i < childs.getLength(); i++){
            if (!"#text".equals(childs.item(i).getNodeName())) return;
        }

        String content = node.getTextContent();
        if (StringUtils.isEmpty(content)) return;

        Pattern paramPattern = Pattern.compile("\\$\\{(\\w+)}");
        Matcher matcher = paramPattern.matcher(content);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()){
            String matches = StringUtils.lowerCase(matcher.group(1));
            matcher.appendReplacement(buffer, params.getOrDefault(matches, ""));
        }
        matcher.appendTail(buffer);
        node.setTextContent(buffer.toString());
    }
}
