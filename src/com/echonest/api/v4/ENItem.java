package com.echonest.api.v4;

import com.echonest.api.v4.util.MQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ENItem {

    @SuppressWarnings("unchecked")
    protected Map data;
    protected EchoNestAPI en;
    private String type;
    private String path;
    private String originalID = null;
    private String id;

    @SuppressWarnings("unchecked")
    ENItem(EchoNestAPI en, String type, String path, Map data)
            throws EchoNestException {
        this.en = en;
        this.type = type;
        this.path = path;
        this.data = data;
        this.id = findID();
        extractForeignIDs(data);
    }

    ENItem(EchoNestAPI en, String type, String path, String id)
            throws EchoNestException {
        this.en = en;
        this.type = type;
        this.path = path;
        originalID = id;
        this.id = id;
        refresh();
    }

    ENItem(EchoNestAPI en, String type, String path, String nameOrID,
            boolean byName) throws EchoNestException {
        this.en = en;
        this.type = type;
        this.path = path;
        if (byName) {
            this.data = getItemMapByName(nameOrID, null);
        } else {
            originalID = nameOrID;
            this.data = getItemMap(nameOrID);
        }
        this.id = findID();
        extractForeignIDs(data);
    }

    ENItem(EchoNestAPI en, String type, String path, String id, String idType)
            throws EchoNestException {
        this.en = en;
        this.type = type;
        this.path = path;
        originalID = id;
        this.data = getItemMap(id, null, idType);
        this.id = findID();
        extractForeignIDs(data);
    }

    private String findID() throws EchoNestException {
        String alt_name = type + "_" + "id";
        if (data.get("id") != null) {
            return (String) data.get("id");
        } else if (data.get(alt_name) != null) {
            return (String) data.get(alt_name);
        } else {
            throw new EchoNestException(
                    EchoNestException.ERR_MISSING_PARAMETER, "Missing ID");
        }
    }

    protected final void refresh() throws EchoNestException {
        this.data = getItemMap(this.id);
        this.id = findID();
        extractForeignIDs(data);
    }

    @Override
    public String toString() {
        return data.toString();
    }
    

    @SuppressWarnings("unchecked")
    public void fetchBuckets(String[] bucket) throws EchoNestException {
        fetchBuckets(bucket, false);
    }

    /**
     * Determines if the item has the given bucket. This is used primarily for
     * testing
     *
     * @param bucket the name of the bucket
     * @return true if the item has the data associated with the given bucket.
     */
    public boolean hasBucket(String bucket) {
        return data.containsKey(bucket);
    }

    @SuppressWarnings("unchecked")
    public void fetchBuckets(String[] bucket, boolean force)
            throws EchoNestException {
        List<String> buckets = new ArrayList<String>();

        for (String s : bucket) {
            if (force || !data.containsKey(s)) {
                buckets.add(s);
            }
        }
        if (buckets.size() > 0) {
            Map map = getItemMap(getID(), buckets);
            extractForeignIDs(map);
            for (String s : buckets) {
                Object value = map.get(s);
                if (value != null) {
                    data.put(s, value);
                }
            }
        }
    }

    public void fetchBucket(String bucket) throws EchoNestException {
        fetchBucket(bucket, bucket);
    }

    public void fetchBucket(String bucket, boolean force) throws EchoNestException {
        fetchBucket(bucket, bucket, force);
    }

    protected void fetchBucket(String paramName, String bucketName)
            throws EchoNestException {
        fetchBucket(paramName, bucketName, false);
    }

    @SuppressWarnings("unchecked")
    protected void fetchBucket(String paramName, String bucketName,
            boolean force) throws EchoNestException {

        if (force || !data.containsKey(bucketName)) {
            Map map = getItemMap(getID(), paramName);
            // special case for foreign_ids
            extractForeignIDs(map);
            Object value = map.get(bucketName);
            if (value != null) {
                data.put(bucketName, value);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void extractForeignIDs(Map map) {
        List idList = (List) map.get("foreign_ids");
        if (idList != null) {
            for (Object o : idList) {
                if (o instanceof String) {
                    String fullID = (String) o;
                    String catalog = fullID.split(":")[0];
                    data.put(catalog, fullID);
                } else {
                    Map idMap = (Map) o;
                    String catalog = (String) idMap.get("catalog");
                    String fid = (String) idMap.get("foreign_id");
                    data.put(catalog, fid);
                }
            }
        }
    }

    public String getID() {
        return id;
    }

    /**
     * Returns a string at the given path within the item. Paths are of the
     * form: 'key', or 'key[0]', or 'key1.key2[0].key3'
     *
     * @param path the path
     * @return the value at the path or null.
     */
    public String getString(String path) {
        return (String) getObject(path);
    }

    public Object getObject(String path) {
        MQuery mq = new MQuery(data);
        return mq.getObject(path);
    }

    @SuppressWarnings("unchecked")
    public Double getDouble(String path) {
        Object val = getObject(path);
        if (val != null) {
            // BUG: workaround API bug
            if (val instanceof List) {
                val = ((List) val).get(0);
            }
            return ((Number) val).doubleValue();
        } else {
            return Double.NaN;
        }
    }

    public Integer getInteger(String path) {
        Number val = (Number) getObject(path);
        return Integer.valueOf(val.intValue());
    }

    @SuppressWarnings("unchecked")
    public final Map getItemMap(String id) throws EchoNestException {
        return getItemMap(id, (String) null);
    }

    @SuppressWarnings("unchecked")
    public Map getItemMap(String id, String bucket) throws EchoNestException {
        return getItemMap(id, bucket, "id");
    }

    @SuppressWarnings("unchecked")
    public final Map getItemMapByName(String name, String bucket)
            throws EchoNestException {
        return getItemMap(name, bucket, "name");
    }

    @SuppressWarnings("unchecked")
    public final Map getItemMap(String id, String bucket, String idType)
            throws EchoNestException {
        Params p = new Params();
        p.add(idType, id);

        if (bucket != null) {
            p.add("bucket", bucket);
        }

        Map results = en.getCmd().sendCommand(type + "/profile", p);
        Map response = (Map) results.get("response");
        MQuery mq = new MQuery(response);
        return (Map) mq.getObject(path);
    }

    @SuppressWarnings("unchecked")
    public Map getItemMap(String id, List<String> buckets)
            throws EchoNestException {
        Params p = new Params();
        p.add("id", id);

        if (buckets != null) {
            p.add("bucket", buckets);
        }

        Map results = en.getCmd().sendCommand(type + "/profile", p);
        Map response = (Map) results.get("response");
        MQuery mq = new MQuery(response);
        return (Map) mq.getObject(path);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ENItem other = (ENItem) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }

    public String getOriginalID() {
        return originalID;
    }
}
