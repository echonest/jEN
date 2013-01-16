/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.echonest.api.v4.util;

/**
 *
 * @author plamere
 */
public class PerformanceStats {

    private int calls;
    private long minCallTime;
    private long maxCallTime;
    private long totalCallTime;
    private int failures;

    public int getCalls() {
        return calls;
    }

    public void setCalls(int calls) {
        this.calls = calls;
    }

    public int getFailures() {
        return failures;
    }

    public void setFailures(int failures) {
        this.failures = failures;
    }

    public long getMaxCallTime() {
        return maxCallTime;
    }

    public void setMaxCallTime(long maxCallTime) {
        this.maxCallTime = maxCallTime;
    }

    public long getMinCallTime() {
        return minCallTime;
    }

    public void setMinCallTime(long minCallTime) {
        this.minCallTime = minCallTime;
    }

    public long getTotalCallTime() {
        return totalCallTime;
    }

    public void setTotalCallTime(long totalCallTime) {
        this.totalCallTime = totalCallTime;
    }

    public long getAvgCallTime() {
        if (calls > 0) {
            return totalCallTime / calls;
        } else {
            return 0;
        }
    }
}
