package com.echonest.api.v4;

import java.util.List;
import java.util.Map;

import com.echonest.api.v4.util.MQuery;

public class Segment extends TimedEvent {
	private double loudnessStart;
	private double loudnessMaxTime;
    private double loudnessMax;
    private double[] pitches;
    private double[] timbre;
	
	@SuppressWarnings("unchecked")
	Segment(Map map) {
		super(map);
		
		MQuery mq = new MQuery(map);
		loudnessStart = mq.getDouble("loudness_start");
		loudnessMaxTime = mq.getDouble("loudness_max_time");
		loudnessMax = mq.getDouble("loudness_max");
		List lpitches = (List) mq.getObject("pitches");
		
		pitches = new double[lpitches.size()];
		for (int i = 0; i < lpitches.size(); i++) {
			Double p = (Double) lpitches.get(i);
			pitches[i] = p;
		}
		
		List ltimbre = (List) mq.getObject("timbre");
		timbre = new double[ltimbre.size()];
		for (int i = 0; i < ltimbre.size(); i++) {
			Double p = (Double) ltimbre.get(i);
			timbre[i] = p;
		}
	}
	
	
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Start: " + getStart() + " Dur: " + getDuration() + "\n");
        sb.append("Loudness  start: " + getLoudnessStart() +
                             " max " + getLoudnessMax() + " at " + getLoudnessMaxTime() +"\n");

        sb.append("Pitches: ");
        for (int i = 0; i < pitches.length; i++) {
            sb.append(pitches[i] + " ");
        }
        sb.append("\n");

        sb.append("Timbre: ");
        for (int i = 0; i < timbre.length; i++) {
            sb.append(timbre[i] + " ");
        }
        sb.append("\n");

        return sb.toString();
    }


	/**
	 * @return the loudnessStart
	 */
	public double getLoudnessStart() {
		return loudnessStart;
	}

	/**
	 * @return the loudnessMaxTime
	 */
	public double getLoudnessMaxTime() {
		return loudnessMaxTime;
	}


	/**
	 * @return the loudnessMax
	 */
	public double getLoudnessMax() {
		return loudnessMax;
	}

	/**
	 * @return the pitches
	 */
	public double[] getPitches() {
		return pitches;
	}

	/**
	 * @return the timbre
	 */
	public double[] getTimbre() {
		return timbre;
	}

}
