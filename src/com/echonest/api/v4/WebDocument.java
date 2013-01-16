package com.echonest.api.v4;

import com.echonest.api.v4.util.MQuery;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class WebDocument {

    private static SimpleDateFormat ISO8601FORMAT = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss");
    private static SimpleDateFormat RFC822DATEFORMAT = new SimpleDateFormat(
            "EEE', 'dd' 'MMM' 'yyyy' 'HH:mm:ss' 'Z", Locale.US);
    @SuppressWarnings("unchecked")
    private Map map;
    private String type;

    @SuppressWarnings("unchecked")
    protected WebDocument(String type, Map map) {
        this.type = type;
        this.map = map;
    }

    @SuppressWarnings("unchecked")
    public void dump(boolean abbreviated) {
        List keys = new ArrayList(map.keySet());
        Collections.sort(keys);
        for (Object k : keys) {
            String val = map.get(k).toString();
            if (abbreviated && val.length() > 50) {
                val = val.substring(0, 50);
            }
            System.out.printf("  %20s %s\n", k, val);
        }
        System.out.println();
    }

    public void dump() {
        dump(false);
    }

    protected String getString(String path) {
        return (String) getObject(path);
    }

    protected Double getDouble(String path) {
        // BUG, workaround for JSON formatting error
        Object value = getObject(path);
        if (value instanceof String) {
            return Double.parseDouble((String) value);
        } else {
            return (Double) value;
        }
    }

    protected Object getObject(String path) {
        MQuery mq = new MQuery(map);
        return mq.getObject(path);
    }

    protected Date getDate(String path) {
        String date = getString(path);
        if (date != null && date.length() > 0) {
            try {
                return RFC822DATEFORMAT.parse(date);
            } catch (ParseException e) {
                try {
                    return ISO8601FORMAT.parse(date);
                } catch (ParseException ex) {
                    System.out.println("Can't parse " + date);
                    return null;
                }
            }
        } else {
            return null;
        }
    }
}
