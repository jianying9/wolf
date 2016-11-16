package com.wolf.framework.service.request;

import com.wolf.framework.request.Request;

/**
 *
 * @author jianying9
 */
public class ListRequestImpl extends ObjectRequestImpl implements ListRequest {
    
    private final String nextIndex;
    private final int nextSize;

    public ListRequestImpl(Request request) {
        super(request);
        String nextIndexTemp = this.getParameter("nextIndex");
        if(nextIndexTemp == null) {
            nextIndexTemp = "-1";
        }
        this.nextIndex = nextIndexTemp;
        String nextSizeTemp = this.getParameter("nextSize");
        if(nextSizeTemp == null) {
            nextSizeTemp = "6";
        }
        this.nextSize = Integer.parseInt(nextSizeTemp);
    }

    @Override
    public String getNextIndex() {
        return this.nextIndex;
    }

    @Override
    public int getNextSize() {
        return this.nextSize;
    }
}
