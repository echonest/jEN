package com.echonest.api.v4;

import java.util.List;
import java.util.Map;

/**
 * Represents years active data for an artist. Some artists have more than one
 * active range.
 *
 * @author plamere
 *
 */
public class YearsActive {

    @SuppressWarnings("unchecked")
    private List yearsActive = null;
    private final static Long[] EMPTY = null;

    @SuppressWarnings("unchecked")
    YearsActive(List ya) {
        yearsActive = ya;
    }

    /**
     * Returns the number of ranges for an artist. If we don't know anything
     * about the years active for the artist, the size will be zero
     *
     * @return
     */
    public int size() {
        return yearsActive.size();
    }

    /**
     * Returns the n'th active range for the artist
     *
     * @param which the range of interest. 0 <= which < size()
     * @return an array containing the beginning and end year for the range,if
     * an artist is still active, the end range will be null.
     */
    @SuppressWarnings("unchecked")
    public Long[] getRange(int which) {
        Map map = (Map) yearsActive.get(which);
        Long[] result = {(Long) map.get("start"), (Long) map.get("end")};
        return result;
    }

    /**
     * Returns the full range for the artist (earliest start year, and latest
     * end year)
     *
     * @return the full range of the artist. If the artist is still active, the
     * end range will be null.
     */
    public Long[] getRange() {
        if (size() == 0) {
            return EMPTY;
        } else if (size() == 1) {
            return getRange(0);
        } else {
            Long[] start = getRange(0);
            Long[] end = getRange(size() - 1);
            Long[] result = {start[0], end[1]};
            return result;
        }
    }

    public void dump() {
        for (int i = 0; i < size(); i++) {
            Long[] range = getRange(i);
            System.out.printf("   %d-%d\n", range[0], range[1]);
        }
    }
}
