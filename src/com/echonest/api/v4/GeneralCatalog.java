/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.echonest.api.v4;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author plamere
 */
public class GeneralCatalog extends Catalog {

    GeneralCatalog(EchoNestAPI en, Map data) throws EchoNestException {
        super(en, data);
    }

    /**
     * Read items from the catalog
     *
     * @param start the starting index
     * @param resultCount the result count
     * @param buckets the buckets to return
     * @return the list of items
     * @throws EchoNestException
     */
    @SuppressWarnings({"unchecked"})
    public List<CatalogItem> read(int start, int resultCount, String[] buckets)
            throws EchoNestException {
        Params p = new Params();
        p.set("id", getID());
        p.set("start", start);
        p.set("results", resultCount);
        if (buckets != null) {
            for (String bucket : buckets) {
                p.add("bucket", bucket);
            }
        }
        Map results = en.getCmd().sendCommand("catalog/read", p, false);
        Map response = (Map) results.get("response");
        Map catalog = (Map) response.get("catalog");
        String name = (String) catalog.get("name");
        String id = (String) catalog.get("id");
        String type = (String) catalog.get("type");

        //System.out.printf("%s %s\n", getID(), id);

        if (!getID().equals(id)) {
            throw new EchoNestException(
                    EchoNestException.CLIENT_SERVER_INCONSISTENCY,
                    "returned ID doesn't match requested ID");
        }

        if (!getType().equals(type)) {
            throw new EchoNestException(
                    EchoNestException.CLIENT_SERVER_INCONSISTENCY,
                    "returned type doesn't match expected type");
        }

        if (!getName().equals(name)) {
            throw new EchoNestException(
                    EchoNestException.CLIENT_SERVER_INCONSISTENCY,
                    "returned name doesn't match expected type");
        }


        Long size = (Long) catalog.get("total");
        totalSize = size.intValue();

        Long lstart = (Long) catalog.get("start");

        if (start != lstart.intValue()) {
            throw new EchoNestException(
                    EchoNestException.CLIENT_SERVER_INCONSISTENCY,
                    "returned start value doesn't match expected start value");
        }

        List items = (List) catalog.get("items");
        List<CatalogItem> itemList = new ArrayList<CatalogItem>();
        for (int i = 0; i < items.size(); i++) {
            Map itemMap = (Map) items.get(i);

            if (itemMap.get("song_id") == null) {
                ArtistCatalogItem item = new ArtistCatalogItem(itemMap);
                itemList.add(item);
            } else {
                SongCatalogItem item = new SongCatalogItem(itemMap);
                itemList.add(item);
            }
        }
        return itemList;
    }

    /**
     * Read items from the catalog
     *
     * @param start starting index
     * @param resultCount max items to read
     * @return a list of items
     * @throws EchoNestException
     */
    public List<CatalogItem> read(int start, int resultCount)
            throws EchoNestException {
        return read(start, resultCount, null);
    }

    /**
     * Read all the items from the catalog
     *
     * @param buckets
     * @return
     * @throws EchoNestException
     */
    public List<CatalogItem> read(String[] buckets) throws EchoNestException {
        final int MAX_READS = 100;
        List<CatalogItem> itemList = new ArrayList<CatalogItem>();

        while (totalSize < 0 || itemList.size() < totalSize) {
            itemList.addAll(read(itemList.size(), MAX_READS, buckets));
        }
        return itemList;
    }

    /**
     * Read all the items from the catalog
     *
     * @return
     * @throws EchoNestException
     */
    public List<CatalogItem> read() throws EchoNestException {
        return read(null);
    }
}
