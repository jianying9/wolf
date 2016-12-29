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
        Long index = super.getLongValue("nextIndex");
        if(index == null) {
            index = 0l;
        }
        this.nextIndex = index;
        Long size = super.getLongValue("nextSize");
        if(size == null) {
            size = 6l;
        }
        this.nextSize = size.intValue();
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
