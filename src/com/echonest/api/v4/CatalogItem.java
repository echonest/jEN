package com.echonest.api.v4;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a catalog item
 * 
 * @author plamere
 * 
 */
public class CatalogItem {

    private Map<String, Object> map = new HashMap<String, Object>();

    public CatalogItem(String id) {
        set("item_id", id);
    }

    @SuppressWarnings("unchecked")
    CatalogItem(Map m) {
        for (Object key : m.keySet()) {
            map.put((String) key, m.get(key));
        }
    }

    /**
     * Gets the ID of the item
     * @return
     */
    @SuppressWarnings("unchecked")
    public String getID() {
        Map<String, Object> requestMap =  (Map<String, Object>) map.get("request");
        if (requestMap != null)  {
            return (String) requestMap.get("item_id");
        } else {
            return getString("item_id");
        }
    }

    /**
     * Sets a key/value pair for the item
     * @param name the name of the pair
     * @param value the value of the pair
     */
    public final void set(String name, String value) {
        map.put(name, value);
    }

    /**
     * Sets a key/value pair
     * @param name
     * @param value
     */
    public final void set(String name, int value) {
        map.put(name, Integer.toString(value));
    }

    /**
     * Gets a string value by name
     * @param name
     * @return
     */
    public String getString(String name) {
        if (map.containsKey(name)) {
            return (String) map.get(name);
        } else {
            return null;
        }
    }
    
    /**
     * Gets the map associated with the name
     * @param name
     * @return
     */
    @SuppressWarnings("unchecked")
    public Map getMap(String name) {
        if (map.containsKey(name)) {
            return (Map) map.get(name);
        } else {
            return null;
        }
    }
    
    /**
     * Gets the list associated with the name
     * @param name
     * @return
     */
    @SuppressWarnings("unchecked")
    public List getList(String name) {
        if (map.containsKey(name)) {
            return (List) map.get(name);
        } else {
            return null;
        }
    }

    /**
     * Gets an int value associated with a name
     * @param name
     * @return
     */
    public Integer getInteger(String name) {
        return (Integer) map.get(name);
    }
    
    /**
     * Gets an float value associated with a name
     * @param name
     * @return
     */
    public Double getDouble(String name) {
        Double dval = (Double) map.get(name);
        if (dval != null) {
            return dval;
        } else {
            return null;
        }
    }
    

    Map<String, Object> getMap() {
        return map;
    }
}
