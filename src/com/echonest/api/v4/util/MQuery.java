package com.echonest.api.v4.util;

import java.util.List;
import java.util.Map;

public class MQuery {

    @SuppressWarnings("unchecked")
    private Map data;

    public MQuery(Map map) {
        data = map;
    }

    // a.b[2].c.d
    @SuppressWarnings("unchecked")
    /**
     * Index into a nested JSON-style map with paths like a.b[3].c.d.e The
     * json-style map can hold, Numbers, Strings, Maps and Lists, which is why
     * we return nothing more specific than an Object.
     */
    public Object getObject(String path) {
        String[] fields = path.split("\\.");
        Object obj = data;
        for (int i = 0; i < fields.length && obj != null; i++) {
            Map map = (Map) obj;
            String field = fields[i];

            if (hasListIndex(field)) {
                String fname = getName(field);
                int findex = getIndex(field);
                if (map.containsKey(fname)) {
                    List list = (List) map.get(fname);
                    obj = list.get(findex);
                } else {
                    obj = null;
                }
            } else {
                if (map.containsKey(field)) {
                    obj = map.get(field);
                } else {
                    obj = null;
                }
            }
        }
        return obj;
    }

    private boolean hasListIndex(String field) {
        return field.indexOf("[") >= 0;
    }

    private String getName(String f) {
        int lbracket = f.indexOf('[');
        return f.substring(0, lbracket);
    }

    private int getIndex(String f) {
        int lbracket = f.indexOf('[');
        int rbracket = f.indexOf(']');
        String sindex = f.substring(lbracket + 1, rbracket);
        return Integer.parseInt(sindex);
    }

    public String getString(String path) {
        return (String) getObject(path);
    }

    public Double getDouble(String path) {
        return (Double) getObject(path);
    }

    public Long getLong(String path) {
        return (Long) getObject(path);
    }

    public Integer getInteger(String path) {
        Long l = getLong(path);
        if (l != null) {
            return Integer.valueOf(l.intValue());
        } else {
            return null;
        }
    }

    public Integer getInteger(String path, int defaultValue) {
        Integer val = getInteger(path);
        if (val == null) {
            val = Integer.valueOf(defaultValue);
        }
        return val;
    }
}
