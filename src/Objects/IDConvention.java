/*
 *  TagViz
 *  2014
 */

package Objects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 *
 * @author jechoi
 */
public class IDConvention extends HashMap<String, ArrayList<String>> {
    
    public static enum Type {PREFIX, SUFFIX, INDEX, NONE};
    private String delimeter;
    private int start;
    private int end;

    
    IDConvention(String[] delimeter, int start, int end) {
	StringBuilder sb = new StringBuilder();
	for (String s : delimeter) sb.append(s);
//	this.delimeter = sb.toString().replaceAll("\\.", "\\\\.");
	this.delimeter = sb.toString();
	this.start = start;
	this.end = end;
    }
    IDConvention(String delimeter, int start, int end) {
	this(new String[] {delimeter}, start, end);
    }

    public static IDConvention create(String delimeter, int start, int end) {
	return create(delimeter, start, end, null);
    }
    public static IDConvention create(String delimeter, int start, int end, Collection<String> ids) {
	String s = new String();
	IDConvention rule = new IDConvention(delimeter, start, end);
	if (ids != null) rule.convert(ids);
	return rule;
    }
    
    public void convert(Collection<String> ids) {
	for (String s : ids) {
	    String t = convert(s);
	    if (containsKey(t) == false) put(t, new ArrayList<String>());
	    get(t).add(s);
	}
    }

    public String convert(String id) {
	String[] sa = id.split(Pattern.quote(delimeter));
	if (start <= -1 || start >= sa.length) return "";
	StringBuilder sb = new StringBuilder(sa[start]);
	boolean flag = start <= end;
	for (int i = flag ? start+1 : start-1; flag ? i <= end : i >= end; i += flag ? 1 : -1)
	{
	    sb.append(delimeter).append(sa[i]);
	}

	return sb.toString();
    }
    
    public static ArrayList<String> reverse(IDConvention map, String id) {
	return map.get(id);
    }
}
