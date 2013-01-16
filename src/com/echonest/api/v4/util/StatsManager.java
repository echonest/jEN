/*
 * (c) 2009  The Echo Nest
 * See "license.txt" for terms
 */
package com.echonest.api.v4.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *
 * @author Paul
 */
public class StatsManager {

    public static class Tracker {

        String name;
        long startTime;
        long endTime = 0L;

        Tracker(String name, long start) {
            this.name = name;
            this.startTime = start;
        }
    }
    private Map<String, OpData> map = new HashMap<String, OpData>();

    public Tracker start(String name) {
        return new Tracker(name, System.currentTimeMillis());
    }

    public void end(Tracker tracker) {
        tracker.endTime = System.currentTimeMillis();
        long delta = tracker.endTime - tracker.startTime;
        OpData opData = get(tracker.name);
        opData.count++;
        opData.sumTime += delta;

        if (delta > opData.maxTime) {
            opData.maxTime = delta;
        }
        if (delta < opData.minTime) {
            opData.minTime = delta;
        }
    }

    public void close(Tracker tracker) {
        if (tracker.endTime == 0L) {
            OpData opData = get(tracker.name);
            opData.count++;
            opData.error++;
        }
    }

    private OpData get(String op) {
        OpData opData = map.get(op);
        if (opData == null) {
            opData = new OpData(op);
            map.put(op, opData);
        }
        return opData;
    }

    public PerformanceStats getOverallPerformanceStats() {
        int total = 0;
        int errs = 0;
        int sum = 0;
        long min = Long.MAX_VALUE;
        long max = Long.MIN_VALUE;

        PerformanceStats ps = new PerformanceStats();
        List<OpData> opList = new ArrayList<OpData>(map.values());
        for (OpData opData : opList) {
            total += opData.count;
            errs += opData.error;
            sum += opData.sumTime;

            if (opData.minTime < min) {
                min = opData.minTime;
            }
            if (opData.maxTime > max) {
                max = opData.maxTime;
            }
        }
        ps.setCalls(total);
        ps.setFailures(errs);
        ps.setTotalCallTime(sum);
        ps.setMinCallTime(min);
        ps.setMinCallTime(max);
        return ps;
    }

    public void dump() {
        System.out.printf("||%5s|| %4s|| %6s || %6s || %6s || %s ||\n",
                "Calls", "Fail", "Avg", "Min", "Max", "Method");
        int total = 0;
        int errs = 0;
        int sum = 0;

        List<OpData> opList = new ArrayList<OpData>(map.values());
        Collections.sort(opList);
        for (OpData opData : opList) {
            System.out.println(opData);
            total += opData.count;
            errs += opData.error;
            sum += opData.sumTime;
        }

        int successCount = total - errs;
        System.out.println("");
        System.out.printf(" Total calls : %d \n", total);
        System.out.printf(" Total errors: %d \n", errs);
        if (total > 0) {
            System.out.printf(" Success Rate: %d %%\n", 100 * (total - errs) / total);
        }

        if (successCount > 0) {
            System.out.printf(" Average Time: %d ms\n", sum / successCount);
        }
        System.out.println("");
    }
}

class OpData implements Comparable<OpData> {

    String name;
    int count = 0;
    int error = 0;
    long minTime = Long.MAX_VALUE;
    long maxTime = -Long.MAX_VALUE;
    long sumTime = 0;

    OpData(String name) {
        this.name = name;
    }

    public long getMaxTime() {
        return maxTime;
    }

    public long getMinTime() {
        return minTime;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    public int getError() {
        return error;
    }

    public long getSumTime() {
        return sumTime;
    }

    public long getAvgTime() {
        int successCount = count - error;
        if (successCount > 0) {
            return sumTime / successCount;
        } else {
            return 0;
        }
    }

    public String toString() {
        return String.format("|| %3d || %3d || %6d || %6d || %6d || %s ||",
                getCount(), getError(), getAvgTime(), getMinTime(), getMaxTime(), getName());

    }

    public int compareTo(OpData other) {
        return name.compareTo(other.name);
    }
}
