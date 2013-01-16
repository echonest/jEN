package com.echonest.api.v4;

import java.util.ArrayList;


public class PagedList<T> extends ArrayList<T> {

    private static final long serialVersionUID = -3406237854830722566L;
    
    private int start = 0;
    private int total = 0;
    
    
    public PagedList(int start, int total) {
        super();
        this.start = start;
        this.total = total;
    }
    
    /**
     * @return the start
     */
    public int getStart() {
        return start;
    }
    /**
     * @return the total
     */
    public int getTotal() {
        return total;
    }
    
    

}
