package io.github.joseerodrigues.utils.properties;

import java.util.Enumeration;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Calcula valor da propriedade quando esta referencia outras propriedades. 
 * Este comportamento � recursivo.
 * 
 * Ex:
 * 
 * Prop: DB2_SCHEMA_CENTRAL = PROD
 * Prop: TABELA_DECISOES = ${DB2_SCHEMA_CENTRAL}.GDVT0200_DECISOES
 * 
 * getObject("TABELA_DECISOES") = PROD.GDVT0200_DECISOES
 * @author 92429
 */
public class InterpolationResourceBundle extends ResourceBundle {
    private static final String strPattern = "\\$\\{([A-Za-z0-9_]+)\\}";
    private static final Pattern PATTERN = Pattern.compile(strPattern);

    private ResourceBundle inner = null;

    public InterpolationResourceBundle(ResourceBundle inner) {
        if (inner == null){
            throw new NullPointerException("inner");
        }

        this.inner = inner;
    }

    public static String interpolate(String str, ResourceBundle bundle) {
        if (str == null){
            return null;
        }

        Matcher matcher = PATTERN.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find())
        {
            String varName = matcher.group(1);
            String val = interpolate(bundle.getString(varName), bundle);

            if (val != null){
                matcher.appendReplacement(sb, val);
            }
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    @Override
    protected Object handleGetObject(String s) {    	
    	Object o = this.inner.getObject(s);    	    	
    	
    	if (o instanceof String){
    		o = interpolate((String)o, this.inner);
    	}    
    	
        return o;
    }

    @Override
    public Enumeration<String> getKeys() {
        return this.inner.getKeys();
    }
}