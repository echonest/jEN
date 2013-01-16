package com.echonest.api.v4;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.simple.JSONValue;

/**
 * Manages parameter sets for API calls
 * 
 * @author plamere
 * 
 */
public class Params {
    private Map<String, Object> map = new HashMap<String, Object>();

    /**
     * Adds a parameter and value to the parameter set
     * 
     * @param key
     * @param value
     */
    @SuppressWarnings("unchecked")
    public void add(String key, String value) {
        Object curValue = map.get(key);

        if (curValue == null) {
            map.put(key, value);
        } else {
            if (curValue instanceof List) {
                List<String> plist = (List<String>) curValue;
                plist.add(value);
            } else {
                List<String> plist = new ArrayList<String>();
                plist.add((String) curValue);
                plist.add(value);
                map.put(key, plist);
            }
        }
    }

    /**
     * Sets a parameter and value to the parameter set
     * 
     * @param key
     * @param value
     */
    public void set(String key, String value) {
        map.put(key, value);
    }

    /**
     * Gets the number of parameters
     * 
     * @return
     */
    public int size() {
        return map.size();
    }

    public void add(String key, int value) {
        add(key, Integer.toString(value));
    }

    public void add(String key, List<String> vals) {
        for (String v : vals) {
            add(key, v);
        }
    }

    public void add(String key, String[] vals) {
        for (String v : vals) {
            add(key, v);
        }
    }

    public void add(String key, float value) {
        add(key, Float.toString(value));
    }

    public void add(String key, boolean value) {
        add(key, value ? "true" : "false");
    }

    public void set(String key, float value) {
        set(key, Float.toString(value));
    }

    public void set(String key, boolean value) {
        set(key, value ? "true" : "false");
    }

    public void set(String key, int value) {
        set(key, Integer.toString(value));
    }
    
        
    public void set(String key, Map map) {
        String smap = JSONValue.toJSONString(map);
        set(key, map);
    }


    @SuppressWarnings("unchecked")
    public String toString(boolean first) {
        StringBuilder sb = new StringBuilder();
        List<String> keys = new ArrayList<String>(map.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            Object value = map.get(key);
            if (value != null) {
                if (value instanceof List) {
                    List lvals = new ArrayList((List)value);
                    Collections.sort(lvals);
                    for (Object v : lvals) {
                        sb.append(getDelim(sb, first));
                        sb.append(key);
                        sb.append("=");
                        sb.append(encode(v.toString()));
                    }
                } else {
                    sb.append(getDelim(sb, first));
                    sb.append(key);
                    sb.append("=");
                    sb.append(encode(value.toString()));
                }
            } else {
                sb.append(getDelim(sb, first));
                sb.append(key);
            }
        }
        return sb.toString();
    }

    private String encode(String s) {
        try {
            s = URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return s;
        }
        return s;
    }

    private String getDelim(StringBuilder sb, boolean first) {
        if (first && sb.length() == 0) {
            return "?";
        } else {
            return "&";
        }
    }
    
    public Map<String, Object> getMap() {
        return map;
    }
}
