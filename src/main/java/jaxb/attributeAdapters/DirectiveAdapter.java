package jaxb.attributeAdapters;

import org.redline_rpm.payload.Directive;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DirectiveAdapter extends XmlAdapter<String, Directive> {
    @Override
    public Directive unmarshal(String s) throws Exception {
        if (s == null) return null;

        List<Integer> directives = new ArrayList<>();
        for (String item : s.split("\\|")){
            String varName = "RPMFILE_"+item.toUpperCase(Locale.ROOT);
            Field constant = null;
            try {
                constant = Directive.class.getDeclaredField(varName);
                directives.add((Integer) constant.get(Directive.class));
            } catch (Exception e){
                System.err.printf("Directive type %s not exists", item);
            }

        }

        int joinedDir = 0;
        for (Integer dir : directives){
            joinedDir = joinedDir | dir;
        }
        return new Directive(joinedDir);
    }

    @Override
    public String marshal(Directive directive) throws Exception {
        //TODO
        return null;
    }
}
