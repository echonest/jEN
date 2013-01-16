package com.echonest.api.v4;

import java.util.Map;

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
     * Updates the catalog
     * 
     * @param update
     *            the catalog updater
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
     * @param ticketn
     *            the ticket of interest
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
     * @param ticket
     *            the ticket of interest
     * @param timeoutMillis
     *            the maximum milliseconds to wait for updates to be done
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
