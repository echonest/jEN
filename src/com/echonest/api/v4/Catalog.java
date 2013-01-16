package com.echonest.api.v4;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

abstract class Catalog extends ENItem {

    private final static String PATH = "catalog";
    private final static String TYPE = "catalog";
    protected int totalSize = -1;

    Catalog(EchoNestAPI en, Map data) throws EchoNestException {
        super(en, TYPE, PATH, data);
    }

    /**
     * Gets the name of the catalog
     *
     * @return
     */
    public String getName() {
        return getString("name");
    }

    /**
     * Returns the total number of elements in the catalog
     *
     * @return
     */
    public int getTotal() {
        return getInteger("total");
    }

    /**
     * Returns the total number of elements in the catalog
     *
     * @return
     */
    public String getType() {
        return getString("type");
    }

    /**
     * Returns true if the catalog is an artist catalog
     *
     * @return
     */
    public boolean isArtistCatalog() {
        return getType().equals("artist");
    }

    /**
     * Returns true if the catalog is a song catalog
     *
     * @return
     */
    public boolean isSongCatalog() {
        return getType().equals("song");
    }

    /**
     * Returns true if the catalog is a general catalog
     *
     * @return
     */
    public boolean isGeneralCatalog() {
        return getType().equals("general");
    }

    /**
     * Returns the score (if available) for the catalog. Catalogs will have
     * scores if they were created/returned via a catalog/similar call.
     *
     * @return the catalog score
     */
    public double getScore() {
        return getDouble("score");
    }

    /**
     *
     */
    public Map<String, String> getKeyValues() throws EchoNestException {
        Params p = new Params();
        p.add("id", getID());
        Map response = en.getCmd().sendCommand("catalog/keyvalues", p);
        JSONObject kv = (JSONObject) response.get("keyvalues");
        Map<String, String> results = new HashMap<String, String>();
        for (Object key : kv.keySet()) {
            Object val = kv.get(key);
            results.put((String) key, (String) val);
        }
        return results;
    }

    public float predict(String category) throws EchoNestException {
        Params p = new Params();
        p.add("id", getID());
        p.add("category", category);
        Map response = en.getCmd().sendCommand("catalog/predict", p);
        JSONArray list = (JSONArray) response.get("predictions");
        JSONObject map = (JSONObject) list.get(0);
        Number results = (Number) map.get("results");
        return results.floatValue();
    }

    /**
     * Deletes this catalog
     *
     * @throws EchoNestException
     */
    public void delete() throws EchoNestException {
        Params p = new Params();
        p.add("id", getID());
        en.getCmd().sendCommand("catalog/delete", p, true);
    }

    /**
     * Increment the playcount for the given items by the given count
     *
     * @param id the ID of the item in the taste profile.
     * @param plays increment the play count for the specified items by the
     * given value
     * @throws EchoNestException
     */
    public void play(String item, int plays) throws EchoNestException {
        Params p = new Params();
        p.add("id", getID());
        p.add("item", item);
        p.add("plays", plays);
        en.getCmd().sendCommand("catalog/play", p);
    }

    /**
     * Increment the playcount for the given items by the given count
     *
     * @param id the ID of the item in the taste profile.
     * @param plays increment the play count for the specified items by the
     * given value
     * @throws EchoNestException
     */
    public void play(List<String> items, int plays) throws EchoNestException {
        Params p = new Params();
        p.add("id", getID());
        p.add("item", items);
        p.add("plays", plays);
        en.getCmd().sendCommand("catalog/play", p);
    }

    /**
     * Increment the skip count for the given items by the given count
     *
     * @param item the ID of the item in the taste profile.
     * @param skips increment the skip count for the specified items by the
     * given value
     * @throws EchoNestException
     */
    public void skip(String item, int skips) throws EchoNestException {
        Params p = new Params();
        p.add("id", getID());
        p.add("item", item);
        p.add("skips", skips);
        en.getCmd().sendCommand("catalog/skip", p);
    }

    /**
     * Increment the skip count for the given items by the given count
     *
     * @param items the ID of the item in the taste profile.
     * @param plays increment the skip count for the specified items by the
     * given value
     * @throws EchoNestException
     */
    public void skip(List<String> items, int skips) throws EchoNestException {
        Params p = new Params();
        p.add("id", getID());
        p.add("item", items);
        p.add("skips", skips);
        en.getCmd().sendCommand("catalog/skip", p);
    }

    /**
     * Indicates that the given items have been favorited or unfavorited
     *
     * @param id the ID of the item in the taste profile.
     * @param fav is the item a favorite
     * @throws EchoNestException
     */
    public void favorite(String item, boolean fav) throws EchoNestException {
        Params p = new Params();
        p.add("id", getID());
        p.add("item", item);
        p.add("favorite", fav);
        en.getCmd().sendCommand("catalog/favorite", p);
    }

    /**
     * Indicates that the given items have been favorited or unfavorited
     *
     * @param items the IDs of the item in the taste profile.
     * @param fav is the item a favorite given value
     * @throws EchoNestException
     */
    public void favorite(List<String> items, boolean fav) throws EchoNestException {
        Params p = new Params();
        p.add("id", getID());
        p.add("item", items);
        p.add("favorite", fav);
        en.getCmd().sendCommand("catalog/favorite", p);
    }

    /**
     * Indicates that the given items have been banned or not
     *
     * @param id the ID of the item in the taste profile.
     * @param ban is the item a favorite
     * @throws EchoNestException
     */
    public void ban(String item, boolean ban) throws EchoNestException {
        Params p = new Params();
        p.add("id", getID());
        p.add("item", item);
        p.add("ban", ban);
        en.getCmd().sendCommand("catalog/ban", p);
    }

    /**
     * Indicates that the given items have been banned or not
     *
     * @param items the IDs of the item in the taste profile.
     * @param ban is the item banned given value
     * @throws EchoNestException
     */
    public void ban(List<String> items, boolean ban) throws EchoNestException {
        Params p = new Params();
        p.add("id", getID());
        p.add("item", items);
        p.add("ban", ban);
        en.getCmd().sendCommand("catalog/ban", p);
    }

    /**
     * apply the given rating to the given items
     *
     * @param items the IDs of the item in the taste profile.
     * @param rating a value between zero and ten. 5 is a neutral rating.
     * @throws EchoNestException
     */
    public void rate(String item, boolean rating) throws EchoNestException {
        Params p = new Params();
        p.add("id", getID());
        p.add("item", item);
        p.add("rating", rating);
        en.getCmd().sendCommand("catalog/rate", p);
    }

    /**
     * apply the given rating to the given items
     *
     * @param items the IDs of the item in the taste profile.
     * @param rating a value between zero and ten. 5 is a neutral rating.
     * @throws EchoNestException
     */
    public void rate(List<String> items, boolean rating) throws EchoNestException {
        Params p = new Params();
        p.add("id", getID());
        p.add("item", items);
        p.add("rating", rating);
        en.getCmd().sendCommand("catalog/rating", p);
    }

    /**
     * Updates the catalog
     *
     * @param update the catalog updater
     * @return a ticket
     * @throws EchoNestException
     */
    @SuppressWarnings("unchecked")
    public String update(CatalogUpdater updater) throws EchoNestException {
        Params p = new Params();
        p.add("id", getID());
        p.add("data", updater.toString());
        Map results = en.getCmd().post("catalog/update", p.getMap());
        Map response = (Map) results.get("response");
        String ticket = (String) response.get("ticket");
        return ticket;
    }

    /**
     * Determines if the processing for a ticket has completed
     *
     * @param ticketn the ticket of interest
     * @return true if the ticket has been completed
     * @throws EchoNestException
     */
    @SuppressWarnings("unchecked")
    public boolean isComplete(String ticket) throws EchoNestException {
        Params p = new Params();
        p.add("ticket", ticket);
        Map results = en.getCmd().sendCommand("catalog/status", p);
        Map response = (Map) results.get("response");
        String status = (String) response.get("ticket_status");
        if (status.equals("pending")) {
            return false;
        } else if (status.equals("complete")) {
            return true;
        } else if (status.equals("error")) {
            String details = (String) response.get("details");
            throw new EchoNestException(
                    EchoNestException.ERR_INVALID_PARAMETER, details);
        } else {
            throw new EchoNestException(
                    EchoNestException.CLIENT_SERVER_INCONSISTENCY,
                    "unknown ticket");
        }
    }

    /**
     * Waits for the updates associated with a ticket to complete
     *
     * @param ticket the ticket of interest
     * @param timeoutMillis the maximum milliseconds to wait for updates to be
     * done
     * @return true if the catalog is done
     * @throws EchoNestException
     */
    public boolean waitForUpdates(String ticket, long timeoutMillis)
            throws EchoNestException {
        long startTime = System.currentTimeMillis();
        long elapsed = 0;
        boolean done = false;
        do {
            done = isComplete(ticket);
            elapsed = System.currentTimeMillis() - startTime;
            if (!done) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        } while (!done && elapsed < timeoutMillis);
        return done;
    }
}
