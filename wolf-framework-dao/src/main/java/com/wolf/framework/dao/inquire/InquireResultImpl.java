package com.wolf.framework.dao.inquire;

import com.wolf.framework.dao.Entity;
import com.wolf.framework.dao.InquireResult;
import java.util.List;

/**
 *
 * @author aladdin
 * @param <T>
 */
public class InquireResultImpl<T extends Entity> implements InquireResult {

    private final long total;
    private final long pageSize;
    private final long pageNum;
    private final long pageIndex;
    private final List<T> resultList;

    public InquireResultImpl(long total, long pageSize, long pageNum, long pageIndex, List<T> resultList) {
        this.total = total;
        this.pageSize = pageSize;
        this.pageNum = pageNum;
        this.pageIndex = pageIndex;
        this.resultList = resultList;
    }
    
    @Override
    public long getTotal() {
        return this.total;
    }

    @Override
    public long getPageSize() {
        return this.pageSize;
    }

    @Override
    public List getResultList() {
        return this.resultList;
    }

    @Override
    public boolean isEmpty() {
        return this.resultList.isEmpty();
    }

    @Override
    public long getPageNum() {
        return this.pageNum;
    }

    @Override
    public long getPageIndex() {
        return this.pageIndex;
    }
}
