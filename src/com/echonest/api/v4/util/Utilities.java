/*
 * Utilities.java
 *
 * Created on February 13, 2007, 8:18 AM
 *
 * (c) 2009  The Echo Nest
 * See "license.txt" for terms
 *
 */
package com.echonest.api.v4.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

/**
 *
 * @author plamere
 */
public class Utilities {

    /** Creates a new instance of Utilities */
    private Utilities() {
    }
    /**
     * Implements normalization rules described at
     * http://www.ee.columbia.edu/~dpwe/research/musicsim/normalization.html
     *
     * Artist names are particularly important to get normalized
     * to the same forms.
     * Hence, they have severe normalization:
     *
     * 1. Names are all mapped to lower case
     * 2. Delete apostrophes ("'") and periods (".").
     * 3. Everything else except a-z 0-9 maps to "_".
     *   - but this doesn't work for non-english titles
     * 3A Multiple _'s in sequence fold into a single _.
     * 3B Leading and trailing _'s are dropped.
     * 4. Don't reorder proper names - it's just too hard,
     *    and there's no clear boundary between proper names and band names. .
     * 5. Always drop a leading "the".
     * 5A Always drop a leading indefinite article too
     *
     *
     * Augmented these rules with:
     *   ampersands (&) are replaced with 'and'
     *
     * Issues:
     *    Number folding - '3' vs. 'three'
     *    Non english names
     */
    static Pattern deletedChars = Pattern.compile("[\"'.]");
    static Pattern ampersand = Pattern.compile("&");
    static Pattern everythingBut = Pattern.compile("[^\\p{Alnum}]");
    static Pattern leadingDash = Pattern.compile("^_+");
    static Pattern trailingDash = Pattern.compile("_+$");
    static Pattern leadingThe = Pattern.compile("^the\\s");
    static Pattern trailingThe = Pattern.compile("\\sthe$");
    static Pattern leadingA = Pattern.compile("^a\\s");
    static Pattern trailingA = Pattern.compile("\\sa$");
    static Pattern multiDash = Pattern.compile("_{2,}");
    static Pattern unprintable = Pattern.compile("[^\\p{Print}]");
    static Pattern punctuation = Pattern.compile("[\\p{Punct}]");
    
    static Random rng = new Random();


    public static String printable(String in) {
        return unprintable.matcher(in).replaceAll("");
    }
    /*
     * 1. Names are all mapped to lower case
     * 2. Delete apostrophes ("'") and periods (".").
     *     2+ PBL added '"'
     * 3. Everything else except a-z 0-9 maps to "_".
     *   - but this doesn't work for non-english titles
     *   - switch this to mapping them to ""
     * 3A Multiple _'s in sequence fold into a single _.
     * 3B Leading and trailing _'s are dropped.
     * 4. Don't reorder proper names - it's just too hard,
     *    and there's no clear boundary between proper names and band names. .
     * 5. Always drop a leading "the".
     * 5A Always drop a leading indefinite article too
     */

    public static String normalize(String in) {
        String s;
        if (in == null) {
            return "";
        }

        s = in.trim();
        s = s.toLowerCase();
        s = removeAccents(s);
        s = deletedChars.matcher(s).replaceAll("");
        s = ampersand.matcher(s).replaceAll(" and ");
        s = leadingDash.matcher(s).replaceAll("");
        s = trailingDash.matcher(s).replaceAll("");
        s = leadingThe.matcher(s).replaceAll("");
        s = trailingThe.matcher(s).replaceAll("");
        s = leadingA.matcher(s).replaceAll("");
        s = trailingA.matcher(s).replaceAll("");
        s = multiDash.matcher(s).replaceAll("_");
        s = everythingBut.matcher(s).replaceAll("");

        // if we've reduced the input down to nothing
        // fall back on input (necessary for non western
        // names

        if (s.length() == 0) {
            s = in;
        }

        //System.out.println(in + " BECOMES " + s);
        return s;
    }

    public static boolean nameEquals(String s1, String s2) {
        if (s1.equalsIgnoreCase(s2)) {
            return true;
        } else {
            return normalize(s1).equals(normalize(s2));
        }
    }

    //static Pattern specialChars = Pattern.compile("[ -/:-@\\[-`]");
    //static Pattern specialChars = Pattern.compile("[<>/\\!#\\$]");
    static Pattern specialChars = Pattern.compile("[\\&,\\[\\]@\\-\\(\\)<>/\\!#\\$]");
    static Pattern the = Pattern.compile("\\s+the\\s+");
    static Pattern indefiniteArticle = Pattern.compile("\\s+a\\s+");
    static Pattern andPattern = Pattern.compile("\\s+and\\s+");
    static Pattern orPattern = Pattern.compile("\\s+or\\s+");
    static Pattern disks1 = Pattern.compile("dis[ck][\\s+][123456789]");
    static Pattern disks2 = Pattern.compile("dis[ck][\\s+]one|two|three|four|five");
    static Pattern disks3 = Pattern.compile("[cC][dD]\\s*[1234567]");
    static Pattern leadingThe1 = Pattern.compile("^the\\s+");
    static Pattern trailingThe1 = Pattern.compile("\\s+the$");
    static Pattern leadingAnd = Pattern.compile("^and\\s+$");
    static Pattern trailingAnd = Pattern.compile("\\s+and$$");
    static Pattern parens = Pattern.compile("\\(.*\\)");

    public static String normalizeForSearch(String in) {
        String s;
        if (in == null) {
            return "";
        }

        s = in.trim();
        s = s.toLowerCase();
        s = parens.matcher(s).replaceAll("");
        s = specialChars.matcher(s).replaceAll(" ");
        s = the.matcher(s).replaceAll(" ");
        s = indefiniteArticle.matcher(s).replaceAll(" ");
        s = andPattern.matcher(s).replaceAll(" ");
        s = orPattern.matcher(s).replaceAll(" \"or\" ");
        s = disks1.matcher(s).replaceAll(" ");
        s = disks2.matcher(s).replaceAll(" ");
        s = disks3.matcher(s).replaceAll(" ");
        s = leadingThe1.matcher(s).replaceAll(" ");
        s = trailingThe1.matcher(s).replaceAll(" ");
        s = leadingAnd.matcher(s).replaceAll(" ");
        s = trailingAnd.matcher(s).replaceAll(" ");
        return s;
    }

    /**
     * returns a quoted version of s with all internal quotes escaped
     */
    static String quote(String s) {
        String escaped = s.replaceAll("\\\"", "\\\\\"");
        return "\"" + escaped + "\"";
    }
    private static Map<String, String> genreMap;

    public static String collapseGenre(String genre) {
        if (genreMap == null) {
            Map<String, String> gMap = new HashMap<String, String>();
            gMap.put("acid", "rock");
            gMap.put("alternative", "rock");
            gMap.put("alternative_and_punk", "rock");
            gMap.put("alternrock", "rock");
            gMap.put("ambient", "ambient");
            gMap.put("baseball", "other");
            gMap.put("blues", "blues");
            gMap.put("blues_rock", "rock");
            gMap.put("brit_pop", "pop");
            gMap.put("celtic", "world");
            gMap.put("classical", "classical");
            gMap.put("classic_rock", "rock");
            gMap.put("country", "country");
            gMap.put("dance", "electronica");
            gMap.put("disco", "pop");
            gMap.put("easy_listening", "pop");
            gMap.put("electronic", "electronica");
            gMap.put("electronica_and_dance", "electronica");
            gMap.put("ethnic", "world");
            gMap.put("folk", "folk");
            gMap.put("folklore", "folk");
            gMap.put("folk_rock", "folk");
            gMap.put("general_blues", "blues");
            gMap.put("general_pop", "pop");
            gMap.put("general_unclassifiable", "other");
            gMap.put("grunge", "rock");
            gMap.put("hard_rock", "rock");
            gMap.put("hip_hop", "rap");
            gMap.put("humor", "other");
            gMap.put("industrial", "electronica");
            gMap.put("instrumental", "rock");
            gMap.put("jazz", "jazz");
            gMap.put("jazz_instrument", "jazz");
            gMap.put("jazz_west_coast", "jazz");
            gMap.put("latin", "world");
            gMap.put("live_rock", "rock");
            gMap.put("mash_up", "rock");
            gMap.put("metal", "rock");
            gMap.put("musical", "pop");
            gMap.put("newage", "world");
            gMap.put("new_age", "world");
            gMap.put("newfie", "folk");
            gMap.put("no_genre", "other");
            gMap.put("norwegian_folk", "folk");
            gMap.put("oldies", "pop");
            gMap.put("other", "other");
            gMap.put("pop", "pop");
            gMap.put("progressive_rock", "rock");
            gMap.put("punk", "rock");
            gMap.put("punk_rock", "rock");
            gMap.put("r_and_b", "rap");
            gMap.put("rap", "rap");
            gMap.put("reggae", "other");
            gMap.put("retro", "rock");
            gMap.put("rock", "rock");
            gMap.put("rock_pop", "rock");
            gMap.put("rock_and_roll", "rock");
            gMap.put("slow_rock", "rock");
            gMap.put("soft_rock", "rock");
            gMap.put("soundtrack", "other");
            gMap.put("techno", "electronica");
            gMap.put("trance", "electronica");
            gMap.put("trip_hop", "rap");
            gMap.put("unclassifiable", "other");
            gMap.put("vocal", "pop");
            gMap.put("unknown", "other");
            genreMap = gMap;
        }
        String normalizedGenre = normalize(genre);
        String mappedGenre = genreMap.get(normalizedGenre);
        if (mappedGenre == null) {
            if (normalizedGenre.contains("rock")) {
                return "rock";
            }
            return genre;
        } else {
            return mappedGenre;
        }
    }

    public static String normalizeFilename(File file)
            throws MalformedURLException {
        return file.toURI().toURL().getFile();
    }

    public static long binaryCopy(URL src, File dest) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        int byteCount = 0;
        if (!dest.exists()) {
            if (src == null) {
                if (!dest.createNewFile()) {
                    throw new IOException("Can't create " + dest);
                }
            } else {
                try {
                    os = new BufferedOutputStream(new FileOutputStream(dest));
                    URLConnection urc = src.openConnection();
//                    urc.setReadTimeout(10000);
                    urc.setRequestProperty("User-Agent", "Mozilla/4.0");
                    is = new BufferedInputStream(urc.getInputStream());
                    int b;

                    while ((b = is.read()) != -1) {
                        os.write(b);
                        byteCount++;
                    }

                } finally {
                    if (is != null) {
                        is.close();
                        is = null;
                    }
                    if (os != null) {
                        os.close();
                        os = null;
                    }
                }
            }
        }
        return byteCount;
    }

    public static long binaryCopyWget(URL src, File dest) throws IOException {
        String wgetPath = System.getProperty("wget");
        if (wgetPath == null) {
            wgetPath = "wget";
        }
        String cmd = wgetPath + " -qU Squeezebox " + src + " -O " + dest;
        Process process = Runtime.getRuntime().exec(cmd);
        try {
            int status = process.waitFor();
            if (status != 0) {
                throw new IOException("binary copy return non zero status " + status);
            }
        } catch (InterruptedException ioe) {
        }
        return dest.length();
    }

    public static void log(String s) {
        System.out.println("   " + s);
    }

    public static void err(String s) {
        System.out.println(" ERR  " + s);
    }

    public static String jam(String[] args, int start) {
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < args.length; i++) {
            sb.append(args[i]);
            sb.append(" ");
        }
        return sb.toString().trim();
    }

    public static String detag(String s) {
        return s.replaceAll("\\<.*?\\>", "");
    }

    public static String md5(String s) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte[] md5sum = digest.digest();
            BigInteger bigInt = new BigInteger(1, md5sum);
            String output = bigInt.toString(16);
            // the MD5 needs to be 32 characters long.
            return prepad(output, 32, '0');
        } catch (NoSuchAlgorithmException e) {
            System.err.println("No MD5 algorithm. we are sunk.");
            return s;
        } 
    }

    public static String md5(File f) throws IOException {
        byte[] buffer = new byte[8192];
        int read = 0;
        InputStream is = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            is = new BufferedInputStream(new FileInputStream(f));
            while ((read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
            byte[] md5sum = digest.digest();
            BigInteger bigInt = new BigInteger(1, md5sum);
            String output = bigInt.toString(16);
            // the MD5 needs to be 32 characters long.
            return prepad(output, 32, '0');
        } catch (NoSuchAlgorithmException e) {
            throw new IOException("Can't find md5 algorithm");
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private static String prepad(String s, int len, char c) {
        // slow but used so rarely, who cares.
        while (s.length() < len) {
            s = c + s;
        }
        return s;
    }


    public static String removeAccents(String text) {
        return Normalizer.normalize(text, Normalizer.Form.NFD)
                     .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    public static void testAccent(String s) {
        System.out.printf(" %s becomes %s\n", s, removeAccents(s));
    }

    public static File createNewAudioFile(String type) throws IOException {
        String seed = "music/dizzy.mp3";
        int totalLength = 120;

        int start = rng.nextInt(totalLength / 2);
        int length = rng.nextInt(totalLength / 2) + 30;
        File output = new File(String.format("music/dizzy.%d.%d.%s", start, length, type));
        String cmd = String.format("/usr/local/bin/sox %s %s trim %d %d", seed,
                output.getAbsoluteFile(), start, length);
        System.out.println(cmd);
        Process process = Runtime.getRuntime().exec(cmd);
        try {
            if (process.waitFor() == 0) {
                return output;
            }
        } catch (InterruptedException e) {
        }
        return null;
    }
    

    public static void main(String[] args) throws Exception {
        System.out.println("md5 " + md5("http://www.theclerisy.net/glws/Weezer%20-%20Mansion%20of%20Cardboard.mp3"));
        //testAccent("Sigur Rós");
    }

}
