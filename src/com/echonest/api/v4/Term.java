package com.echonest.api.v4;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Term {
    private String name;
    private double weight;
    private double frequency;

    Term(String name, double weight, double frequency) {
        this.name = name;
        this.weight = weight;
        this.frequency = frequency;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the weight
     */
    public double getWeight() {
        return weight;
    }

    /**
     * @return the frequency
     */
    public double getFrequency() {
        return frequency;
    }

    public static void sortByWeight(List<Term> terms) {
        Collections.sort(terms, new Comparator<Term>() {

            @Override
            public int compare(Term o1, Term o2) {
                if (o1.getWeight() > o2.getWeight()) {
                    return 1;
                } else if (o1.getWeight() < o2.getWeight()) {
                    return -1;
                } else {
                    return 0;
                }
            }

        });
        Collections.reverse(terms);
    }

    public static void sortByFrequency(List<Term> terms) {
        Collections.sort(terms, new Comparator<Term>() {

            @Override
            public int compare(Term o1, Term o2) {
                if (o1.getFrequency() > o2.getFrequency()) {
                    return 1;
                } else if (o1.getFrequency() < o2.getFrequency()) {
                    return -1;
                } else {
                    return 0;
                }
            }

        });
        Collections.reverse(terms);
    }

}
