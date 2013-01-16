package com.echonest.api.v4;

import java.util.HashMap;
import java.util.Map;
import org.json.simple.JSONArray;

public class CatalogUpdater {

    JSONArray arry = new JSONArray();

    /**
     * Deletes the item from the catalog
     *
     * @param myID the id of the item
     */
    @SuppressWarnings("unchecked")
    public void delete(String myID) {
        arry.add(getOpMap("delete", myID));
    }

    /**
     * Add values to the catalog level kvstore
     *
     * @param kvstore the items to be added to the kv store
     */
    public void addKeyValueStore(Map<String, String> kvstore) {
        Map<String, Object> opMap = new HashMap<String, Object>();
        opMap.put("action", "update");
        opMap.put("catalog_keyvalues", kvstore);
        arry.add(opMap);
    }

    /**
     * Indicate that the given item has been skipped
     *
     * @param myID the id of the item
     */
    @SuppressWarnings("unchecked")
    public void skip(String myID) {
        arry.add(getOpMap("skip", myID));
    }

    /**
     * Indicate that the given item has been played
     *
     * @param myID
     */
    @SuppressWarnings("unchecked")
    public void play(String myID) {
        arry.add(getOpMap("play", myID));
    }

    /**
     * Indicate that the given item has been banned
     *
     * @param myID
     */
    @SuppressWarnings("unchecked")
    public void ban(String myID) {
        arry.add(getOpMap("ban", myID));
    }

    /**
     * Indicate that the given item has been unbanned
     *
     * @param myID
     */
    @SuppressWarnings("unchecked")
    public void unban(String myID) {
        arry.add(getOpMap("unban", myID));
    }

    /**
     * Indicate that the given item is a favorite
     *
     * @param myID
     */
    @SuppressWarnings("unchecked")
    public void favorite(String myID) {
        arry.add(getOpMap("favorite", myID));
    }

    /**
     * Indicate that the given item is no longer a favorite
     *
     * @param myID
     */
    @SuppressWarnings("unchecked")
    public void unfavorite(String myID) {
        arry.add(getOpMap("unfavorite", myID));
    }

    /**
     * Updates the given item
     *
     * @param item the item to be updated
     */
    @SuppressWarnings("unchecked")
    public void update(CatalogItem item) {
        Map<String, Object> opMap = getOpMap("update", item);
        arry.add(opMap);
    }

    @Override
    public String toString() {
        return arry.toJSONString();
    }

    private Map<String, Object> getOpMap(String action, String id) {
        Map<String, Object> opMap = new HashMap<String, Object>();
        opMap.put("action", action);
        opMap.put("item", getItemMap(id));
        return opMap;
    }

    private Map<String, Object> getOpMap(String action, CatalogItem item) {
        Map<String, Object> opMap = new HashMap<String, Object>();
        opMap.put("action", action);
        opMap.put("item", item.getMap());
        return opMap;
    }

    private Map<String, String> getItemMap(String id) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("item_id", id);
        return map;
    }
}
