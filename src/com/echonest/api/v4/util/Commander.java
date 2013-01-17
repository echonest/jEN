package com.echonest.api.v4.util;

import com.echonest.api.v4.EchoNestException;
import com.echonest.api.v4.Params;
import com.echonest.api.v4.util.StatsManager.Tracker;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Commander {

    private String name;
    private String defaultHost = "developer.echonest.com";
    private String host;
    private String prefix = "/api/v4/";
    private boolean traceSends = false;
    private boolean traceRecvs = false;
    private boolean traceErrs = false;
    private int minCommandTime = 0;
    private int timeout = 30000;
    private int tryCount = 1;
    private long lastCommandTime;
    private Params standardParams = new Params();
    private StatsManager statsManager = new StatsManager();
    private long startTime;
    private int cmdCount = 0;
    private String expectedVersion = "4.";
    private JSONParser parser = new JSONParser();
    private boolean autoThrottle = true;
    private int autoThrottleTime = 0;

    public Commander(String name) {
        this.name = name;
        startTime = System.currentTimeMillis();
        host = System.getenv("ECHO_NEST_API_HOST");
        if (host == null) {
            host = defaultHost;
        }
        if (System.getenv("ECHO_NEST_API_TRACE_SENDS") != null) {
            traceSends = true;
        }
        if (System.getenv("ECHO_NEST_API_TRACE_RECVS") != null) {
            traceRecvs = true;
        }
    }

    @SuppressWarnings("unchecked")
    public Map sendCommand(String command, Params params)
            throws EchoNestException {
        return sendCommand(command, params, false);
    }

    @SuppressWarnings("unchecked")
    public Map sendCommand(String command, Params params, boolean usePost)
            throws EchoNestException {
        return sendCommand(command, params, usePost, null);
    }

    @SuppressWarnings("unchecked")
    public Map sendCommand(String command, Params params, boolean usePost,
            File file) throws EchoNestException {

        long curGap = System.currentTimeMillis() - lastCommandTime;
        long delayTime = minCommandTime - curGap;

        delay(delayTime);

        Tracker tracker = statsManager.start(command);

        try {
            StringBuilder url = new StringBuilder();
            url.append("http://");
            url.append(host);
            url.append(prefix);
            url.append(command);
            url.append(params.toString(true));
            url.append(standardParams.toString(params.size() == 0));

            Map results = getCheckedResults(url.toString(), usePost, file);
            statsManager.end(tracker);
            return results;
        } finally {
            statsManager.close(tracker);
        }
    }

    public String buildFeedUrl(String command) {
        StringBuilder url = new StringBuilder();
        url.append("http://");
        url.append(host);
        url.append(command);
        return url.toString();
    }

    @SuppressWarnings("unchecked")
    static public Map fetchURLAsJSON(String urls) throws IOException {
        InputStream is = null;

        try {
            JSONParser parser = new JSONParser();

            URI uri = new URI(urls);
            URL url = uri.toURL();

            URLConnection urc = url.openConnection();
            is = new BufferedInputStream(urc.getInputStream());
            BufferedReader in = new BufferedReader(new InputStreamReader(is,
                    Charset.forName("UTF-8")));

            return (JSONObject) parser.parse(in);
        } catch (ParseException e) {
            throw new IOException("Parse Exception", e);
        } catch (URISyntaxException e) {
            throw new IOException("Bad URI " + urls);
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public void showStats() {
        statsManager.dump();
    }

    @SuppressWarnings("unchecked")
    private Map getCheckedResults(String command, boolean usePost, File file)
            throws EchoNestException {
        boolean ok = false;
        Map results = null;
        try {
            results = getMapResults(command, usePost, file);
            checkStatus(results);
            ok = true;
            return results;
        } catch (IOException ioe) {
            throw new EchoNestException(ioe);
        } finally {
            if (!ok && traceErrs && !traceSends) {
                System.out.println("send-err-> " + command);
                if (results != null && !traceRecvs) {
                    System.out.println("recv-err-> " + results.toString());
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private Map getMapResults(String command, boolean usePost, File file)
            throws IOException {
        String results = getStringResults(command, usePost, file);
        try {
            return (JSONObject) parser.parse(results);
        } catch (ParseException e) {
            throw new IOException("Parse Exception", e);
        }
    }

    public String getStringResults(String command, boolean usePost, File file)
            throws IOException {

        InputStream is = null;
        if (usePost && file != null) {
            is = post(command, file);
        } else {
            is = sendCommandRaw(command, usePost, file);
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(is,
                Charset.forName("UTF-8")));

        String line = null;
        StringBuilder results = new StringBuilder();

        while ((line = in.readLine()) != null) {
            results.append(line);
        }

        if (traceRecvs) {
            System.out.println("received-->     " + results.toString());
        }
        in.close();
        return results.toString();
    }
    long last_time = 0;

    private InputStream post(String command, File file) throws IOException {
        HttpURLConnection conn = null;
        DataOutputStream dos = null;

        URL url = null;
        try {
            URI uri = new URI(command);
            url = uri.toURL();
        } catch (URISyntaxException e) {
            throw new IOException("Bad URL " + e);
        } catch (MalformedURLException e) {
            throw new IOException("Bad URL " + e);
        }

        if (traceSends) {
            System.out.println("Sending-->     " + url);
        }

        conn = (HttpURLConnection) url.openConnection();
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setFixedLengthStreamingMode((int) file.length());
        conn.setAllowUserInteraction(false);
        conn.setUseCaches(false);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/octet-stream");

        if (getTimeout() != -1) {
            conn.setReadTimeout(getTimeout());
            conn.setConnectTimeout(getTimeout());
        }


        dos = new DataOutputStream(conn.getOutputStream());

        InputStream fis = new FileInputStream(file);
        int r = 0;
        byte[] data = new byte[1024];
        while ((r = fis.read(data, 0, data.length)) != -1) {
            dos.write(data, 0, r);
        }
        fis.close();

        dos.flush();
        dos.close();

        int code = conn.getResponseCode();

        InputStream is = null;
        if (code >= 300) {
            is = conn.getErrorStream();
        } else {
            is = conn.getInputStream();
        }
        return is;
    }

    public InputStream sendCommandRaw(String command, boolean usePost, File file)
            throws IOException {
        try {
            String fullCommand = command;

            URI uri = new URI(fullCommand);
            URL url = uri.toURL();

            cmdCount++;
            if (traceSends) {
                long now = System.currentTimeMillis();
                long delta = now - last_time;
                last_time = now;
                System.out.printf("Sending %d %d %d-->     %s\n", cmdCount, now
                        - startTime, delta, url);
            }

            InputStream is = null;
            for (int i = 0; i < tryCount; i++) {
                try {
                    HttpURLConnection urc = (HttpURLConnection) url.openConnection();


                    if (usePost) {
                        if (urc instanceof HttpURLConnection) {
                            ((HttpURLConnection) urc).setRequestMethod("POST");
                            ((HttpURLConnection) urc).setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                            ((HttpURLConnection) urc).setRequestProperty("Content-Length", "0");
                        }
                    }

                    if (getTimeout() != -1) {
                        urc.setReadTimeout(getTimeout());
                        urc.setConnectTimeout(getTimeout());
                    }

                    if (false) {
                        System.out.println();
                        for (String key : urc.getHeaderFields().keySet()) {
                            String val = urc.getHeaderField(key);
                            System.out.println("response '" + key + "' " + val);
                        }
                        System.out.println();
                    }

                    autoThrottleTime = throttle(urc);
                    int code = urc.getResponseCode();

                    is = null;
                    if (code >= 300) {
                        is = urc.getErrorStream();
                    } else {
                        is = urc.getInputStream();
                    }
                    is = new BufferedInputStream(is);
                    break;
                } catch (FileNotFoundException e) {
                    throw e;
                } catch (IOException e) {
                    System.out.println(name + " Error: " + e + " cmd: "
                            + command);
                }
            }

            lastCommandTime = System.currentTimeMillis();
            if (is == null) {
                System.out.println(name + " retry failure  cmd: " + url);
                throw new IOException("Can't send command");
            }
            return is;
        } catch (URISyntaxException ex) {
            throw new IOException("bad uri " + ex);
        }
    }

    private int getMillisecondsRemaining(String date) {
        // Date Tue, 08 Jan 2013 18:47:38 GMT
        String time = date.split(" ")[4];
        int secs = Integer.parseInt(time.split(":")[2]);
        return 60000 - (secs * 1000);
    }

    private int throttle(HttpURLConnection urc) {
        int throttle = 0;

        if (autoThrottle) {
            try {

                int remainingCalls = Integer.parseInt(urc.getHeaderField("X-RateLimit-Remaining"));
                int remainingTime = getMillisecondsRemaining(urc.getHeaderField("Date"));
                if (remainingCalls > 0) {
                    throttle = remainingTime / remainingCalls;
                } else {
                    throttle = remainingTime;
                }
            } catch (NumberFormatException e) {
                throttle = minCommandTime;
            }
            // System.out.printf("Autothrottle rt:%d rc:%d throttle:%d\n", remainingTime, remainingCalls, throttle);
        }
        return throttle;
    }

    @SuppressWarnings("unchecked")
    public Map post(String command, Map<String, Object> params)
            throws EchoNestException {
        try {
            String NEWLINE = "\r\n";
            String PREFIX = "--";
            String BOUNDARY = "-----------"
                    + Long.toString(System.currentTimeMillis(), 16);

            HttpURLConnection conn = null;

            StringBuilder urlSB = new StringBuilder();
            urlSB.append("http://");
            urlSB.append(host);
            urlSB.append(prefix);
            urlSB.append(command);

            String fullCommand = urlSB.toString();
            URL url = null;

            try {
                URI uri = new URI(fullCommand);
                url = uri.toURL();
            } catch (URISyntaxException e) {
                throw new EchoNestException(
                        EchoNestException.CLIENT_SERVER_INCONSISTENCY,
                        "Bad URL " + e);
            } catch (MalformedURLException e) {
                throw new EchoNestException(
                        EchoNestException.CLIENT_SERVER_INCONSISTENCY,
                        "Bad URL " + e);
            }

            if (traceSends) {
                System.out.println("Sending-->     " + url);
            }

            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setAllowUserInteraction(false);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept", "*/*");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Cache-Control", "no-cache");
            conn.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + BOUNDARY);
            DataOutputStream dostream = new DataOutputStream(conn.getOutputStream());
            //DataOutputStream dostream = new DataOutputStream(System.out);
            OutputStreamWriter out = new OutputStreamWriter(dostream, "utf-8");

            Map<String, Object> p = new HashMap<String, Object>(params);

            for (Entry<String, Object> kv : standardParams.getMap().entrySet()) {
                p.put(kv.getKey(), kv.getValue());
            }


            if (false) {
                for (String s : p.keySet()) {
                    System.out.printf("   %s=%s\n", s, p.get(s));
                }
            }
            for (String key : p.keySet()) {
                out.write(PREFIX);
                out.write(BOUNDARY);
                out.write(NEWLINE);
                Object val = p.get(key);

                if (val instanceof File) {

                    File file = (File) val;

                    out
                            .write("Content-Disposition: form-data; name=\"file\";"
                            + " filename=\"" + file.getName() + "\"");
                    out.write(NEWLINE);
                    out.write("Content-Type: application/octet-stream");
                    out.write(NEWLINE);
                    out.write(NEWLINE);
                    out.flush();

                    InputStream is = new FileInputStream(file);
                    int r = 0;
                    byte[] data = new byte[1024];
                    while ((r = is.read(data, 0, data.length)) != -1) {
                        dostream.write(data, 0, r);
                    }
                    is.close();
                    out.write(NEWLINE);
                } else {
                    out.write("Content-Disposition: form-data; name=\""
                            + key + "\"");
                    out.write(NEWLINE);
                    out.write(NEWLINE);
                    out.write(val.toString());
                    //dos.writeUTF(val.toString());
                    out.write(NEWLINE);
                }
            }
            out.write(PREFIX);
            out.write(BOUNDARY);
            out.write(PREFIX);
            out.write(NEWLINE);
            // close streams
            out.flush();
            out.close();

            int code = conn.getResponseCode();

            InputStream is = null;
            if (code >= 300) {
                is = conn.getErrorStream();
            } else {
                is = conn.getInputStream();
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(is,
                    Charset.forName("UTF-8")));

            String line = null;
            StringBuilder results = new StringBuilder();

            while ((line = in.readLine()) != null) {
                results.append(line);
            }

            if (traceRecvs) {
                System.out.println("received-->     " + results.toString());
            }
            in.close();
            String resultString = results.toString();

            try {
                JSONObject jobj = (JSONObject) parser.parse(resultString);
                checkStatus(jobj);
                return (Map) jobj;
            } catch (ParseException e) {
                throw new IOException("Parse Exception", e);
            }
        } catch (IOException ioe) {
            throw new EchoNestException(ioe);
        }
    }

    private void cmdDelay() {
        long curGap = System.currentTimeMillis() - lastCommandTime;
        long delayTime;

        if (autoThrottle) {
            delayTime = autoThrottleTime - curGap;
        } else {
            delayTime = minCommandTime - curGap;
        }
        delay(delayTime);

    }

    private void delay(long time) {

        if (time > 0) {
            try {
                Thread.sleep(time);
            } catch (InterruptedException ie) {
            }
        }
    }

    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * @param host the host to set
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * @return the prefix
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * @param prefix the prefix to set
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * @return the traceSends
     */
    public boolean isTraceSends() {
        return traceSends;
    }

    /**
     * @param traceSends the traceSends to set
     */
    public void setTraceSends(boolean traceSends) {
        this.traceSends = traceSends;
    }

    /**
     * @return the trackRecvs
     */
    public boolean isTraceRecvs() {
        return traceRecvs;
    }

    /**
     * @param trackRecvs the trackRecvs to set
     */
    public void setTraceRecvs(boolean trackRecvs) {
        this.traceRecvs = trackRecvs;
    }

    /**
     * @return the minCommandTime
     */
    public int getMinCommandTime() {
        return minCommandTime;
    }

    /**
     * @param minCommandTime the minCommandTime to set
     */
    public void setMinCommandTime(int minCommandTime) {
        if (minCommandTime < 0) {
            autoThrottle = true;
        } else {
            this.minCommandTime = minCommandTime;
            autoThrottle = false;
        }
    }

    /**
     * @return the timeout
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * @param timeout the timeout to set
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * @return the tryCount
     */
    public int getTryCount() {
        return tryCount;
    }

    /**
     * @param tryCount the tryCount to set
     */
    public void setTryCount(int tryCount) {
        this.tryCount = tryCount;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the standardParams
     */
    public Params getStandardParams() {
        return standardParams;
    }

    /**
     * @param standardParams the standardParams to set
     */
    public void setStandardParams(Params standardParams) {
        this.standardParams = standardParams;
    }

    @SuppressWarnings("unchecked")
    private void checkStatus(Map results) throws EchoNestException {
        Map response = (Map) results.get("response");
        Map status = (Map) response.get("status");
        String version = (String) status.get("version");
        String message = (String) status.get("message");
        Long scode = (Long) status.get("code");

        int code = scode.intValue();

        if (!version.startsWith(expectedVersion)) {
            throw new EchoNestException(
                    EchoNestException.CLIENT_SERVER_INCONSISTENCY,
                    "Unexpected API version number");
        }

        if (code != 0) {
            throw new EchoNestException(code, message);
        }
    }

    @SuppressWarnings("unchecked")
    void test() throws IOException, EchoNestException {
        String command = "http://developer3.sandpit.us/api/v4/artist/get_similar?&api_key=XZTXVRO3VC3FBXS8C&id=ARH6W4X1187B99274F&format=json&rows=10&start=0&bucket=hotttnesss";
        String results = getStringResults(command, false, null);
        System.out.println(results);

        Map jo = getCheckedResults(command, false, null);
        System.out.println(jo);

        String errcmd = "http://developer3.sandpit.us/api/v4/artist/get_similar?&api_key=AZTXVRO3VC3FBXS8C&id=ARH6W4X1187B99274F&format=json&rows=10&start=0&bucket=hotttnesss";
        Map jo2 = getCheckedResults(errcmd, false, null);
        System.out.println(jo2);
    }

    @SuppressWarnings("unchecked")
    void test2() throws EchoNestException {
        Params params = new Params();
        params.add("rows", 10);
        params.add("bucket", "hotttnesss");
        params.add("bucket", "familiarity");
        params.add("id", "ARH6W4X1187B99274F");

        Map results = sendCommand("artist/get_similar", params, false);
        System.out.println(results);
    }

    @SuppressWarnings("unchecked")
    void test3() throws EchoNestException {
        Params params = new Params();
        params.add("rows", 10);
        params.add("bucket", "hotttnesss");
        params.add("bucket", "familiarity");
        params.add("bucket", "crap");

        params.add("id", "ARH6W4X1187B99274F");
        params.add("id", "ARH1N081187B9AC562");

        Map results = sendCommand("artist/get_similar", params, false);
        System.out.println(results);
    }

    public static void main(String[] args) throws Exception {
        Commander cmd = new Commander("test");
        Params stdParams = new Params();
        stdParams.add("api_key", "FILDTEOIK2HBORODV");
        cmd.setStandardParams(stdParams);
        cmd.test2();
        cmd.test3();
    }
}
