package com.wolf.framework.service.request;

import com.wolf.framework.request.Request;

/**
 *
 * @author jianying9
 */
public class ListRequestImpl extends ObjectRequestImpl implements ListRequest {

    private final long nextIndex;
    private final int nextSize;

    public ListRequestImpl(Request request) {
        super(request);
        Object nextIndexTemp = this.getValue("nextIndex");
        long index = 0;
        if (nextIndexTemp != null && String.class.isInstance(nextIndexTemp)) {
            index = Long.parseLong((String) nextIndexTemp);
        }
        this.nextIndex = index;
        Object nextSizeTemp = this.getValue("nextSize");
        int size = 6;
        if (nextSizeTemp != null && String.class.isInstance(nextSizeTemp)) {
            size = Integer.parseInt((String) nextSizeTemp);
        }
        this.nextSize = size;
    }

    @Override
    public long getNextIndex() {
        return this.nextIndex;
    }

    @Override
    public int getNextSize() {
        return this.nextSize;
    }
}
